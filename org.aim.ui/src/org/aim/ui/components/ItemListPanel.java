package org.aim.ui.components;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

/**
 * Component to provide an input with multiple textfields or comboboxes.
 * 
 * @author Marius Oehler
 *
 */
public class ItemListPanel extends JPanel implements ActionListener {

	/**  */
	private static final long serialVersionUID = 1L;
	
	private JButton addButton;
	private boolean isEditable = true;
	private List<ItemListEntity> items = new ArrayList<ItemListEntity>();
	private List<String> predefinedValues;

	private String validationPattern;

	/**
	 * Constructor.
	 */
	public ItemListPanel() {
		setLayout(new GridLayout(0, 1, 0, 0));

		addButton = new JButton("");
		addButton.setIcon(new ImageIcon(ItemListPanel.class.getResource("/icons/plus-circle.png")));
		addButton.addActionListener(this);
		add(addButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		addItem();
	}

	/**
	 * Adds an empty item.
	 */
	public void addItem() {
		addItem("");
	}

	/**
	 * Adds an item with a given value.
	 * 
	 * @param value
	 *            - value of the item
	 */
	public void addItem(String value) {
		ItemListEntity item = new ItemListEntity(value, predefinedValues);
		item.registerRemoveHandler(this);
		item.setEditable(isEditable);
		items.add(item);

		updateItems();
	}

	/**
	 * Returns all specified values in a list.
	 * 
	 * @return list of strings
	 */
	public List<String> getValues() {
		List<String> list = new ArrayList<String>();
		for (ItemListEntity item : items) {
			list.add(item.getText());
		}
		return list;
	}

	/**
	 * Removes the given item from the panel.
	 * 
	 * @param item
	 *            - item to remove
	 */
	public void removeItem(ItemListEntity item) {
		for (int i = 0; i < items.size(); i++) {
			if (item == items.get(i)) {
				items.remove(i);
				updateItems();
				return;
			}
		}
	}

	/**
	 * Removes all items from the panel.
	 */
	public void removeItems() {
		items.clear();
		updateItems();
	}

	@Override
	public void revalidate() {
		super.revalidate();
	}

	/**
	 * Sets whether the items are editable.
	 * 
	 * @param editable
	 *            - are editable
	 */
	public void setEditable(boolean editable) {
		isEditable = editable;
		for (ItemListEntity item : items) {
			item.setEditable(isEditable);
		}
	}

	/**
	 * Sets predefined values which can be selected.
	 * 
	 * @param values
	 *            - predefined values
	 */
	public void setPredefinedValues(List<String> values) {
		predefinedValues = values;
	}

	/**
	 * Sets a pattern that is used to validate the input of the textfields.
	 * 
	 * @param pattern
	 *            - pattern to validate input
	 */
	public void setValidationPattern(String pattern) {
		validationPattern = pattern;
	}

	/**
	 * Updates the panel and its items.
	 */
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

	/**
	 * Checks the given string whether it matches the set validation pattern. If
	 * no pattern is set, this method will return <code>true</code>.
	 * 
	 * @param text
	 *            - string to validate
	 * @return if the pattern is <code>null</code> or the string matches the
	 *         pattern it will return <code>true</code>
	 */
	public boolean validateValue(String text) {
		if (validationPattern == null) {
			return true;
		}
		return text.matches(validationPattern);
	}
}
