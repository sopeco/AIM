package org.aim.ui.manager;

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

public final class Core {

	private static Core instance;

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

	public void instrument() {
		ClientManager.instance().instrument(buildInstrumentationDescription());
	}

	public void uninstrument() {
		ClientManager.instance().uninstrument();
	}

	public void addRawInstrumentationEntity(RawInstrumentationEntity entity) {
		currentInstrumentEntities.add(entity);
		MainView.SINGLETON().updateInstrumentEntities(currentInstrumentEntities);
	}

	public InstrumentationDescription buildInstrumentationDescription() {
		Restriction globalRestriction = MainView.SINGLETON().getGlobalRestriction();
		return IDBuilder.build(currentInstrumentEntities, globalRestriction);
	}

	public void removeRawInstrumentationEntity(RawInstrumentationEntity entity) {
		currentInstrumentEntities.remove(entity);
		MainView.SINGLETON().updateInstrumentEntities(currentInstrumentEntities);
	}

	public void editRawInstrumentationEntity(RawInstrumentationEntity entity) {
		InstrumentationEntityWizard dialog = new InstrumentationEntityWizard();
		dialog.setSize(400, 500);
		dialog.setModal(true);
		dialog.setLocationRelativeTo(MainView.SINGLETON());
		dialog.setRawInstrumentationEntity(entity);
		dialog.setVisible(true);
	}

	public void updateRawInstrumentationEntity(RawInstrumentationEntity oldEntity, RawInstrumentationEntity newEntity) {
		for (int i = 0; i < currentInstrumentEntities.size(); i++) {
			if (currentInstrumentEntities.get(i) == oldEntity) {
				currentInstrumentEntities.set(i, newEntity);
			}
		}
		MainView.SINGLETON().updateInstrumentEntities(currentInstrumentEntities);
	}

	public void exportRawInstrumentationEntityInFile(RawInstrumentationEntity entity) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
		chooser.setFileFilter(filter);
		chooser.setApproveButtonText("Export");
		int returnVal = chooser.showOpenDialog(MainView.SINGLETON());
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

	public void importRawInstrumentationEntityFromFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
		chooser.setFileFilter(filter);
		chooser.setApproveButtonText("Export");
		int returnVal = chooser.showOpenDialog(MainView.SINGLETON());
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
}
