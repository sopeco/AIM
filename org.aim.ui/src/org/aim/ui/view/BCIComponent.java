package org.aim.ui.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.lang.model.element.Modifier;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.aim.ui.entities.RawInstrumentationEntity;
import org.aim.ui.manager.Core;

/**
 * Component to display a {@link RawInstrumentationEntity}.
 * 
 * @author Marius Oehler
 *
 */
public class BCIComponent extends JPanel implements ActionListener {

	/** */
	private static final long serialVersionUID = 1L;
	
	private static final int BOLD_FONT_SIZE = 11;
	private static final int INSET_VALUE = 5;
	private static final int THREE = 3, FOUR = 4, FIVE = 5, SIX = 6, SEVEN = 7;
	
	private JButton btnEdit;
	private JButton btnExport;
	private JButton btnRemove;
	private RawInstrumentationEntity entity;
	private GridBagConstraints gbcBtnEdit;
	private GridBagConstraints gbcBtnExport;
	private GridBagConstraints gbcBtnRemove;
	private GridBagConstraints gbcLblExclude;
	private GridBagConstraints gbcLblModExc;
	private GridBagConstraints gbcLblModifier;
	private GridBagConstraints gbcLblModInc;
	private GridBagConstraints gbcLblNewLabel;
	private GridBagConstraints gbcLblNewLabel1;
	private GridBagConstraints gbcLblNewLabel2;
	private GridBagConstraints gbcLblPackageExc;
	private GridBagConstraints gbcLblPackageInc;
	private GridBagConstraints gbcLblPackages;
	private GridBagConstraints gbcLblProbes;
	private GridBagConstraints gbcLblProbesValue;
	private GridBagConstraints gbcLblScope;
	private GridBagConstraints gbcLblScopeValue;
	private GridBagLayout gridBagLayout;
	private JLabel lblExclude;
	private JLabel lblModExc;
	private JLabel lblModifier;
	private JLabel lblModInc;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel1;
	private JLabel lblNewLabel2;
	private JLabel lblPackageExc;
	private JLabel lblPackageInc;
	private JLabel lblPackages;
	private JLabel lblProbes;
	private JLabel lblProbesValue;
	private JLabel lblScope;
	private JLabel lblScopeValue;

	/**
	 * Constructor.
	 */
	public BCIComponent() {
		initFields();

		setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));

		// CHECKSTYLE:OFF
		gridBagLayout.columnWidths = new int[] { 70, 200, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 0, 0, 0, 0, 0, 0, 0, 0 };
		// CHECKSTYLE:ON
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		gbcLblScope.insets = new Insets(INSET_VALUE, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblScope.anchor = GridBagConstraints.WEST;
		gbcLblScope.gridx = 0;
		gbcLblScope.gridy = 0;
		add(lblScope, gbcLblScope);

		gbcLblScopeValue.anchor = GridBagConstraints.WEST;
		gbcLblScopeValue.insets = new Insets(INSET_VALUE, 0, INSET_VALUE, INSET_VALUE);
		gbcLblScopeValue.gridx = 1;
		gbcLblScopeValue.gridy = 0;
		add(lblScopeValue, gbcLblScopeValue);

		btnExport.addActionListener(this);
		btnExport.setIcon(new ImageIcon(BCIComponent.class.getResource("/icons/document-export.png")));
		gbcBtnExport.insets = new Insets(INSET_VALUE, 0, INSET_VALUE, INSET_VALUE);
		gbcBtnExport.gridx = 2;
		gbcBtnExport.gridy = 0;
		add(btnExport, gbcBtnExport);

		btnEdit.addActionListener(this);
		btnEdit.setIcon(new ImageIcon(BCIComponent.class.getResource("/icons/pencil.png")));
		gbcBtnEdit.insets = new Insets(INSET_VALUE, 0, INSET_VALUE, INSET_VALUE);
		gbcBtnEdit.gridx = THREE;
		gbcBtnEdit.gridy = 0;
		add(btnEdit, gbcBtnEdit);

		btnRemove.addActionListener(this);
		btnRemove.setIcon(new ImageIcon(BCIComponent.class.getResource("/icons/cross.png")));

		gbcBtnRemove.insets = new Insets(INSET_VALUE, 0, INSET_VALUE, 0);
		gbcBtnRemove.gridx = FOUR;
		gbcBtnRemove.gridy = 0;
		add(btnRemove, gbcBtnRemove);

		gbcLblProbes.anchor = GridBagConstraints.NORTHWEST;
		gbcLblProbes.insets = new Insets(INSET_VALUE, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblProbes.gridx = 0;
		gbcLblProbes.gridy = 1;
		add(lblProbes, gbcLblProbes);

		gbcLblProbesValue.gridwidth = FOUR;
		gbcLblProbesValue.insets = new Insets(INSET_VALUE, 0, INSET_VALUE, INSET_VALUE);
		gbcLblProbesValue.anchor = GridBagConstraints.WEST;
		gbcLblProbesValue.gridx = 1;
		gbcLblProbesValue.gridy = 1;
		add(lblProbesValue, gbcLblProbesValue);

		gbcLblModifier.anchor = GridBagConstraints.WEST;
		gbcLblModifier.insets = new Insets(INSET_VALUE, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblModifier.gridx = 0;
		gbcLblModifier.gridy = 2;
		add(lblModifier, gbcLblModifier);

		gbcLblExclude.anchor = GridBagConstraints.NORTHEAST;
		gbcLblExclude.insets = new Insets(0, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblExclude.gridx = 0;
		gbcLblExclude.gridy = THREE;
		add(lblExclude, gbcLblExclude);

		gbcLblModExc.gridwidth = FOUR;
		gbcLblModExc.anchor = GridBagConstraints.WEST;
		gbcLblModExc.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblModExc.gridx = 1;
		gbcLblModExc.gridy = THREE;
		add(lblModExc, gbcLblModExc);

		gbcLblNewLabel.anchor = GridBagConstraints.NORTHEAST;
		gbcLblNewLabel.insets = new Insets(0, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblNewLabel.gridx = 0;
		gbcLblNewLabel.gridy = FOUR;
		add(lblNewLabel, gbcLblNewLabel);

		gbcLblModInc.gridwidth = FOUR;
		gbcLblModInc.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblModInc.anchor = GridBagConstraints.WEST;
		gbcLblModInc.gridx = 1;
		gbcLblModInc.gridy = FOUR;
		add(lblModInc, gbcLblModInc);

		gbcLblPackages.anchor = GridBagConstraints.WEST;
		gbcLblPackages.insets = new Insets(INSET_VALUE, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblPackages.gridx = 0;
		gbcLblPackages.gridy = FIVE;
		add(lblPackages, gbcLblPackages);

		gbcLblNewLabel1.anchor = GridBagConstraints.NORTHEAST;
		gbcLblNewLabel1.insets = new Insets(0, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblNewLabel1.gridx = 0;
		gbcLblNewLabel1.gridy = SIX;
		add(lblNewLabel1, gbcLblNewLabel1);

		gbcLblPackageExc.gridwidth = FOUR;
		gbcLblPackageExc.anchor = GridBagConstraints.WEST;
		gbcLblPackageExc.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblPackageExc.gridx = 1;
		gbcLblPackageExc.gridy = SIX;
		add(lblPackageExc, gbcLblPackageExc);

		gbcLblNewLabel2.anchor = GridBagConstraints.NORTHEAST;
		gbcLblNewLabel2.insets = new Insets(0, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcLblNewLabel2.gridx = 0;
		gbcLblNewLabel2.gridy = SEVEN;
		add(lblNewLabel2, gbcLblNewLabel2);

		gbcLblPackageInc.gridwidth = FOUR;
		gbcLblPackageInc.insets = new Insets(0, 0, INSET_VALUE, INSET_VALUE);
		gbcLblPackageInc.anchor = GridBagConstraints.WEST;
		gbcLblPackageInc.gridx = 1;
		gbcLblPackageInc.gridy = SEVEN;
		add(lblPackageInc, gbcLblPackageInc);

		for (JLabel label : new JLabel[] { lblNewLabel2, lblScope, lblProbes, lblExclude, lblModifier, lblNewLabel,
				lblPackages, lblNewLabel1 }) {
			label.setFont(new Font("Tahoma", Font.BOLD, BOLD_FONT_SIZE));
		}

		updateView();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == btnRemove) {
			Core.instance().removeRawInstrumentationEntity(entity);
		} else if (event.getSource() == btnEdit) {
			Core.instance().editRawInstrumentationEntity(entity);
		} else if (event.getSource() == btnExport) {
			Core.instance().exportRawInstrumentationEntityInFile(entity);
		}
	}

	private void initFields() {
		gridBagLayout = new GridBagLayout();
		lblScope = new JLabel("Scope");
		gbcLblScope = new GridBagConstraints();
		lblScopeValue = new JLabel("New label");
		gbcLblScopeValue = new GridBagConstraints();
		btnExport = new JButton("");
		gbcBtnExport = new GridBagConstraints();
		btnEdit = new JButton("");
		gbcBtnEdit = new GridBagConstraints();
		lblProbes = new JLabel("Probes");
		gbcLblProbes = new GridBagConstraints();
		lblProbesValue = new JLabel("New label");
		gbcLblProbesValue = new GridBagConstraints();
		lblModifier = new JLabel("Modifier");
		gbcLblModifier = new GridBagConstraints();
		lblExclude = new JLabel("Exclude");
		gbcLblExclude = new GridBagConstraints();
		lblModExc = new JLabel("ModExc");
		gbcLblModExc = new GridBagConstraints();
		lblNewLabel = new JLabel("Include");
		gbcLblNewLabel = new GridBagConstraints();
		lblModInc = new JLabel("ModInc");
		gbcLblModInc = new GridBagConstraints();
		lblPackages = new JLabel("Packages");
		gbcLblPackages = new GridBagConstraints();
		lblNewLabel1 = new JLabel("Exclude");
		gbcLblNewLabel1 = new GridBagConstraints();
		btnRemove = new JButton("");
		gbcBtnRemove = new GridBagConstraints();
		lblPackageExc = new JLabel("PackageExc");
		gbcLblPackageExc = new GridBagConstraints();
		lblNewLabel2 = new JLabel("Include");
		gbcLblNewLabel2 = new GridBagConstraints();
		lblPackageInc = new JLabel("PackageInc");
		gbcLblPackageInc = new GridBagConstraints();
	}

	/**
	 * Sets the {@link RawInstrumentationEntity} to display.
	 * 
	 * @param entity
	 *            - entity to show
	 */
	public void setRawEntity(RawInstrumentationEntity entity) {
		this.entity = entity;
		updateView();
	}

	/**
	 * Refreshes the view and its components.
	 */
	public void updateView() {
		if (entity == null) {
			return;
		}

		String scope = entity.getScope();
		if (entity.isTraceScope()) {
			scope += " [Trace Scope]";
		}
		lblScopeValue.setText(scope);

		String probes = "<html>";
		if (entity.getProbes().length == 0) {
			probes = "-";
		} else {
			for (String probe : entity.getProbes()) {
				probes += probe;
				probes += "<br>";
			}
			probes += "</html>";
		}
		lblProbesValue.setText(probes);

		String modExc = "<html>";
		if (entity.getExcModifiers().length == 0) {
			modExc = "-";
		} else {
			for (int mod : entity.getExcModifiers()) {
				modExc += Modifier.values()[mod];
				modExc += "<br>";
			}
			modExc += "</html>";
		}
		lblModExc.setText(modExc);

		String modInc = "<html>";
		if (entity.getIncModifiers().length == 0) {
			modInc = "-";
		} else {
			for (int mod : entity.getIncModifiers()) {
				modInc += Modifier.values()[mod];
				modInc += "<br>";
			}
			modInc += "</html>";
		}
		lblModInc.setText(modInc);

		String pacExc = "<html>";
		if (entity.getExcPackages().length == 0) {
			pacExc = "-";
		} else {
			for (String pge : entity.getExcPackages()) {
				pacExc += pge;
				pacExc += "<br>";
			}
			pacExc += "</html>";
		}
		lblPackageExc.setText(pacExc);

		String pacInc = "<html>";
		if (entity.getIncPackages().length == 0) {
			pacInc = "-";
		} else {
			for (String pge : entity.getIncPackages()) {
				pacInc += pge;
				pacInc += "<br>";
			}
			pacInc += "</html>";
		}
		lblPackageInc.setText(pacInc);
	}
}
