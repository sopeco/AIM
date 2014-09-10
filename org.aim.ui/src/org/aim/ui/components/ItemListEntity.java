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

/**
 * 
 * ItemPanel that is used by the {@link ItemListPanel}.
 * 
 * @author Marius Oehler
 *
 */
@SuppressWarnings("unchecked")
public class ItemListEntity extends JPanel implements ActionListener, KeyListener {

	/**  */
	private static final long serialVersionUID = 1L;
	
	private static final int INSET_VALUE = 5;
	private static final Color WARNING_BG_COLOR = new Color(255, 200, 200);

	private JButton btnRemove;
	private boolean isTextbox;
	private ItemListPanel listPanel;
	private JComponent textField;

	/**
	 * Constructor. Equal to <code>ItemListEntity("")</code>.
	 */
	public ItemListEntity() {
		this("");
	}

	/**
	 * Constructor.
	 * 
	 * @param value
	 *            - the value of this item
	 */
	public ItemListEntity(String value) {
		this(value, null);
	}

	/**
	 * Construcotr. Sets the value of this item to the given value and produces
	 * a list with the values out of the given list.
	 * 
	 * @param value
	 *            - value of this item
	 * @param values
	 *            - list of selectable values
	 */
	public ItemListEntity(String value, List<String> values) {
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
			for (int i = 0; i < ((JComboBox<String>) textField).getItemCount(); i++) {
				if (((JComboBox<String>) textField).getItemAt(i).equals(value)) {
					((JComboBox<String>) textField).setSelectedIndex(i);
				}
			}
		}

		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.insets = new Insets(0, 0, 0, INSET_VALUE);
		gbcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcTextField.gridx = 0;
		gbcTextField.gridy = 0;
		add(textField, gbcTextField);
		// textField.setColumns(10);

		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(this);
		GridBagConstraints gbcBtnRemove = new GridBagConstraints();
		gbcBtnRemove.gridx = 1;
		gbcBtnRemove.gridy = 0;
		add(btnRemove, gbcBtnRemove);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRemove && listPanel != null) {
			listPanel.removeItem(this);
		}
	}

	/**
	 * Returns the value of this item.
	 * 
	 * @return the item's value
	 */
	public String getText() {
		if (isTextbox) {
			return ((JTextField) textField).getText();
		} else {
			return (String) ((JComboBox<String>) textField).getSelectedItem();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (listPanel.validateValue(getText())) {
			textField.setBackground(Color.WHITE);
		} else {
			textField.setBackground(WARNING_BG_COLOR);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Registers an {@link ItemListPanel} which is listening to actions of the
	 * remove button.
	 * 
	 * @param panel
	 *            - panel that is listening
	 */
	public void registerRemoveHandler(ItemListPanel panel) {
		listPanel = panel;
	}

	/**
	 * Sets whether this item is editable.
	 * 
	 * @param editable
	 *            - is editable
	 */
	public void setEditable(boolean editable) {
		if (textField instanceof JComboBox) {
			((JComboBox<String>) textField).setEditable(editable);
		}
	}

}
