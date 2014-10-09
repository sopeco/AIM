package org.aim.ui.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.aim.ui.entities.RawInstrumentationEntity;
import org.aim.ui.manager.Core;

/**
 * An wizard to create an InstrumentationEntity.
 * 
 * @author Marius Oehler
 *
 */
public class InstrumentationEntityWizard extends JDialog implements ActionListener {

	/**  */
	private static final long serialVersionUID = 1L;
	private static final int INSET_VALUE = 5;

	private JButton btnSave;
	private ProbePanel probePanel;
	private RawInstrumentationEntity rawEntity;
	private RestrictionPanel restrictionPanel;
	private ScopePanel scpPanel;

	/**
	 * Constructor.
	 */
	public InstrumentationEntityWizard() {
		setTitle("Add Instrumentation Entity");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		scpPanel = new ScopePanel(this);
		GridBagConstraints gbcPanel = new GridBagConstraints();
		gbcPanel.gridwidth = 2;
		gbcPanel.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcPanel.anchor = GridBagConstraints.NORTH;
		gbcPanel.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel.gridx = 0;
		gbcPanel.gridy = 0;
		getContentPane().add(scpPanel, gbcPanel);

		probePanel = new ProbePanel();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		gbcPanel1.gridwidth = 2;
		gbcPanel1.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 1;
		getContentPane().add(probePanel, gbcPanel1);

		restrictionPanel = new RestrictionPanel();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		gbcPanel2.gridwidth = 2;
		gbcPanel2.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 2;
		getContentPane().add(restrictionPanel, gbcPanel2);

		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		GridBagConstraints gbcBtnNewButton = new GridBagConstraints();
		gbcBtnNewButton.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnNewButton.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcBtnNewButton.gridx = 0;
		// CHECKSTYLE:OFF
		gbcBtnNewButton.gridy = 3;
		// CHECKSTYLE:ON
		getContentPane().add(btnSave, gbcBtnNewButton);

		JButton btnNewButton1 = new JButton("Cancel");
		btnNewButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		GridBagConstraints gbcBtnNewButton1 = new GridBagConstraints();
		gbcBtnNewButton1.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcBtnNewButton.insets = new Insets(0, 0, INSET_VALUE, 0);
		// CHECKSTYLE:OFF
		gbcBtnNewButton1.gridy = 3;
		// CHECKSTYLE:ON
		gbcBtnNewButton1.gridx = 1;
		gbcBtnNewButton1.anchor = GridBagConstraints.SOUTHEAST;
		getContentPane().add(btnNewButton1, gbcBtnNewButton1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSave) {
			if (rawEntity != null) {
				Core.instance().updateRawInstrumentationEntity(rawEntity, getRawInstrumentationEntity());
			} else {
				Core.instance().addRawInstrumentationEntity(getRawInstrumentationEntity());
			}
			dispose();
		}
	}

	/**
	 * Builds the RawInstrumentationEntity out of the wizard's inputfields.
	 * 
	 * @return the {@link RawInstrumentationEntity}
	 */
	public RawInstrumentationEntity getRawInstrumentationEntity() {
		RawInstrumentationEntity entity = new RawInstrumentationEntity();
		entity.setScope(scpPanel.getSelectedScope());
		entity.setTraceScope(scpPanel.isTraceScope());
		entity.setScopeSettings(scpPanel.getSettings());
		entity.setProbes(probePanel.getProbes());
		entity.setExcludedModifiers(restrictionPanel.getExcludedModifiers());
		entity.setIncludedModifiers(restrictionPanel.getIncludedModifiers());
		entity.setExcludedPackages(restrictionPanel.getExcludedPackages());
		entity.setIncludedPackages(restrictionPanel.getIncludedPackages());
		return entity;
	}

	/**
	 * Loads an {@link RawInstrumentationEntity} into the wizard.
	 * 
	 * @param entity
	 *            - the {@link RawInstrumentationEntity} to load
	 */
	public void setRawInstrumentationEntity(RawInstrumentationEntity entity) {
		rawEntity = entity;
		scpPanel.setScope(entity.getScope());
		scpPanel.setTraceScope(true);
		scpPanel.setScopeSettings(entity.getScopeSettings());
		probePanel.setProbes(entity.getProbes());
		restrictionPanel.setExcludedPackages(entity.getExcludedPackages());
		restrictionPanel.setIncludedPackages(entity.getIncludedPackages());
		restrictionPanel.setExcludedModifiers(entity.getExcludedModifiers());
		restrictionPanel.setIncludedModifiers(entity.getIncludedModifiers());
	}

	/**
	 * Filters the list of probes thereby only probes consistent with the
	 * selected scope are shown.
	 */
	public void filterProbes() {
		probePanel.filterProbes(scpPanel.getSelectedScope());
	}
}
