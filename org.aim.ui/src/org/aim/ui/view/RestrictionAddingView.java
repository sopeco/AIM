package org.aim.ui.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.aim.ui.manager.RestrictionManager;

public class RestrictionAddingView extends JDialog implements ActionListener {
	/** */
	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbGroup;

	private JComboBox<String> cbType;

	private JComboBox<String> cbValue;

	private JTextField tfValue;

	public RestrictionAddingView(Component comp) {
		setSize(275, 165);
		setLocationRelativeTo(comp);
		setModal(true);
		setResizable(false);
		setTitle("Add Restriction");
		getContentPane().setLayout(null);

		cbGroup = new JComboBox<String>();
		cbGroup.setModel(new DefaultComboBoxModel<String>(new String[] { "Package", "Modifier" }));
		cbGroup.setSelectedIndex(1);
		cbGroup.setBounds(66, 11, 190, 20);
		getContentPane().add(cbGroup);
		cbGroup.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateValueField();
			}
		});

		JLabel lblGroup = new JLabel("Group:");
		lblGroup.setBounds(10, 14, 46, 14);
		getContentPane().add(lblGroup);

		cbType = new JComboBox<String>();
		cbType.setModel(new DefaultComboBoxModel<String>(new String[] { "Include", "Exclude" }));
		cbType.setBounds(66, 42, 190, 20);
		getContentPane().add(cbType);

		JLabel lblType = new JLabel("Type:");
		lblType.setBounds(10, 45, 46, 14);
		getContentPane().add(lblType);

		tfValue = new JTextField();
		tfValue.setBounds(66, 73, 190, 20);
		tfValue.setColumns(10);

		JLabel lblValue = new JLabel("Value:");
		lblValue.setBounds(10, 76, 46, 14);
		getContentPane().add(lblValue);

		JButton btnNewButton = new JButton("Add");
		btnNewButton.addActionListener(this);
		btnNewButton.setBounds(10, 104, 115, 23);
		getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		btnNewButton_1.setBounds(141, 104, 115, 23);
		getContentPane().add(btnNewButton_1);

		cbValue = new JComboBox<String>();
		cbValue.setModel(new DefaultComboBoxModel<String>(new String[] { "public", "private", "static" }));
		cbValue.setBounds(66, 73, 190, 20);
		getContentPane().add(cbValue);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		RestrictionManager.SINGLETON().addRestriction(this);
		setVisible(false);
		dispose();
	}

	public JComboBox<String> getCbGroup() {
		return cbGroup;
	}

	public JComboBox<String> getCbType() {
		return cbType;
	}

	public JComboBox<String> getCbValue() {
		return cbValue;
	}

	public JTextField getTfValue() {
		return tfValue;
	}

	/**
	 * Shows a textbox or combobox dependend on the selected group (modifier or
	 * package).
	 */
	public void updateValueField() {
		int idx = cbGroup.getSelectedIndex();

		remove(tfValue);
		remove(cbValue);

		if (idx == 0) {
			getContentPane().add(tfValue);
		} else {
			getContentPane().add(cbValue);
		}

		repaint();
	}

}
