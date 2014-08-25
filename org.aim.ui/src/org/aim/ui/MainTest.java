package org.aim.ui;

import java.awt.GridLayout;

import javax.swing.JDialog;

import org.aim.ui.bci.InstrumentationEntityWizard;
import org.aim.ui.components.TextBoxListPanel;

public class MainTest {

	public static void main(String[] args) {
//		JDialog jDialog = new JDialog();
//		jDialog.setLayout(new GridLayout(1, 1));
//		jDialog.add(new TextBoxListPanel());
//		jDialog.setVisible(true);
		
		InstrumentationEntityWizard w = new InstrumentationEntityWizard();
		w.setVisible(true);
	}

}
