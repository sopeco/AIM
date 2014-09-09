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

/**
 * Panel to specify a scope.
 * 
 * @author Marius Oehler
 *
 */
public class ScopePanel extends JPanel implements ActionListener {

	public static final String ALLOCATION_SCOPE = "Allocation Scope";
	public static final String CONSTRUCTOR_SCOPE = "Constructor Scope";
	public static final String METHOD_SCOPE = "Method Scope";

	private static final int INSET_VALUE = 5;

	/** */
	private static final long serialVersionUID = 1L;

	private JCheckBox checkBoxTrace;
	private JComboBox<String> comboBox;
	private JLabel lblSettings;
	private JLabel lblTraceScope;
	private ItemListPanel lpScopeSettings;

	/**
	 * Constructor.
	 */
	public ScopePanel() {
		setBorder(new TitledBorder(null, "Scope", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblType = new JLabel("Type:");
		GridBagConstraints gbcLblType = new GridBagConstraints();
		gbcLblType.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblType.anchor = GridBagConstraints.WEST;
		gbcLblType.gridx = 0;
		gbcLblType.gridy = 0;
		add(lblType, gbcLblType);

		comboBox = new JComboBox<String>();
		comboBox.addActionListener(this);
		GridBagConstraints gbcComboBox = new GridBagConstraints();
		gbcComboBox.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbcComboBox.gridx = 1;
		gbcComboBox.gridy = 0;
		add(comboBox, gbcComboBox);

		lblTraceScope = new JLabel("Trace Scope:");
		GridBagConstraints gbcLblTraceScope = new GridBagConstraints();
		gbcLblTraceScope.anchor = GridBagConstraints.WEST;
		gbcLblTraceScope.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblTraceScope.gridx = 0;
		gbcLblTraceScope.gridy = 1;
		add(lblTraceScope, gbcLblTraceScope);

		checkBoxTrace = new JCheckBox("");
		GridBagConstraints gbcCheckBoxTrace = new GridBagConstraints();
		gbcCheckBoxTrace.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcCheckBoxTrace.anchor = GridBagConstraints.WEST;
		gbcCheckBoxTrace.gridx = 1;
		gbcCheckBoxTrace.gridy = 1;
		add(checkBoxTrace, gbcCheckBoxTrace);

		lblSettings = new JLabel("Method Patterns:");
		GridBagConstraints gbcLblSettings = new GridBagConstraints();
		gbcLblSettings.anchor = GridBagConstraints.NORTH;
		gbcLblSettings.insets = new Insets(INSET_VALUE, 0, 0, INSET_VALUE);
		gbcLblSettings.gridx = 0;
		gbcLblSettings.gridy = 2;
		add(lblSettings, gbcLblSettings);

		lpScopeSettings = new ItemListPanel();
		GridBagConstraints gbcPanel = new GridBagConstraints();
		gbcPanel.fill = GridBagConstraints.BOTH;
		gbcPanel.gridx = 1;
		gbcPanel.gridy = 2;
		add(lpScopeSettings, gbcPanel);

		// /
		lpScopeSettings
				.setValidationPattern("([a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*\\.)*[a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*");

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

	/**
	 * Returns the selected scope.
	 * 
	 * @return scope name
	 */
	public String getSelectedScope() {
		return (String) comboBox.getSelectedItem();
	}

	/**
	 * Returns an array containing the specified settings.
	 * 
	 * @return string array
	 */
	public String[] getSettings() {
		if (lblSettings.isVisible()) {
			return lpScopeSettings.getValues().toArray(new String[0]);
		} else {
			return new String[0];
		}
	}

	private void hideSettings() {
		lblSettings.setVisible(false);
		lpScopeSettings.setVisible(false);
	}

	private void hideTraceOption() {
		lblTraceScope.setVisible(false);
		checkBoxTrace.setVisible(false);
	}

	/**
	 * Returns true if the selected scope was marked as a trace scope. It's not
	 * checked if the selected scope can be a trace scope!
	 * 
	 * @return boolean whether the scope is a trace scope.
	 */
	public boolean isTraceScope() {
		return checkBoxTrace.isVisible() && checkBoxTrace.isSelected();
	}

	/**
	 * Sets the selected scope to the given value. If the given scope doesn't
	 * exist in the list, it will be added.
	 * 
	 * @param scope
	 *            - scope to select
	 */
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

	/**
	 * Loads the given array into the input fields of the scope settings.
	 * 
	 * @param scopeSettings
	 *            - strings to load
	 */
	public void setScopeSettings(String[] scopeSettings) {
		for (String s : scopeSettings) {
			lpScopeSettings.addItem(s);
		}
	}

	/**
	 * Sets whether the scope is a trace scope.
	 * 
	 * @param isTraceScope
	 *            - is the scope a trace scope
	 */
	public void setTraceScope(boolean isTraceScope) {
		checkBoxTrace.setSelected(isTraceScope);
	}

	private void showSettings() {
		lblSettings.setVisible(true);
		lpScopeSettings.setVisible(true);
	}

	private void showTraceOption() {
		lblTraceScope.setVisible(true);
		checkBoxTrace.setVisible(true);
	}

}
