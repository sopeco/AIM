package org.aim.ui.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.aim.ui.components.ItemListPanel;
import org.aim.ui.manager.ClientManager;

/**
 * Panel to define probes.
 * 
 * @author Marius Oehler
 *
 */
public class ProbePanel extends JPanel {
	/**  */
	private static final long serialVersionUID = 1L;

	private ItemListPanel panel;

	private Map<String, Set<String>> probeMapping;

	/**
	 * Constructor.
	 */
	public ProbePanel() {
		setBorder(new TitledBorder(null, "Probes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		panel = new ItemListPanel();
		GridBagConstraints gbcPanel = new GridBagConstraints();
		gbcPanel.fill = GridBagConstraints.BOTH;
		gbcPanel.gridx = 0;
		gbcPanel.gridy = 0;
		add(panel, gbcPanel);

		if (ClientManager.instance().isConnected()) {
			probeMapping = ClientManager.instance().getProbeMapping();
			panel.setPredefinedValues(ClientManager.instance().getProbes());
		}
	}

	/**
	 * Returns an array of the specified probes.
	 * 
	 * @return an array with strings, representing probes
	 */
	public String[] getProbes() {
		return panel.getValues().toArray(new String[0]);
	}

	/**
	 * Sets the given probes into the inputfields.
	 * 
	 * @param probes
	 *            - the probes to set
	 */
	public void setProbes(String[] probes) {
		for (String probe : probes) {
			panel.addItem(probe);
		}
	}

	/**
	 * Fitlers the probes. Show only probes which are consistent with the
	 * selected scope.
	 * 
	 * @param selectedScope
	 *            scope that is selected
	 * @param traceScope
	 *            scope is trace scope
	 */
	public void filterProbes(String selectedScope, boolean traceScope) {
		if (traceScope) {
			selectedScope = "org.aim.description.scopes.TraceScope";
		} else if (selectedScope.equals(ScopePanel.METHOD_SCOPE)) {
			selectedScope = "org.aim.description.scopes.MethodScope";
		} else if (selectedScope.equals(ScopePanel.CONSTRUCTOR_SCOPE)) {
			selectedScope = "org.aim.description.scopes.ConstructorScope";
		} else if (selectedScope.equals(ScopePanel.ALLOCATION_SCOPE)) {
			selectedScope = "org.aim.description.scopes.AllocationScope";
		} else if (selectedScope.equals(ScopePanel.MEMORY_SCOPE)) {
			selectedScope = "org.aim.description.scopes.MemoryScope";
		} else if (selectedScope.equals(ScopePanel.SYNCHRONIZED_SCOPE)) {
			selectedScope = "org.aim.description.scopes.SynchronizedScope";
		}

		List<String> filtered = new ArrayList<String>();
		for (String probe : probeMapping.keySet()) {
			if (probeMapping.get(probe).contains(selectedScope)) {
				filtered.add(probe);
			}
		}

		panel.setPredefinedValues(filtered, true);
	}
}
