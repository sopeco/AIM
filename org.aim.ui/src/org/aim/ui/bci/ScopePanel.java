package org.aim.ui.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.aim.ui.components.ItemListPanel;
import org.aim.ui.manager.ClientManager;

public class ScopePanel extends JPanel implements ActionListener {

	private static final String ALLOCATION_SCOPE = "Allocation Scope";

	private static final String CONSTRUCTOR_SCOPE = "Constructor Scope";

	private static final String METHOD_SCOPE = "Method Scope";

	/** */
	private static final long serialVersionUID = 1L;

	private JComboBox<String> comboBox;
	private JLabel lblSettings;
	private ItemListPanel panel;

	public ScopePanel() {
		setBorder(new TitledBorder(null, "Scope", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblType = new JLabel("Type:");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.anchor = GridBagConstraints.WEST;
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 0;
		add(lblType, gbc_lblType);

		comboBox = new JComboBox<String>();
		comboBox.addActionListener(this);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		add(comboBox, gbc_comboBox);

		lblSettings = new JLabel("Method Patterns:");
		GridBagConstraints gbc_lblSettings = new GridBagConstraints();
		gbc_lblSettings.anchor = GridBagConstraints.NORTH;
		gbc_lblSettings.insets = new Insets(5, 0, 0, 5);
		gbc_lblSettings.gridx = 0;
		gbc_lblSettings.gridy = 1;
		add(lblSettings, gbc_lblSettings);

		panel = new ItemListPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);

		// /
		panel.setValidationPattern("([a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*\\.)*[a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*");

		comboBox.addItem("Trace Scope");
		comboBox.addItem(METHOD_SCOPE);
		comboBox.addItem(CONSTRUCTOR_SCOPE);
		comboBox.addItem(ALLOCATION_SCOPE);

		if (ClientManager.SINGLETON().isConnected()) {
			for (String i : ClientManager.SINGLETON().getScopes()) {
				comboBox.addItem(i);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		panel.removeItems();
		String scp = (String) comboBox.getSelectedItem();

		if (scp.equals(METHOD_SCOPE) || scp.equals(CONSTRUCTOR_SCOPE) || scp.equals(ALLOCATION_SCOPE)) {
			showSettings();

			String lblText;
			if (scp.equals(METHOD_SCOPE)) {
				lblText = "Method Patterns:";
			} else {
				lblText = "Target Classes:";
			}

			lblSettings.setText(lblText);
		} else {
			hideSettings();
		}
	}

	private void hideSettings() {
		lblSettings.setVisible(false);
		panel.setVisible(false);
	}

	private void showSettings() {
		lblSettings.setVisible(true);
		panel.setVisible(true);
	}

}
