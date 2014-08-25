package org.aim.ui.components;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ItemListPanel extends JPanel implements ActionListener {

	/**  */
	private static final long serialVersionUID = 1L;
	private JButton addButton;
	private boolean isEditable = true;
	private List<ItemListEntity> items = new ArrayList<ItemListEntity>();
	private List<String> predefinedValues;

	private String validationPattern;

	public ItemListPanel() {
		setLayout(new GridLayout(0, 1, 0, 0));

		addButton = new JButton("Add");
		addButton.addActionListener(this);
		add(addButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		addItem();
	}

	public void addItem() {
		addItem("");
	}

	public void addItem(String value) {
		ItemListEntity item = new ItemListEntity(value, predefinedValues);
		item.registerRemoveHandler(this);
		item.setEditable(isEditable);
		items.add(item);

		updateItems();
	}

	public List<String> getValues() {
		List<String> list = new ArrayList<String>();
		for (ItemListEntity item : items) {
			list.add(item.getText());
		}
		return list;
	}

	public void removeItem(ItemListEntity item) {
		for (int i = 0; i < items.size(); i++) {
			if (item == items.get(i)) {
				items.remove(i);
				updateItems();
				return;
			}
		}
	}

	public void removeItems() {
		items.clear();
		updateItems();
	}

	public void setEditable(boolean editable) {
		isEditable = editable;
		for (ItemListEntity item : items) {
			item.setEditable(isEditable);
		}
	}

	public void setPredefinedValues(List<String> values) {
		predefinedValues = values;
	}

	public void setValidationPattern(String pattern) {
		validationPattern = pattern;
	}

	public void updateItems() {
		removeAll();

		for (ItemListEntity item : items) {
			add(item);
		}

		// Add add button
		add(addButton);

		revalidate();
		repaint();
	}

	public boolean validateValue(String text) {
		if (validationPattern == null) {
			return true;
		}
		return text.matches(validationPattern);
	}

	@Override
	public void revalidate() {
		super.revalidate();
	}
}
