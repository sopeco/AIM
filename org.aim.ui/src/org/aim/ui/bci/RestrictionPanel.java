package org.aim.ui.bci;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.aim.ui.components.ItemListPanel;

public class RestrictionPanel extends JPanel {
	/** */
	private static final long serialVersionUID = 1L;
	private ItemListPanel lpIncModifier;
	private ItemListPanel lpExcModifier;
	private ItemListPanel lpIncPackage;
	private ItemListPanel lpExcPackage;

	public RestrictionPanel() {
		setBorder(new TitledBorder(null, "Restrictions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblPackageRestrictions = new JLabel("Package Restrictions");
		GridBagConstraints gbc_lblPackageRestrictions = new GridBagConstraints();
		gbc_lblPackageRestrictions.anchor = GridBagConstraints.WEST;
		gbc_lblPackageRestrictions.gridwidth = 2;
		gbc_lblPackageRestrictions.insets = new Insets(0, 0, 5, 0);
		gbc_lblPackageRestrictions.gridx = 0;
		gbc_lblPackageRestrictions.gridy = 0;
		add(lblPackageRestrictions, gbc_lblPackageRestrictions);

		JLabel lblExclude = new JLabel("Exclude:");
		GridBagConstraints gbc_lblExclude = new GridBagConstraints();
		gbc_lblExclude.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblExclude.insets = new Insets(5, 0, 0, 5);
		gbc_lblExclude.gridx = 0;
		gbc_lblExclude.gridy = 1;
		add(lblExclude, gbc_lblExclude);

		lpExcPackage = new ItemListPanel();
		GridBagConstraints gbc_lpExcPackage = new GridBagConstraints();
		gbc_lpExcPackage.insets = new Insets(0, 0, 5, 0);
		gbc_lpExcPackage.fill = GridBagConstraints.BOTH;
		gbc_lpExcPackage.gridx = 1;
		gbc_lpExcPackage.gridy = 1;
		add(lpExcPackage, gbc_lpExcPackage);

		JLabel lblInclude = new JLabel("Include:");
		GridBagConstraints gbc_lblInclude = new GridBagConstraints();
		gbc_lblInclude.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblInclude.insets = new Insets(5, 0, 5, 5);
		gbc_lblInclude.gridx = 0;
		gbc_lblInclude.gridy = 2;
		add(lblInclude, gbc_lblInclude);

		lpIncPackage = new ItemListPanel();
		GridBagConstraints gbc_lpIncPackage = new GridBagConstraints();
		gbc_lpIncPackage.insets = new Insets(0, 0, 5, 0);
		gbc_lpIncPackage.fill = GridBagConstraints.BOTH;
		gbc_lpIncPackage.gridx = 1;
		gbc_lpIncPackage.gridy = 2;
		add(lpIncPackage, gbc_lpIncPackage);

		JLabel lblModifierRestrictions = new JLabel("Modifier Restrictions");
		GridBagConstraints gbc_lblModifierRestrictions = new GridBagConstraints();
		gbc_lblModifierRestrictions.anchor = GridBagConstraints.WEST;
		gbc_lblModifierRestrictions.gridwidth = 2;
		gbc_lblModifierRestrictions.insets = new Insets(0, 0, 5, 0);
		gbc_lblModifierRestrictions.gridx = 0;
		gbc_lblModifierRestrictions.gridy = 3;
		add(lblModifierRestrictions, gbc_lblModifierRestrictions);

		JLabel lblExclude_1 = new JLabel("Exclude:");
		GridBagConstraints gbc_lblExclude_1 = new GridBagConstraints();
		gbc_lblExclude_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblExclude_1.insets = new Insets(5, 0, 5, 5);
		gbc_lblExclude_1.gridx = 0;
		gbc_lblExclude_1.gridy = 4;
		add(lblExclude_1, gbc_lblExclude_1);

		lpExcModifier = new ItemListPanel();
		GridBagConstraints gbc_lpExcModifier = new GridBagConstraints();
		gbc_lpExcModifier.insets = new Insets(0, 0, 5, 0);
		gbc_lpExcModifier.fill = GridBagConstraints.BOTH;
		gbc_lpExcModifier.gridx = 1;
		gbc_lpExcModifier.gridy = 4;
		add(lpExcModifier, gbc_lpExcModifier);

		JLabel lblInclude_1 = new JLabel("Include:");
		GridBagConstraints gbc_lblInclude_1 = new GridBagConstraints();
		gbc_lblInclude_1.insets = new Insets(5, 0, 0, 5);
		gbc_lblInclude_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblInclude_1.gridx = 0;
		gbc_lblInclude_1.gridy = 5;
		add(lblInclude_1, gbc_lblInclude_1);

		lpIncModifier = new ItemListPanel();
		GridBagConstraints gbc_lpIncModifier = new GridBagConstraints();
		gbc_lpIncModifier.fill = GridBagConstraints.BOTH;
		gbc_lpIncModifier.gridx = 1;
		gbc_lpIncModifier.gridy = 5;
		add(lpIncModifier, gbc_lpIncModifier);

		//
		List<String> modifier = Arrays
				.asList(new String[] { "PUBLIC", "PROTECTED", "PRIVATE", "STATIC", "SYNCHRONIZED" });
		lpExcModifier.setPredefinedValues(modifier);
		lpIncModifier.setPredefinedValues(modifier);

		lpExcModifier.setEditable(false);
		lpIncModifier.setEditable(false);
	}

	public String[] getExcludedPackages() {
		return lpExcPackage.getValues().toArray(new String[0]);
	}

	public String[] getIncludedPackages() {
		return lpIncPackage.getValues().toArray(new String[0]);
	}

	public int[] getExcludedModifiers() {
		return modifierIntArray(lpExcModifier.getValues().toArray(new String[0]));
	}

	public int[] getIncludedModifiers() {
		return modifierIntArray(lpIncModifier.getValues().toArray(new String[0]));
	}

	private int[] modifierIntArray(String[] values) {
		int[] mods = new int[values.length];
		for (int i = 0; i < mods.length; i++) {
			mods[i] = Modifier.valueOf(values[i]).ordinal();
		}
		return mods;
	}

	public void setExcludedModifiers(int[] exModifiers) {
		for (int mod : exModifiers) {
			lpExcModifier.addItem(Modifier.values()[mod].name());
		}
	}
	
	public void setIncludedModifiers(int[] inModifiers) {
		for (int mod : inModifiers) {
			lpIncModifier.addItem(Modifier.values()[mod].name());
		}
	}

	public void setExcludedPackages(String[] exPackages) {
		for (String s : exPackages) {
			lpExcPackage.addItem(s);
		}
	}

	public void setIncludedPackages(String[] inPackages) {
		for (String s : inPackages) {
			lpIncPackage.addItem(s);
		}
	}
}
