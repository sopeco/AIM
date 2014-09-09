package org.aim.ui.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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

		if (ClientManager.SINGLETON().isConnected()) {
			panel.setPredefinedValues(ClientManager.SINGLETON().getProbes());
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
}
