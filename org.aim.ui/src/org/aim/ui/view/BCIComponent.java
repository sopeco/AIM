package org.aim.ui.view;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.GridLayout;

public class BCIComponent extends JPanel {
	
	public BCIComponent() {
		setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
//		setPreferredSize(new Dimension(500, 100));
//		setSize(500, 100);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {70, 200, 0};
		gridBagLayout.rowHeights = new int[]{14, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblTest = new JLabel("Scope");
		lblTest.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblTest = new GridBagConstraints();
		gbc_lblTest.insets = new Insets(5, 5, 5, 5);
		gbc_lblTest.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblTest.gridx = 0;
		gbc_lblTest.gridy = 0;
		add(lblTest, gbc_lblTest);
		
		JLabel lblNewLabel = new JLabel("New label");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(5, 0, 5, 0);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblProbes = new JLabel("Probes");
		lblProbes.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblProbes = new GridBagConstraints();
		gbc_lblProbes.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblProbes.insets = new Insets(5, 5, 5, 5);
		gbc_lblProbes.gridx = 0;
		gbc_lblProbes.gridy = 1;
		add(lblProbes, gbc_lblProbes);
		
		JPanel probePanel = new JPanel();
		GridBagConstraints gbc_probePanel = new GridBagConstraints();
		gbc_probePanel.insets = new Insets(0, 0, 5, 0);
		gbc_probePanel.anchor = GridBagConstraints.NORTH;
		gbc_probePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_probePanel.gridx = 1;
		gbc_probePanel.gridy = 1;
		add(probePanel, gbc_probePanel);
		probePanel.setLayout(new GridLayout(1, 1, 0, 0));
	}

	/** */
	private static final long serialVersionUID = 1L;

}
