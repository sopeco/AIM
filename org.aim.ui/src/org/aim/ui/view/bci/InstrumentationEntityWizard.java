package org.aim.ui.view.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.aim.ui.manager.ClientManager;

public class InstrumentationEntityWizard extends JDialog {
	private JComboBox<String> scopeBox;

	public InstrumentationEntityWizard() {
		setSize(400, 300);
		setTitle("Add Instrumentation Entity");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel lblScope = new JLabel("Scope:");
		GridBagConstraints gbc_lblScope = new GridBagConstraints();
		gbc_lblScope.anchor = GridBagConstraints.WEST;
		gbc_lblScope.insets = new Insets(5, 5, 5, 5);
		gbc_lblScope.gridx = 0;
		gbc_lblScope.gridy = 0;
		getContentPane().add(lblScope, gbc_lblScope);

		scopeBox = new JComboBox<String>();
		GridBagConstraints gbc_scopeBox = new GridBagConstraints();
		gbc_scopeBox.gridwidth = 2;
		gbc_scopeBox.insets = new Insets(5, 0, 5, 5);
		gbc_scopeBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_scopeBox.gridx = 1;
		gbc_scopeBox.gridy = 0;
		getContentPane().add(scopeBox, gbc_scopeBox);

		JLabel lblProbes = new JLabel("Probes:");
		GridBagConstraints gbc_lblProbes = new GridBagConstraints();
		gbc_lblProbes.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblProbes.insets = new Insets(0, 5, 5, 5);
		gbc_lblProbes.gridx = 0;
		gbc_lblProbes.gridy = 1;
		getContentPane().add(lblProbes, gbc_lblProbes);

		ProbePanel panel = new ProbePanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		getContentPane().add(panel, gbc_panel);

		JLabel lblNewLabel = new JLabel("Restrictions:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		getContentPane().add(panel_1, gbc_panel_1);
		
		JButton btnNewButton = new JButton("Save");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.EAST;
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 3;
		getContentPane().add(btnNewButton, gbc_btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Cancel");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridx = 2;
		gbc_btnNewButton_1.gridy = 3;
		getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);
		
		updateFields();
	}

	public void updateFields() {
		scopeBox.removeAllItems();

		List<String> scopes = ClientManager.SINGLETON().getScopes();

		for (String s : scopes) {
			scopeBox.addItem(s);

		}
	}

}
