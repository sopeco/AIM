package org.aim.ui.view.sampler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Panel to specify the particular sampler.
 * 
 * @author Marius Oehler
 *
 */
public class SamplerPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton btnAddSampler;

	private static final int INSET_VALUE = 5;

	private JPanel panel;
	private JPanel panelSamplerWrapper;
	private List<SamplerComponent> samplerList = new ArrayList<>();
	private JScrollPane scrollPane;

	/**
	 * Constructor.
	 */
	public SamplerPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0 };
		setLayout(gridBagLayout);

		btnAddSampler = new JButton("Add Sampler");
		btnAddSampler.addActionListener(this);

		scrollPane = new JScrollPane();
		GridBagConstraints gbcScrollPane = new GridBagConstraints();
		gbcScrollPane.insets = new Insets(0, 0, INSET_VALUE, 0);
		gbcScrollPane.fill = GridBagConstraints.BOTH;
		gbcScrollPane.gridx = 0;
		gbcScrollPane.gridy = 0;
		add(scrollPane, gbcScrollPane);

		panel = new JPanel();
		scrollPane.setViewportView(panel);
		GridBagLayout gblPanel = new GridBagLayout();
		gblPanel.columnWidths = new int[] { 0, 0 };
		gblPanel.rowHeights = new int[] { 0, 0 };
		gblPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gblPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel.setLayout(gblPanel);

		panelSamplerWrapper = new JPanel();
		GridBagConstraints gbcPanelSamplerWrapper = new GridBagConstraints();
		gbcPanelSamplerWrapper.insets = new Insets(INSET_VALUE, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcPanelSamplerWrapper.anchor = GridBagConstraints.NORTH;
		gbcPanelSamplerWrapper.fill = GridBagConstraints.HORIZONTAL;
		gbcPanelSamplerWrapper.gridx = 0;
		gbcPanelSamplerWrapper.gridy = 0;
		panel.add(panelSamplerWrapper, gbcPanelSamplerWrapper);
		GridBagConstraints gbcBtnAddSampler = new GridBagConstraints();
		gbcBtnAddSampler.anchor = GridBagConstraints.EAST;
		gbcBtnAddSampler.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcBtnAddSampler.gridx = 0;
		gbcBtnAddSampler.gridy = 1;
		add(btnAddSampler, gbcBtnAddSampler);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAddSampler) {
			SamplerComponent samplerComponent = new SamplerComponent(this);
			samplerList.add(samplerComponent);
			updateSamplerWrapper();
		}
	}

	/**
	 * Returns the list including all {@link SamplerComponent}s.
	 * 
	 * @return lits containing {@link SamplerComponent}
	 */
	public List<SamplerComponent> getAllSamplerComponents() {
		return samplerList;
	}

	/**
	 * Removes the given {@link SamplerComponent} from the panel.
	 * 
	 * @param component
	 *            to remove
	 */
	public void removeSampler(SamplerComponent component) {
		samplerList.remove(component);
		updateSamplerWrapper();
	}

	private void updateSamplerWrapper() {
		panelSamplerWrapper.removeAll();
		// CHECKSTYLE:OFF
		panelSamplerWrapper.setLayout(new GridLayout(samplerList.size(), 1, 5, 5));
		// CHECKSTYLE:ON
		for (SamplerComponent comp : samplerList) {
			panelSamplerWrapper.add(comp);
		}
		revalidate();
	}

	/**
	 * @return the btnAddSampler
	 */
	public JButton getBtnAddSampler() {
		return btnAddSampler;
	}

}
