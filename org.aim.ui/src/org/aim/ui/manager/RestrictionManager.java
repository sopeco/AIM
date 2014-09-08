package org.aim.ui.manager;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.aim.description.restrictions.Restriction;
import org.aim.ui.view.MainView;
import org.aim.ui.view.RestrictionAddingView;
import org.aim.ui.view.RestrictionItem;
import org.aim.ui.view.RestrictionItem.Group;
import org.aim.ui.view.RestrictionItem.Type;

public class RestrictionManager {

	private static RestrictionManager SINGLETON;

	public static RestrictionManager SINGLETON() {
		if (SINGLETON == null) {
			SINGLETON = new RestrictionManager();
		}
		return SINGLETON;
	}

	private Restriction globalRestriction;

	private RestrictionManager() {
		globalRestriction = new Restriction();
	}

	public void addRestriction(RestrictionAddingView view) {
		// Group: 0 = package, 1 = modifier
		int group = view.getCbGroup().getSelectedIndex();
		// Type: 0 = include, 1 = exclude
		int type = view.getCbType().getSelectedIndex();

		if (group == 0) {
			if (type == 0) {
				globalRestriction.addPackageInclude(view.getTfValue().getText());
			} else {
				globalRestriction.addPackageExclude(view.getTfValue().getText());
			}
		} else {
			// TODO - map to modifier
			if (type == 0) {
				globalRestriction.addModifierInclude(view.getCbValue().getSelectedIndex());
			} else {
				globalRestriction.addModifierExclude(view.getCbValue().getSelectedIndex());
			}
		}

		updateRestrictionView();
	}

	public void updateRestrictionView() {
		JPanel panelPackages = MainView.SINGLETON().getPanelRestrictionPackages();
		JPanel panelModifiers = MainView.SINGLETON().getPanelRestrictionModifiers();

		panelPackages.removeAll();
		panelModifiers.removeAll();

		int rowsPac = globalRestriction.getPackageIncludes().size() + globalRestriction.getPackageExcludes().size();
		panelPackages.setLayout(new GridLayout(Math.max(1, rowsPac), 1));
		if (rowsPac <= 0) {
			JLabel label = new JLabel("   No restrictions specified.");
			panelPackages.add(label);
		} else {
			for (String val : globalRestriction.getPackageIncludes()) {
				panelPackages.add(new RestrictionItem(Type.INCLUDE, Group.PACKAGE, val));
			}
			for (String val : globalRestriction.getPackageExcludes()) {
				panelPackages.add(new RestrictionItem(Type.EXCLUDE, Group.PACKAGE, val));
			}
		}

		int rowsMod = globalRestriction.getModifierIncludes().size() + globalRestriction.getModifierExcludes().size();
		panelModifiers.setLayout(new GridLayout(Math.max(1, rowsMod), 1));
		if (rowsMod <= 0) {
			JLabel label = new JLabel("   No restrictions specified.");
			panelModifiers.add(label);
		} else {
			for (int val : globalRestriction.getModifierIncludes()) {
				panelModifiers.add(new RestrictionItem(Type.INCLUDE, Group.MODIFIER, "" + val));
			}
			for (int val : globalRestriction.getModifierExcludes()) {
				panelModifiers.add(new RestrictionItem(Type.EXCLUDE, Group.MODIFIER, "" + val));
			}
		}

		MainView.SINGLETON().getPaneRestrictions().validate();
		MainView.SINGLETON().getPaneRestrictions().repaint();
	}
}
