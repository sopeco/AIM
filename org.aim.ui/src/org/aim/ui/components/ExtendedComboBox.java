package org.aim.ui.components;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Combobox that supports delimiter.
 * 
 * @author Marius Oehler
 *
 */
@SuppressWarnings("rawtypes")
public class ExtendedComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	@SuppressWarnings("unchecked")
	public ExtendedComboBox() {
		setModel(new ExtendedComboBoxModel());
		setRenderer(new ExtendedListCellRenderer());
	}

	/**
	 * Adds a delimiter to the combobox. The delimiter can not be selected.
	 * 
	 * @param text
	 *            text of the delimiter
	 */
	@SuppressWarnings("unchecked")
	public void addDelimiter(String text) {
		this.addItem(new Delimiter(text));
	}

	/**
	 * Adds an new item. The item is an instance of the {@link String} class.
	 * 
	 * @param item
	 *            new item
	 */
	@SuppressWarnings("unchecked")
	public void addItem(String item) {
		super.addItem(item);
	}

	private static class ExtendedComboBoxModel extends DefaultComboBoxModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setSelectedItem(Object anObject) {
			if (!(anObject instanceof Delimiter)) {
				super.setSelectedItem(anObject);
			} else {
				int index = getIndexOf(anObject);
				if (index < getSize()) {
					setSelectedItem(getElementAt(index + 1));
				}
			}
		}

	}

	private static class ExtendedListCellRenderer extends DefaultListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			if (value instanceof String) {
				return new JLabel("  " + value.toString());
			} else if (value instanceof Delimiter) {
				JLabel label = new JLabel(value.toString());
				Font f = label.getFont();
				label.setFont(f.deriveFont(f.getStyle() | Font.BOLD | Font.ITALIC));
				return label;
			} else {
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		}
	}

	private static final class Delimiter {
		private String text;

		private Delimiter(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text.toString();
		}
	}
}