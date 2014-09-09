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

/**
 * Panel to specify restrictions.
 * 
 * @author Marius Oehler
 *
 */
public class RestrictionPanel extends JPanel {
	private static final int INSET_VALUE = 5;
	/** */
	private static final long serialVersionUID = 1L;

	private ItemListPanel lpExcModifier;
	private ItemListPanel lpExcPackage;
	private ItemListPanel lpIncModifier;
	private ItemListPanel lpIncPackage;

	/**
	 * Constructor.
	 */
	public RestrictionPanel() {
		setBorder(new TitledBorder(null, "Restrictions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblPackageRestrictions = new JLabel("Package Restrictions");
		GridBagConstraints gbcLblPackageRestrictions = new GridBagConstraints();
		gbcLblPackageRestrictions.anchor = GridBagConstraints.WEST;
		gbcLblPackageRestrictions.gridwidth = 2;
		gbcLblPackageRestrictions.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcLblPackageRestrictions.gridx = 0;
		gbcLblPackageRestrictions.gridy = 0;
		add(lblPackageRestrictions, gbcLblPackageRestrictions);

		JLabel lblExclude = new JLabel("Exclude:");
		GridBagConstraints gbcLblExclude = new GridBagConstraints();
		gbcLblExclude.anchor = GridBagConstraints.NORTHWEST;
		gbcLblExclude.insets = new Insets(INSET_VALUE, 0, 0, INSET_VALUE);
		gbcLblExclude.gridx = 0;
		gbcLblExclude.gridy = 1;
		add(lblExclude, gbcLblExclude);

		lpExcPackage = new ItemListPanel();
		GridBagConstraints gbcLpExcPackage = new GridBagConstraints();
		gbcLpExcPackage.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcLpExcPackage.fill = GridBagConstraints.BOTH;
		gbcLpExcPackage.gridx = 1;
		gbcLpExcPackage.gridy = 1;
		add(lpExcPackage, gbcLpExcPackage);

		JLabel lblInclude = new JLabel("Include:");
		GridBagConstraints gbcLblInclude = new GridBagConstraints();
		gbcLblInclude.anchor = GridBagConstraints.NORTHWEST;
		gbcLblInclude.insets = new Insets(INSET_VALUE, 0, INSET_VALUE, INSET_VALUE);
		gbcLblInclude.gridx = 0;
		gbcLblInclude.gridy = 2;
		add(lblInclude, gbcLblInclude);

		lpIncPackage = new ItemListPanel();
		GridBagConstraints gbcLpIncPackage = new GridBagConstraints();
		gbcLpIncPackage.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcLpIncPackage.fill = GridBagConstraints.BOTH;
		gbcLpIncPackage.gridx = 1;
		gbcLpIncPackage.gridy = 2;
		add(lpIncPackage, gbcLpIncPackage);

		JLabel lblModifierRestrictions = new JLabel("Modifier Restrictions");
		GridBagConstraints gbcLblModifierRestrictions = new GridBagConstraints();
		gbcLblModifierRestrictions.anchor = GridBagConstraints.WEST;
		gbcLblModifierRestrictions.gridwidth = 2;
		gbcLblModifierRestrictions.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcLblModifierRestrictions.gridx = 0;
		// CHECKSTYLE:OFF
		gbcLblModifierRestrictions.gridy = 3;
		// CHECKSTYLE:ON
		add(lblModifierRestrictions, gbcLblModifierRestrictions);

		JLabel lblExclude1 = new JLabel("Exclude:");
		GridBagConstraints gbcLblExclude1 = new GridBagConstraints();
		gbcLblExclude1.anchor = GridBagConstraints.NORTHWEST;
		gbcLblExclude1.insets = new Insets(INSET_VALUE, 0, INSET_VALUE, INSET_VALUE);
		gbcLblExclude1.gridx = 0;
		// CHECKSTYLE:OFF
		gbcLblExclude1.gridy = 4;
		// CHECKSTYLE:ON
		add(lblExclude1, gbcLblExclude1);

		lpExcModifier = new ItemListPanel();
		GridBagConstraints gbcLpExcModifier = new GridBagConstraints();
		gbcLpExcModifier.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcLpExcModifier.fill = GridBagConstraints.BOTH;
		gbcLpExcModifier.gridx = 1;
		// CHECKSTYLE:OFF
		gbcLpExcModifier.gridy = 4;
		// CHECKSTYLE:ON
		add(lpExcModifier, gbcLpExcModifier);

		JLabel lblInclude1 = new JLabel("Include:");
		GridBagConstraints gbcLblInclude1 = new GridBagConstraints();
		gbcLblInclude1.insets = new Insets(INSET_VALUE, 0, 0, INSET_VALUE);
		gbcLblInclude1.anchor = GridBagConstraints.NORTHWEST;
		gbcLblInclude1.gridx = 0;
		// CHECKSTYLE:OFF
		gbcLblInclude1.gridy = 5;
		// CHECKSTYLE:ON
		add(lblInclude1, gbcLblInclude1);

		lpIncModifier = new ItemListPanel();
		GridBagConstraints gbcLpIncModifier = new GridBagConstraints();
		gbcLpIncModifier.fill = GridBagConstraints.BOTH;
		gbcLpIncModifier.gridx = 1;
		// CHECKSTYLE:OFF
		gbcLpIncModifier.gridy = 5;
		// CHECKSTYLE:ON
		add(lpIncModifier, gbcLpIncModifier);

		//
		List<String> modifier = Arrays
				.asList(new String[] { "PUBLIC", "PROTECTED", "PRIVATE", "STATIC", "SYNCHRONIZED" });
		lpExcModifier.setPredefinedValues(modifier);
		lpIncModifier.setPredefinedValues(modifier);

		lpExcModifier.setEditable(false);
		lpIncModifier.setEditable(false);
	}

	/**
	 * Returns an array consisting of integers which represents the excluded
	 * modifiers.
	 * 
	 * @return an array of excluded modifiers
	 */
	public int[] getExcludedModifiers() {
		return modifierIntArray(lpExcModifier.getValues().toArray(new String[0]));
	}

	/**
	 * Returns an array consisting of the patterns of excluded packages.
	 * 
	 * @return an array of excluded packages
	 */
	public String[] getExcludedPackages() {
		return lpExcPackage.getValues().toArray(new String[0]);
	}

	/**
	 * Returns an array consisting of integers which represents the included
	 * modifiers.
	 * 
	 * @return an array of included modifiers
	 */
	public int[] getIncludedModifiers() {
		return modifierIntArray(lpIncModifier.getValues().toArray(new String[0]));
	}

	/**
	 * Returns an array consisting of the patterns of included packages.
	 * 
	 * @return an array of included packages
	 */
	public String[] getIncludedPackages() {
		return lpIncPackage.getValues().toArray(new String[0]);
	}

	private int[] modifierIntArray(String[] values) {
		int[] mods = new int[values.length];
		for (int i = 0; i < mods.length; i++) {
			mods[i] = Modifier.valueOf(values[i]).ordinal();
		}
		return mods;
	}

	/**
	 * Loads the given integer array into the input fields of the excluded
	 * modifiers.
	 * 
	 * @param exModifiers
	 *            - modifiers to load
	 */
	public void setExcludedModifiers(int[] exModifiers) {
		for (int mod : exModifiers) {
			lpExcModifier.addItem(Modifier.values()[mod].name());
		}
	}

	/**
	 * Loads the given string array into the input fields of the excluded
	 * packages.
	 * 
	 * @param exPackages
	 *            - packages to load
	 */
	public void setExcludedPackages(String[] exPackages) {
		for (String s : exPackages) {
			lpExcPackage.addItem(s);
		}
	}

	/**
	 * Loads the given integer array into the input fields of the included
	 * modifiers.
	 * 
	 * @param inModifiers
	 *            - modifiers to load
	 */
	public void setIncludedModifiers(int[] inModifiers) {
		for (int mod : inModifiers) {
			lpIncModifier.addItem(Modifier.values()[mod].name());
		}
	}

	/**
	 * Loads the given string array into the input fields of the included
	 * packages.
	 * 
	 * @param inPackages
	 *            - packages to load
	 */
	public void setIncludedPackages(String[] inPackages) {
		for (String s : inPackages) {
			lpIncPackage.addItem(s);
		}
	}
}
