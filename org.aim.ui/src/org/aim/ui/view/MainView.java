package org.aim.ui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

import org.aim.ui.Main;
import org.aim.ui.bci.InstrumentationEntityWizard;
import org.aim.ui.manager.ClientManager;

public class MainView extends JFrame {

	/**  */
	private static final long serialVersionUID = 1L;

	private static MainView SINGLETON;

	public static MainView SINGLETON() {
		if (SINGLETON == null) {
			SINGLETON = new MainView();
		}
		return SINGLETON;
	}

	public enum ClientSettingsState {
		DEFAULT, CONNECTING, CONNECTED
	}

	private JTextField inputPort;
	private JComboBox<String> inputHost;

	private JTextPane textLog;

	private JButton btnConnect;

	private JScrollPane scrollPaneLog;

	private JPanel panelRestModifiers;

	private JPanel panelRestPackages;

	private JPanel paneRestrictions;

	private MainView() {
		setTitle("AIM Control");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Main.exit();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.exit();
			}
		});
		mnFile.add(mntmExit);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "InstrumentationClient", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		getContentPane().add(panel, BorderLayout.NORTH);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		JLabel lblNewLabel = new JLabel("Host:");
		panel.add(lblNewLabel);

		inputHost = new JComboBox<String>();
		inputHost.setPreferredSize(new Dimension(200, 20));
		inputHost.setEditable(true);
		panel.add(inputHost);

		JLabel lblNewLabel_1 = new JLabel("Port:");
		panel.add(lblNewLabel_1);

		inputPort = new JTextField();
		inputPort.setText("1010");
		panel.add(inputPort);
		inputPort.setColumns(5);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						ClientManager.SINGLETON().actionButton();
					}
				});
			}
		});
		panel.add(btnConnect);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Instrumentation Entities", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane bciScrollPane = new JScrollPane();
		panel_1.add(bciScrollPane);
		
		JPanel panel_3 = new JPanel();
		bciScrollPane.setViewportView(panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{577, 0};
		gbl_panel_3.rowHeights = new int[]{241, 0};
		gbl_panel_3.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JPanel bciPanel = new JPanel();
		GridBagConstraints gbc_bciPanel = new GridBagConstraints();
		gbc_bciPanel.anchor = GridBagConstraints.NORTH;
		gbc_bciPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_bciPanel.gridx = 0;
		gbc_bciPanel.gridy = 0;
		panel_3.add(bciPanel, gbc_bciPanel);
		bciPanel.setLayout(new GridLayout(2, 1, 0, 0));
		
		bciPanel.add(new BCIComponent());
		bciPanel.add(new BCIComponent());
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);
		panel_1.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Add Instrumentation Entity");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InstrumentationEntityWizard dialog = new InstrumentationEntityWizard();
				dialog.setSize(400,500);
				dialog.setModal(true);
				dialog.setLocationRelativeTo(MainView.this);
				dialog.setVisible(true);
			}
		});
		panel_2.add(btnNewButton);
		
		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Sampler", null, panel_6, null);

		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Global Restrictions", null, panel_4, null);
		panel_4.setLayout(new BorderLayout(0, 0));

		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_5.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_4.add(panel_5, BorderLayout.SOUTH);

		JButton btnAddRestriction = new JButton("Add Restriction");
		btnAddRestriction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RestrictionAddingView view = new RestrictionAddingView(MainView.this);
			}
		});
		panel_5.add(btnAddRestriction);

		JButton btnRemoveRestriction = new JButton("Remove Selected");
		panel_5.add(btnRemoveRestriction);

		JScrollPane scrollPaneRestrictions = new JScrollPane();
		panel_4.add(scrollPaneRestrictions, BorderLayout.CENTER);

		paneRestrictions = new JPanel();
		scrollPaneRestrictions.setViewportView(paneRestrictions);
		GridBagLayout gbl_paneRestrictions = new GridBagLayout();
		gbl_paneRestrictions.columnWidths = new int[] { 0, 0 };
		gbl_paneRestrictions.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_paneRestrictions.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_paneRestrictions.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		paneRestrictions.setLayout(gbl_paneRestrictions);

		JLabel lblNewLabel_3 = new JLabel("Packages");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(5, 5, 5, 0);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 0;
		paneRestrictions.add(lblNewLabel_3, gbc_lblNewLabel_3);

		panelRestPackages = new JPanel();
		GridBagConstraints gbc_panelRestPackages = new GridBagConstraints();
		gbc_panelRestPackages.insets = new Insets(0, 0, 5, 0);
		gbc_panelRestPackages.fill = GridBagConstraints.BOTH;
		gbc_panelRestPackages.gridx = 0;
		gbc_panelRestPackages.gridy = 1;
		paneRestrictions.add(panelRestPackages, gbc_panelRestPackages);
		panelRestPackages.setLayout(new GridLayout(1, 0, 0, 0));
		
		JLabel lblNoRestrictionsSpecified = new JLabel("   No restrictions specified.");
		panelRestPackages.add(lblNoRestrictionsSpecified);

		JLabel lblNewLabel_2 = new JLabel("Modifiers");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(5, 5, 5, 0);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		paneRestrictions.add(lblNewLabel_2, gbc_lblNewLabel_2);

		panelRestModifiers = new JPanel();
		GridBagConstraints gbc_panelRestModifiers = new GridBagConstraints();
		gbc_panelRestModifiers.fill = GridBagConstraints.BOTH;
		gbc_panelRestModifiers.gridx = 0;
		gbc_panelRestModifiers.gridy = 3;
		paneRestrictions.add(panelRestModifiers, gbc_panelRestModifiers);
		panelRestModifiers.setLayout(new GridLayout(1, 0, 0, 0));
		
		JLabel lblNoRestrictionsSpecified_1 = new JLabel("   No restrictions specified.");
		panelRestModifiers.add(lblNoRestrictionsSpecified_1);

		textLog = new JTextPane();
		textLog.setEditable(false);

		scrollPaneLog = new JScrollPane(textLog);
		scrollPaneLog.setPreferredSize(new Dimension(100, 60));

		getContentPane().add(scrollPaneLog, BorderLayout.SOUTH);
		//
		setSize(600, 480);

		loadHosts();
	}

	public void loadHosts() {
		inputHost.addItem("localhost");
	}

	public void addLogMessage(String message) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
		textLog.setText(textLog.getText() + "\n" + format.format(new Date()) + " - " + message);
		scrollPaneLog.getVerticalScrollBar().setValue(scrollPaneLog.getVerticalScrollBar().getMaximum());
	}

	public JTextField getInputPort() {
		return inputPort;
	}

	public JPanel getPaneRestrictions() {
		return paneRestrictions;
	}

	public JComboBox<String> getInputHost() {
		return inputHost;
	}

	public JPanel getPanelRestrictionModifiers() {
		return panelRestModifiers;
	}

	public JPanel getPanelRestrictionPackages() {
		return panelRestPackages;
	}

	public void setClientSettingsState(ClientSettingsState state) {
		switch (state) {
		case CONNECTED:
			inputHost.setEnabled(false);
			inputPort.setEnabled(false);
			btnConnect.setEnabled(true);
			btnConnect.setText("Disconnect");
			break;
		case CONNECTING:
			inputHost.setEnabled(false);
			inputPort.setEnabled(false);
			btnConnect.setEnabled(false);
			btnConnect.setText("Connecting..");
			break;
		case DEFAULT:
		default:
			inputHost.setEnabled(true);
			inputPort.setEnabled(true);
			btnConnect.setEnabled(true);
			btnConnect.setText("Connect");
			break;
		}
	}
}
