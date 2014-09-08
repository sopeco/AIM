package org.aim.ui.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.lang.model.element.Modifier;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.aim.ui.entities.RawInstrumentationEntity;
import org.aim.ui.manager.Core;

import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BCIComponent extends JPanel implements ActionListener {

	/** */
	private static final long serialVersionUID = 1L;
	private RawInstrumentationEntity entity;
	private JLabel lblScopeValue;
	private JLabel lblProbesValue;
	private JLabel lblModExc;
	private JLabel lblModInc;
	private JLabel lblPackageExc;
	private JLabel lblPackageInc;
	private JButton btnRemove;
	private JButton btnEdit;
	private JButton btnExport;

	public BCIComponent() {
		setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
		// setPreferredSize(new Dimension(500, 100));
		// setSize(500, 100);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 70, 200, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblScope = new JLabel("Scope");
		lblScope.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblScope = new GridBagConstraints();
		gbc_lblScope.insets = new Insets(5, 5, 5, 5);
		gbc_lblScope.anchor = GridBagConstraints.WEST;
		gbc_lblScope.gridx = 0;
		gbc_lblScope.gridy = 0;
		add(lblScope, gbc_lblScope);

		lblScopeValue = new JLabel("New label");
		GridBagConstraints gbc_lblScopeValue = new GridBagConstraints();
		gbc_lblScopeValue.anchor = GridBagConstraints.WEST;
		gbc_lblScopeValue.insets = new Insets(5, 0, 5, 5);
		gbc_lblScopeValue.gridx = 1;
		gbc_lblScopeValue.gridy = 0;
		add(lblScopeValue, gbc_lblScopeValue);

		btnExport = new JButton("");
		btnExport.addActionListener(this);
		btnExport.setIcon(new ImageIcon(BCIComponent.class.getResource("/icons/document-export.png")));
		GridBagConstraints gbc_btnExport = new GridBagConstraints();
		gbc_btnExport.insets = new Insets(5, 0, 5, 5);
		gbc_btnExport.gridx = 2;
		gbc_btnExport.gridy = 0;
		add(btnExport, gbc_btnExport);

		btnEdit = new JButton("");
		btnEdit.addActionListener(this);
		btnEdit.setIcon(new ImageIcon(BCIComponent.class.getResource("/icons/pencil.png")));
		GridBagConstraints gbc_btnEdit = new GridBagConstraints();
		gbc_btnEdit.insets = new Insets(5, 0, 5, 5);
		gbc_btnEdit.gridx = 3;
		gbc_btnEdit.gridy = 0;
		add(btnEdit, gbc_btnEdit);

		btnRemove = new JButton("");
		btnRemove.addActionListener(this);
		btnRemove.setIcon(new ImageIcon(BCIComponent.class.getResource("/icons/cross.png")));
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(5, 0, 5, 0);
		gbc_btnRemove.gridx = 4;
		gbc_btnRemove.gridy = 0;
		add(btnRemove, gbc_btnRemove);

		JLabel lblProbes = new JLabel("Probes");
		lblProbes.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblProbes = new GridBagConstraints();
		gbc_lblProbes.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblProbes.insets = new Insets(5, 5, 5, 5);
		gbc_lblProbes.gridx = 0;
		gbc_lblProbes.gridy = 1;
		add(lblProbes, gbc_lblProbes);

		lblProbesValue = new JLabel("New label");
		GridBagConstraints gbc_lblProbesValue = new GridBagConstraints();
		gbc_lblProbesValue.gridwidth = 4;
		gbc_lblProbesValue.insets = new Insets(5, 0, 5, 5);
		gbc_lblProbesValue.anchor = GridBagConstraints.WEST;
		gbc_lblProbesValue.gridx = 1;
		gbc_lblProbesValue.gridy = 1;
		add(lblProbesValue, gbc_lblProbesValue);

		JLabel lblModifier = new JLabel("Modifier");
		lblModifier.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblModifier = new GridBagConstraints();
		gbc_lblModifier.anchor = GridBagConstraints.WEST;
		gbc_lblModifier.insets = new Insets(5, 5, 5, 5);
		gbc_lblModifier.gridx = 0;
		gbc_lblModifier.gridy = 2;
		add(lblModifier, gbc_lblModifier);

		JLabel lblExclude = new JLabel("Exclude");
		lblExclude.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblExclude = new GridBagConstraints();
		gbc_lblExclude.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblExclude.insets = new Insets(0, 5, 5, 5);
		gbc_lblExclude.gridx = 0;
		gbc_lblExclude.gridy = 3;
		add(lblExclude, gbc_lblExclude);

		lblModExc = new JLabel("ModExc");
		GridBagConstraints gbc_lblModExc = new GridBagConstraints();
		gbc_lblModExc.gridwidth = 4;
		gbc_lblModExc.anchor = GridBagConstraints.WEST;
		gbc_lblModExc.insets = new Insets(0, 0, 5, 5);
		gbc_lblModExc.gridx = 1;
		gbc_lblModExc.gridy = 3;
		add(lblModExc, gbc_lblModExc);

		JLabel lblNewLabel = new JLabel("Include");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 4;
		add(lblNewLabel, gbc_lblNewLabel);

		lblModInc = new JLabel("ModInc");
		GridBagConstraints gbc_lblModInc = new GridBagConstraints();
		gbc_lblModInc.gridwidth = 4;
		gbc_lblModInc.insets = new Insets(0, 0, 5, 5);
		gbc_lblModInc.anchor = GridBagConstraints.WEST;
		gbc_lblModInc.gridx = 1;
		gbc_lblModInc.gridy = 4;
		add(lblModInc, gbc_lblModInc);

		JLabel lblPackages = new JLabel("Packages");
		lblPackages.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblPackages = new GridBagConstraints();
		gbc_lblPackages.anchor = GridBagConstraints.WEST;
		gbc_lblPackages.insets = new Insets(5, 5, 5, 5);
		gbc_lblPackages.gridx = 0;
		gbc_lblPackages.gridy = 5;
		add(lblPackages, gbc_lblPackages);

		JLabel lblNewLabel_1 = new JLabel("Exclude");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 6;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		lblPackageExc = new JLabel("PackageExc");
		GridBagConstraints gbc_lblPackageExc = new GridBagConstraints();
		gbc_lblPackageExc.gridwidth = 4;
		gbc_lblPackageExc.anchor = GridBagConstraints.WEST;
		gbc_lblPackageExc.insets = new Insets(0, 0, 5, 5);
		gbc_lblPackageExc.gridx = 1;
		gbc_lblPackageExc.gridy = 6;
		add(lblPackageExc, gbc_lblPackageExc);

		JLabel lblNewLabel_2 = new JLabel("Include");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 7;
		add(lblNewLabel_2, gbc_lblNewLabel_2);

		lblPackageInc = new JLabel("PackageInc");
		GridBagConstraints gbc_lblPackageInc = new GridBagConstraints();
		gbc_lblPackageInc.gridwidth = 4;
		gbc_lblPackageInc.insets = new Insets(0, 0, 0, 5);
		gbc_lblPackageInc.anchor = GridBagConstraints.WEST;
		gbc_lblPackageInc.gridx = 1;
		gbc_lblPackageInc.gridy = 7;
		add(lblPackageInc, gbc_lblPackageInc);

		RawInstrumentationEntity dummy = new RawInstrumentationEntity();
		dummy.setScope("org.ScopeName");
		dummy.setProbes(new String[] { "Probe 1", "PRobe 2" });
		dummy.setExcModifiers(new int[] { 1, 2 });
		dummy.setIncModifiers(new int[] { 0 });
		dummy.setExcPackages(new String[] { "org.test.*" });
		dummy.setIncPackages(new String[] {});
		entity = dummy;

		updateView();
	}

	public void setRawEntity(RawInstrumentationEntity entity) {
		this.entity = entity;
		updateView();
	}

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
}
