package org.aim.ui.components;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

public class TextBoxListPanel extends JPanel implements ActionListener {

	private List<TextBoxListItem> items = new ArrayList<TextBoxListItem>();
	private JButton addButton;
	private String validationPattern;
	private List<String> predefinedValues;
	private boolean isEditable = true;

	public TextBoxListPanel() {
		setLayout(new GridLayout(0, 1, 0, 0));

		addButton = new JButton("Add");
		addButton.addActionListener(this);
		add(addButton);
	}

	public void setPredefinedValues(List<String> values) {
		predefinedValues = values;
	}

	public void setValidationPattern(String pattern) {
		validationPattern = pattern;
	}

	public boolean validateValue(String text) {
		if (validationPattern == null)
			return true;
		return text.matches(validationPattern);
	}

	public void removeItems() {
		items.clear();
		updateItems();
	}

	public void updateItems() {
		removeAll();

		for (TextBoxListItem item : items) {
			add(item);
		}

		// Add add button
		add(addButton);

		revalidate();
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		addItem();
	}

	public void addItem() {
		addItem("");
	}

	public void setEditable(boolean editable) {
		isEditable = editable;
		for (TextBoxListItem item : items) {
			item.setEditable(isEditable);
		}
	}

	public void addItem(String value) {
		TextBoxListItem item = new TextBoxListItem(value, predefinedValues);
		item.registerRemoveHandler(this);
		item.setEditable(isEditable);
		items.add(item);

		updateItems();
	}

	public void removeItem(TextBoxListItem item) {
		for (int i = 0; i < items.size(); i++) {
			if (item == items.get(i)) {
				items.remove(i);
				updateItems();
				return;
			}
		}
	}

	public List<String> getValues() {
		List<String> list = new ArrayList<String>();
		for (TextBoxListItem item : items) {
			list.add(item.getText());
		}
		return list;
	}

	/**  */
	private static final long serialVersionUID = 1L;

}
