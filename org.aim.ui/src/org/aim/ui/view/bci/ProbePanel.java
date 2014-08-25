package org.aim.ui.view.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.aim.ui.manager.ClientManager;

public class ProbePanel extends JPanel {

	private JPanel probeItemPanel;

	private List<String> probes = new ArrayList<String>();

	public ProbePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0 };
		setLayout(gridBagLayout);

		probeItemPanel = new JPanel();
		GridBagConstraints gbc_probeItemPanel = new GridBagConstraints();
		gbc_probeItemPanel.anchor = GridBagConstraints.NORTH;
		gbc_probeItemPanel.insets = new Insets(0, 0, 5, 0);
		gbc_probeItemPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_probeItemPanel.gridx = 0;
		gbc_probeItemPanel.gridy = 0;
		add(probeItemPanel, gbc_probeItemPanel);
		probeItemPanel.setLayout(new GridLayout(0, 1, 0, 0));

		ProbeItem panel_1 = new ProbeItem();
		probeItemPanel.add(panel_1);

		JButton btnNewButton = new JButton("Add Probe");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				probes.add("");
				updateProbes();
				revalidate();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 1;
		add(btnNewButton, gbc_btnNewButton);

		updateProbes();
	}

	public void updateProbes() {
		List<String> clientProbes ;
		if (ClientManager.SINGLETON().isConnected()) {
			clientProbes = ClientManager.SINGLETON().getProbes();
		} else {
			clientProbes = new ArrayList<String>();
		}
		
		probeItemPanel.removeAll();
		probeItemPanel.setLayout(new GridLayout(probes.size(), 1, 0, 0));

		int id = 0;
		for (String p : probes) {
			probeItemPanel.add(new ProbeItem(this, id++, p, clientProbes));
		}
	}

	public void updateProbe(int id, String newValue) {
		probes.set(id, newValue);
	}

	public void removeProbe(int index) {
		System.out.println("Remove " + index);
		probes.remove(index);
		updateProbes();
		revalidate();
	}
}
