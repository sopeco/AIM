package org.aim.ui.manager;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.aim.description.InstrumentationDescription;
import org.aim.description.restrictions.Restriction;
import org.aim.ui.IDBuilder;
import org.aim.ui.bci.InstrumentationEntityWizard;
import org.aim.ui.entities.RawInstrumentationEntity;
import org.aim.ui.view.MainView;

/**
 * Major part of the applications logic.
 * 
 * @author Marius Oehler
 *
 */
public final class Core {

	private static final Dimension ENTITY_WIZARD_SIZE = new Dimension(400, 500);

	private static Core instance;

	/**
	 * Get the singleton instance of this class.
	 * 
	 * @return {@link Core} instance
	 */
	public static Core instance() {
		if (instance == null) {
			instance = new Core();
		}
		return instance;
	}

	private List<RawInstrumentationEntity> currentInstrumentEntities;

	private Core() {
		currentInstrumentEntities = new ArrayList<RawInstrumentationEntity>();
	}

	/**
	 * Adds a {@link RawInstrumentationEntity} to the application's set of
	 * entities.
	 * 
	 * @param entity
	 *            - entity to add
	 */
	public void addRawInstrumentationEntity(RawInstrumentationEntity entity) {
		currentInstrumentEntities.add(entity);
		MainView.instance().updateInstrumentEntities(currentInstrumentEntities);
	}

	/**
	 * Returns an {@link InstrumentationDescription} of the applications
	 * {@link RawInstrumentationEntity} and other settings like sampler and
	 * restrictions.
	 * 
	 * @return the built {@link InstrumentationDescription}
	 */
	public InstrumentationDescription buildInstrumentationDescription() {
		Restriction globalRestriction = MainView.instance().getGlobalRestriction();
		return IDBuilder.build(currentInstrumentEntities, globalRestriction);
	}

	/**
	 * Opens a dialog to edit the settings of the given
	 * {@link RawInstrumentationEntity}.
	 * 
	 * @param entity
	 *            - entity to edit
	 */
	public void editRawInstrumentationEntity(RawInstrumentationEntity entity) {
		InstrumentationEntityWizard dialog = new InstrumentationEntityWizard();
		dialog.setSize(ENTITY_WIZARD_SIZE);
		dialog.setModal(true);
		dialog.setLocationRelativeTo(MainView.instance());
		dialog.setRawInstrumentationEntity(entity);
		dialog.setVisible(true);
	}

	/**
	 * Exports a {@link RawInstrumentationEntity} to a XML file. The target file
	 * can be chosen by a file chooser.
	 * 
	 * @param entity
	 *            - entity to export
	 */
	public void exportRawInstrumentationEntityInFile(RawInstrumentationEntity entity) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
		chooser.setFileFilter(filter);
		chooser.setApproveButtonText("Export");
		int returnVal = chooser.showOpenDialog(MainView.instance());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File outFile = chooser.getSelectedFile();
			if (!outFile.getAbsolutePath().endsWith(".xml")) {
				outFile = new File(outFile.getAbsolutePath() + ".xml");
			}

			try {
				JAXBContext context = JAXBContext.newInstance(RawInstrumentationEntity.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				m.marshal(entity, outFile);
			} catch (JAXBException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Imports a {@link RawInstrumentationEntity} from a XML file. The source
	 * file can be chosen by a file chooser.
	 */
	public void importRawInstrumentationEntityFromFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
		chooser.setFileFilter(filter);
		chooser.setApproveButtonText("Export");
		int returnVal = chooser.showOpenDialog(MainView.instance());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File inFile = chooser.getSelectedFile();

			try {
				JAXBContext context = JAXBContext.newInstance(RawInstrumentationEntity.class);
				Unmarshaller um = context.createUnmarshaller();
				RawInstrumentationEntity entity = (RawInstrumentationEntity) um.unmarshal(new FileReader(inFile));

				addRawInstrumentationEntity(entity);
			} catch (JAXBException e) {
				throw new RuntimeException(e);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Downloads the dataset of the connected agent.
	 */
	public void downloadDataset() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setApproveButtonText("Download");
		int returnVal = chooser.showOpenDialog(MainView.instance());
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File targetDir = chooser.getSelectedFile();
			ClientManager.instance().downloadDataset(targetDir);
		}
	}

	/**
	 * Induce the instrumentation of the connected agent.
	 */
	public void instrument() {
		ClientManager.instance().instrument(buildInstrumentationDescription());
	}

	/**
	 * Removes a {@link RawInstrumentationEntity} of the aplication's set of
	 * entities.
	 * 
	 * @param entity
	 *            - entity to remove
	 */
	public void removeRawInstrumentationEntity(RawInstrumentationEntity entity) {
		currentInstrumentEntities.remove(entity);
		MainView.instance().updateInstrumentEntities(currentInstrumentEntities);
	}

	/**
	 * Induce the uninstrumentation of the connected agent.
	 */
	public void uninstrument() {
		ClientManager.instance().uninstrument();
	}

	/**
	 * Replaces the <code>oldEntity</code> with the <code>newEntity</code> in
	 * the application's set of entites.
	 * 
	 * @param oldEntity
	 *            - entity to replace
	 * @param newEntity
	 *            - entity to store
	 */
	public void updateRawInstrumentationEntity(RawInstrumentationEntity oldEntity, RawInstrumentationEntity newEntity) {
		for (int i = 0; i < currentInstrumentEntities.size(); i++) {
			if (currentInstrumentEntities.get(i) == oldEntity) {
				currentInstrumentEntities.set(i, newEntity);
			}
		}
		MainView.instance().updateInstrumentEntities(currentInstrumentEntities);
	}
}
