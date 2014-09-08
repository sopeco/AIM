package org.aim.ui.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.aim.ui.components.ItemListPanel;
import org.aim.ui.manager.ClientManager;

public class ScopePanel extends JPanel implements ActionListener {

	public static final String ALLOCATION_SCOPE = "Allocation Scope";
	public static final String CONSTRUCTOR_SCOPE = "Constructor Scope";
	public static final String METHOD_SCOPE = "Method Scope";

	/** */
	private static final long serialVersionUID = 1L;

	private JComboBox<String> comboBox;
	private JLabel lblSettings;
	private ItemListPanel lpScopeSettings;
	private JLabel lblTraceScope;
	private JCheckBox checkBoxTrace;

	public ScopePanel() {
		setBorder(new TitledBorder(null, "Scope", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
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

		lblTraceScope = new JLabel("Trace Scope:");
		GridBagConstraints gbc_lblTraceScope = new GridBagConstraints();
		gbc_lblTraceScope.anchor = GridBagConstraints.WEST;
		gbc_lblTraceScope.insets = new Insets(0, 0, 5, 5);
		gbc_lblTraceScope.gridx = 0;
		gbc_lblTraceScope.gridy = 1;
		add(lblTraceScope, gbc_lblTraceScope);

		checkBoxTrace = new JCheckBox("");
		GridBagConstraints gbc_checkBoxTrace = new GridBagConstraints();
		gbc_checkBoxTrace.insets = new Insets(0, 0, 5, 0);
		gbc_checkBoxTrace.anchor = GridBagConstraints.WEST;
		gbc_checkBoxTrace.gridx = 1;
		gbc_checkBoxTrace.gridy = 1;
		add(checkBoxTrace, gbc_checkBoxTrace);

		lblSettings = new JLabel("Method Patterns:");
		GridBagConstraints gbc_lblSettings = new GridBagConstraints();
		gbc_lblSettings.anchor = GridBagConstraints.NORTH;
		gbc_lblSettings.insets = new Insets(5, 0, 0, 5);
		gbc_lblSettings.gridx = 0;
		gbc_lblSettings.gridy = 2;
		add(lblSettings, gbc_lblSettings);

		lpScopeSettings = new ItemListPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 2;
		add(lpScopeSettings, gbc_panel);

		// /
		lpScopeSettings.setValidationPattern("([a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*\\.)*[a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*");

		comboBox.addItem(METHOD_SCOPE);
		comboBox.addItem(CONSTRUCTOR_SCOPE);
		comboBox.addItem(ALLOCATION_SCOPE);

		if (ClientManager.SINGLETON().isConnected()) {
			for (String i : ClientManager.SINGLETON().getScopes()) {
				comboBox.addItem(i);
			}
		}
	}

	public String getSelectedScope() {
		return (String) comboBox.getSelectedItem();
	}

	public String[] getSettings() {
		if (lblSettings.isVisible()) {
			return lpScopeSettings.getValues().toArray(new String[0]);
		} else {
			return new String[0];
		}
	}

	public boolean isTraceScope() {
		return checkBoxTrace.isVisible() && checkBoxTrace.isSelected();
	}

	public void setScope(String scope) {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			if (comboBox.getItemAt(i).equals(scope)) {
				comboBox.setSelectedIndex(i);
				return;
			}
		}
		comboBox.addItem(scope);
		comboBox.setSelectedIndex(comboBox.getItemCount() - 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		lpScopeSettings.removeItems();
		String scp = (String) comboBox.getSelectedItem();

		if (scp.equals(METHOD_SCOPE) || scp.equals(CONSTRUCTOR_SCOPE) || scp.equals(ALLOCATION_SCOPE)) {
			showSettings();

			if (!scp.equals(ALLOCATION_SCOPE)) {
				showTraceOption();
			}

			String lblText;
			if (scp.equals(METHOD_SCOPE)) {
				lblText = "Method Patterns:";
			} else {
				lblText = "Target Classes:";
			}

			lblSettings.setText(lblText);
		} else {
			hideSettings();
			hideTraceOption();
		}
	}

	private void hideSettings() {
		lblSettings.setVisible(false);
		lpScopeSettings.setVisible(false);
	}

	private void showSettings() {
		lblSettings.setVisible(true);
		lpScopeSettings.setVisible(true);
	}

	private void hideTraceOption() {
		lblTraceScope.setVisible(false);
		checkBoxTrace.setVisible(false);
	}

	private void showTraceOption() {
		lblTraceScope.setVisible(true);
		checkBoxTrace.setVisible(true);
	}

	public void setTraceScope(boolean b) {
		checkBoxTrace.setSelected(true);
	}

	public void setScopeSettings(String[] scopeSettings) {
		for (String s : scopeSettings) {
			lpScopeSettings.addItem(s);
		}
	}

}
