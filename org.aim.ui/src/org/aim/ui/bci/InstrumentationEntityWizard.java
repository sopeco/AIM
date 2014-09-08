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

public class InstrumentationEntityWizard extends JDialog implements ActionListener {
	/**  */
	private static final long serialVersionUID = 1L;
	private JButton btnSave;
	private ScopePanel scpPanel;
	private ProbePanel probePanel;
	private RestrictionPanel restrictionPanel;
	private RawInstrumentationEntity rawEntity;

	public InstrumentationEntityWizard() {
		setTitle("Add Instrumentation Entity");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		scpPanel = new ScopePanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(scpPanel, gbc_panel);

		probePanel = new ProbePanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		getContentPane().add(probePanel, gbc_panel_1);

		restrictionPanel = new RestrictionPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		getContentPane().add(restrictionPanel, gbc_panel_2);

		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.SOUTHEAST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 3;
		getContentPane().add(btnSave, gbc_btnNewButton);

		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.anchor = GridBagConstraints.SOUTHEAST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 3;
		getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);
	}

	public RawInstrumentationEntity getRawInstrumentationEntity() {
		RawInstrumentationEntity entity = new RawInstrumentationEntity();
		entity.setScope(scpPanel.getSelectedScope());
		entity.setTraceScope(scpPanel.isTraceScope());
		entity.setScopeSettings(scpPanel.getSettings());
		entity.setProbes(probePanel.getProbes());
		entity.setExcModifiers(restrictionPanel.getExcludedModifiers());
		entity.setIncModifiers(restrictionPanel.getIncludedModifiers());
		entity.setExcPackages(restrictionPanel.getExcludedPackages());
		entity.setIncPackages(restrictionPanel.getIncludedPackages());
		return entity;
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

	public void setRawInstrumentationEntity(RawInstrumentationEntity entity) {
		rawEntity = entity;
		scpPanel.setScope(entity.getScope());
		scpPanel.setTraceScope(true);
		scpPanel.setScopeSettings(entity.getScopeSettings());
		probePanel.setProbes(entity.getProbes());
		restrictionPanel.setExcludedPackages(entity.getExcPackages());
		restrictionPanel.setIncludedPackages(entity.getIncPackages());
		restrictionPanel.setExcludedModifiers(entity.getExcModifiers());
		restrictionPanel.setIncludedModifiers(entity.getIncModifiers());
	}
}
