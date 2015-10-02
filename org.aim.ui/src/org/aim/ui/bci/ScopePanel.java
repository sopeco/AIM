package org.aim.ui.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.aim.ui.components.ExtendedComboBox;
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
	public static final String CONSTRUCTOR_SCOPE = "org.aim.artifacts.scopes.ConstructorScope";
	public static final String METHOD_SCOPE = "org.aim.artifacts.scopes.MethodScope";
	public static final String MEMORY_SCOPE = "org.aim.artifacts.scopes.MemoryScope";
	public static final String SYNCHRONIZED_SCOPE = "org.aim.artifacts.scopes.SynchronizedScope";

	private static final int INSET_VALUE = 5;

	/** */
	private static final long serialVersionUID = 1L;

	private final JCheckBox checkBoxTrace;
	private final ExtendedComboBox comboBox;
	private final JLabel lblSettings;
	private final JLabel lblTraceScope;
	private final ItemListPanel lpScopeSettings;

	private final Map<String, String> scopeMapping = new HashMap<String, String>();
	private final InstrumentationEntityWizard wizard;

	/**
	 * Constructor.
	 * 
	 * @param wizard
	 *            parent
	 */
	public ScopePanel(final InstrumentationEntityWizard wizard) {
		this.wizard = wizard;

		setBorder(new TitledBorder(null, "Scope", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JLabel lblType = new JLabel("Type:");
		final GridBagConstraints gbcLblType = new GridBagConstraints();
		gbcLblType.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblType.anchor = GridBagConstraints.WEST;
		gbcLblType.gridx = 0;
		gbcLblType.gridy = 0;
		add(lblType, gbcLblType);

		comboBox = new ExtendedComboBox();
		comboBox.addActionListener(this);
		final GridBagConstraints gbcComboBox = new GridBagConstraints();
		gbcComboBox.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbcComboBox.gridx = 1;
		gbcComboBox.gridy = 0;
		add(comboBox, gbcComboBox);

		lblTraceScope = new JLabel("Trace Scope:");
		final GridBagConstraints gbcLblTraceScope = new GridBagConstraints();
		gbcLblTraceScope.anchor = GridBagConstraints.WEST;
		gbcLblTraceScope.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblTraceScope.gridx = 0;
		gbcLblTraceScope.gridy = 1;
		add(lblTraceScope, gbcLblTraceScope);

		checkBoxTrace = new JCheckBox("");
		final GridBagConstraints gbcCheckBoxTrace = new GridBagConstraints();
		gbcCheckBoxTrace.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcCheckBoxTrace.anchor = GridBagConstraints.WEST;
		gbcCheckBoxTrace.gridx = 1;
		gbcCheckBoxTrace.gridy = 1;
		add(checkBoxTrace, gbcCheckBoxTrace);

		lblSettings = new JLabel("Method Patterns:");
		final GridBagConstraints gbcLblSettings = new GridBagConstraints();
		gbcLblSettings.anchor = GridBagConstraints.NORTH;
		gbcLblSettings.insets = new Insets(INSET_VALUE, 0, 0, INSET_VALUE);
		gbcLblSettings.gridx = 0;
		gbcLblSettings.gridy = 2;
		add(lblSettings, gbcLblSettings);

		lpScopeSettings = new ItemListPanel();
		final GridBagConstraints gbcPanel = new GridBagConstraints();
		gbcPanel.fill = GridBagConstraints.BOTH;
		gbcPanel.gridx = 1;
		gbcPanel.gridy = 2;
		add(lpScopeSettings, gbcPanel);

		// /
		lpScopeSettings
				.setValidationPattern("([a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*\\.)*[a-zA-Z_$\\*\\?][a-zA-Z\\d_$\\*\\?]*");

		if (ClientManager.instance().isConnected()) {
			final List<String> customScopes = ClientManager.instance().getCustomScopes();
			if (!customScopes.isEmpty()) {
				comboBox.addDelimiter("Scopes");
				for (final String i : customScopes) {
					scopeMapping.put(abbreviateString(i), i);
					comboBox.addItem(abbreviateString(i));
				}
			}
			
			final List<String> apiScopes = ClientManager.instance().getApiScopes();
			if (!apiScopes.isEmpty()) {
				comboBox.addDelimiter("API Scopes");
				for (final String i : apiScopes) {
					scopeMapping.put(abbreviateString(i), i);
					comboBox.addItem(abbreviateString(i));
				}
			}
		}
	}

	private String abbreviateString(final String scopeName) {
		final int lastIndexOf = scopeName.lastIndexOf('.');
		if (lastIndexOf >= 0) {
			return scopeName.substring(lastIndexOf + 1);
		}
		return scopeName;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		lpScopeSettings.removeItems();

		wizard.filterProbes();

		final String scp = (String) comboBox.getSelectedItem();
		if (scp != null && (scp.equals(abbreviateString(METHOD_SCOPE)) || scp.equals(abbreviateString(CONSTRUCTOR_SCOPE)))) {
			showSettings();

			if (!scp.equals(abbreviateString(ALLOCATION_SCOPE))) {
				showTraceOption();
			}

			String lblText;
			if (scp.equals(abbreviateString(METHOD_SCOPE))) {
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
		return scopeMapping.get(comboBox.getSelectedItem());
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
	public void setScope(final String scope) {
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
	public void setScopeSettings(final String[] scopeSettings) {
		for (final String s : scopeSettings) {
			lpScopeSettings.addItem(s);
		}
	}

	/**
	 * Sets whether the scope is a trace scope.
	 * 
	 * @param isTraceScope
	 *            - is the scope a trace scope
	 */
	public void setTraceScope(final boolean isTraceScope) {
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
