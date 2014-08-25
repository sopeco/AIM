package org.aim.ui.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RestrictionItem extends JPanel {

	private Type type;
	private Group group;

	public enum Group {
		PACKAGE, MODIFIER
	}

	public enum Type {
		INCLUDE, EXCLUDE
	}

	public RestrictionItem(Type type, Group group, String restriction) {
		this();
		this.type = type;
		this.group = group;

		lblRestriction.setText(restriction);
	}

	public RestrictionItem() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("[ " + ((type == Type.INCLUDE) ? ("+") : ("-")) + " ]");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 10, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		lblRestriction = new JLabel("namen");
		GridBagConstraints gbc_lblRestriction = new GridBagConstraints();
		gbc_lblRestriction.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblRestriction.insets = new Insets(0, 0, 0, 5);
		gbc_lblRestriction.gridx = 1;
		gbc_lblRestriction.gridy = 0;
		add(lblRestriction, gbc_lblRestriction);

		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.EAST;
		gbc_chckbxNewCheckBox.gridx = 2;
		gbc_chckbxNewCheckBox.gridy = 0;
		add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);

		setSize(200, 22);
		setPreferredSize(new Dimension(200, 22));
	}

	/** */
	private static final long serialVersionUID = 1L;
	private JLabel lblRestriction;

}
