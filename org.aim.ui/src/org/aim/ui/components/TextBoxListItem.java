package org.aim.ui.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("unchecked")
public class TextBoxListItem extends JPanel implements ActionListener, KeyListener {

	private boolean isTextbox;

	public TextBoxListItem() {
		this("");
	}

	public TextBoxListItem(String value) {
		this(value, null);
	}

	public TextBoxListItem(String value, List<String> values) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		isTextbox = values == null;

		if (values == null) {
			textField = new JTextField(value);
			textField.addKeyListener(this);
		} else {
			textField = new JComboBox<String>();
			((JComboBox<String>) textField).setEditable(true);
			for (String v : values) {
				((JComboBox<String>) textField).addItem(v);
			}
		}

		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 0;
		add(textField, gbc_textField);
		// textField.setColumns(10);

		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(this);
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 0;
		add(btnRemove, gbc_btnRemove);
	}

	public void setEditable(boolean editable) {
		if (textField instanceof JComboBox)
			((JComboBox<String>) textField).setEditable(editable);
	}

	/**  */
	private static final long serialVersionUID = 1L;
	private JComponent textField;
	private JButton btnRemove;
	private TextBoxListPanel listPanel;

	public void registerRemoveHandler(TextBoxListPanel panel) {
		listPanel = panel;
	}

	// public JTextField getTextField() {
	// return textField;
	// }

	public String getText() {
		if (isTextbox) {
			return ((JTextField) textField).getText();
		} else {
			return (String) ((JComboBox<String>) textField).getSelectedItem();
		}
	}

	public JButton getButton() {
		return btnRemove;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRemove && listPanel != null) {
			listPanel.removeItem(this);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (listPanel.validateValue(getText())) {
			textField.setBackground(new Color(255, 255, 255));
		} else {
			textField.setBackground(new Color(255, 200, 200));
		}
	}

}
