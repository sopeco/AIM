package org.aim.ui.view.sampler;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.aim.ui.manager.ClientManager;

/**
 * View that represents a sampler.
 * 
 * @author Marius Oehler
 *
 */
public class SamplerComponent extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton btnRemove;
	private JTextField inputDelay;
	private JComboBox<String> inputSampler;
	private SamplerPanel parentPanel;

	/**
	 * Constructor.
	 */
	public SamplerComponent(SamplerPanel parentPanel) {
		this.parentPanel = parentPanel;
		setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblSampler = new JLabel("Sampler");
		GridBagConstraints gbcLblSampler = new GridBagConstraints();
		gbcLblSampler.anchor = GridBagConstraints.WEST;
		gbcLblSampler.insets = new Insets(5, 5, 0, 5);
		gbcLblSampler.gridx = 0;
		gbcLblSampler.gridy = 0;
		add(lblSampler, gbcLblSampler);

		JLabel lblDelay = new JLabel("Delay");
		GridBagConstraints gbcLblDelay = new GridBagConstraints();
		gbcLblDelay.anchor = GridBagConstraints.WEST;
		gbcLblDelay.insets = new Insets(5, 0, 0, 5);
		gbcLblDelay.gridx = 1;
		gbcLblDelay.gridy = 0;
		add(lblDelay, gbcLblDelay);

		inputSampler = new JComboBox<String>();
		inputSampler.setEditable(true);
		GridBagConstraints gbcInputSampler = new GridBagConstraints();
		gbcInputSampler.weightx = 1.0;
		gbcInputSampler.insets = new Insets(0, 5, 5, 5);
		gbcInputSampler.fill = GridBagConstraints.HORIZONTAL;
		gbcInputSampler.gridx = 0;
		gbcInputSampler.gridy = 1;
		add(inputSampler, gbcInputSampler);

		inputDelay = new JTextField();
		inputDelay.setText("500");
		GridBagConstraints gbcInputDelay = new GridBagConstraints();
		gbcInputDelay.weightx = 0.5;
		gbcInputDelay.insets = new Insets(0, 0, 5, 5);
		gbcInputDelay.fill = GridBagConstraints.HORIZONTAL;
		gbcInputDelay.gridx = 1;
		gbcInputDelay.gridy = 1;
		add(inputDelay, gbcInputDelay);
		inputDelay.setColumns(10);

		btnRemove = new JButton("");
		btnRemove.addActionListener(this);
		btnRemove.setIcon(new ImageIcon(SamplerComponent.class.getResource("/icons/cross.png")));
		GridBagConstraints gbcBtnRemove = new GridBagConstraints();
		gbcBtnRemove.insets = new Insets(0, 0, 5, 5);
		gbcBtnRemove.gridx = 2;
		gbcBtnRemove.gridy = 1;
		add(btnRemove, gbcBtnRemove);

		if (!Beans.isDesignTime() && ClientManager.instance().isConnected()) {
			List<String> sampler = ClientManager.instance().getSampler();
			for (String s : sampler) {
				inputSampler.addItem(s);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRemove) {
			parentPanel.removeSampler(this);
		}
	}

	/**
	 * Returns the sampler's delay.
	 * 
	 * @return the delay
	 */
	public long getDelay() {
		return Long.parseLong(inputDelay.getText());
	}

	/**
	 * Returns the sampler's class.
	 * 
	 * @return the sampler
	 */
	public String getSampler() {
		return (String) inputSampler.getSelectedItem();
	}
}
