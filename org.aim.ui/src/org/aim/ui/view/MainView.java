package org.aim.ui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Collection;
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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.aim.description.restrictions.Restriction;
import org.aim.ui.Main;
import org.aim.ui.bci.InstrumentationEntityWizard;
import org.aim.ui.bci.RestrictionPanel;
import org.aim.ui.entities.RawInstrumentationEntity;
import org.aim.ui.interfaces.ConnectionStateListener;
import org.aim.ui.manager.ClientManager;
import org.aim.ui.manager.Core;

public class MainView extends JFrame implements ConnectionStateListener, ActionListener {

	public enum ClientSettingsState {
		CONNECTED, CONNECTING, DEFAULT
	}

	/**  */
	private static final long serialVersionUID = 1L;

	private static MainView SINGLETON;

	public static MainView SINGLETON() {
		if (SINGLETON == null) {
			SINGLETON = new MainView();
		}
		return SINGLETON;
	}

	private JButton btnConnect;
	private JComboBox<String> inputHost;

	private JTextField inputPort;

	private JScrollPane scrollPaneLog;

	private JTextPane textLog;

	private JButton btnAddIE;

	private JButton btnInstrument;

	private JButton btnMonitoring;

	private JPanel bciPanel;

	private RestrictionPanel panelGlobalRestrictions;

	private JButton btnImportInstrumentationEntity;

	@Override
	public void onConnection() {
		btnAddIE.setEnabled(true);
	}

	@Override
	public void onDisconnection() {
		btnAddIE.setEnabled(false);

		setClientSettingsState(ClientSettingsState.DEFAULT);
	}

	private MainView() {
		ClientManager.instance().addConnectionStateListener(this);

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
			@Override
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
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						ClientManager.instance().actionButton();
					}
				});
			}
		});
		panel.add(btnConnect);

		JPanel panel_7 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_7.getLayout();
		flowLayout_3.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_7);

		btnInstrument = new JButton("Instrument");
		btnInstrument.addActionListener(this);
		btnInstrument.setEnabled(false);
		panel_7.add(btnInstrument);

		btnMonitoring = new JButton("Start Monitoring");
		btnMonitoring.addActionListener(this);
		btnMonitoring.setEnabled(false);
		panel_7.add(btnMonitoring);

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Instrumentation Entities", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane bciScrollPane = new JScrollPane();
		panel_1.add(bciScrollPane);

		JPanel panel_3 = new JPanel();
		bciScrollPane.setViewportView(panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 577, 0 };
		gbl_panel_3.rowHeights = new int[] { 241, 0 };
		gbl_panel_3.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		bciPanel = new JPanel();
		GridBagConstraints gbc_bciPanel = new GridBagConstraints();
		gbc_bciPanel.insets = new Insets(5, 5, 5, 5);
		gbc_bciPanel.anchor = GridBagConstraints.NORTH;
		gbc_bciPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_bciPanel.gridx = 0;
		gbc_bciPanel.gridy = 0;
		panel_3.add(bciPanel, gbc_bciPanel);
		bciPanel.setLayout(new GridLayout(2, 1, 5, 5));

		bciPanel.add(new BCIComponent());
		bciPanel.add(new BCIComponent());

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);
		panel_1.add(panel_2, BorderLayout.SOUTH);

		btnAddIE = new JButton("Add Instrumentation Entity");
		btnAddIE.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InstrumentationEntityWizard dialog = new InstrumentationEntityWizard();
				dialog.setSize(400, 500);
				dialog.setModal(true);
				dialog.setLocationRelativeTo(MainView.this);
				dialog.setVisible(true);
			}
		});

		btnImportInstrumentationEntity = new JButton("Import Instrumentation Entity");
		btnImportInstrumentationEntity.setEnabled(false);
		btnImportInstrumentationEntity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Core.instance().importRawInstrumentationEntityFromFile();
			}
		});
		panel_2.add(btnImportInstrumentationEntity);
		panel_2.add(btnAddIE);

		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Sampler", null, panel_6, null);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Global Restriction", null, scrollPane, null);

		panelGlobalRestrictions = new RestrictionPanel();
		scrollPane.setViewportView(panelGlobalRestrictions);

		textLog = new JTextPane();
		textLog.setEditable(false);

		scrollPaneLog = new JScrollPane(textLog);
		scrollPaneLog.setPreferredSize(new Dimension(100, 60));

		getContentPane().add(scrollPaneLog, BorderLayout.SOUTH);
		//
		setSize(680, 480);

		loadHosts();

		onDisconnection();
	}

	public Restriction getGlobalRestriction() {
		Restriction restriction = new Restriction();
		for (int mod : panelGlobalRestrictions.getExcludedModifiers()) {
			restriction.addModifierExclude(mod);
		}
		for (int mod : panelGlobalRestrictions.getIncludedModifiers()) {
			restriction.addModifierInclude(mod);
		}
		for (String pge : panelGlobalRestrictions.getExcludedPackages()) {
			restriction.addPackageExclude(pge);
		}
		for (String pge : panelGlobalRestrictions.getIncludedPackages()) {
			restriction.addPackageInclude(pge);
		}
		return restriction;
	}

	public void addLogMessage(String message) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
		textLog.setText(textLog.getText() + "\n" + format.format(new Date()) + " - " + message);
		scrollPaneLog.getVerticalScrollBar().setValue(scrollPaneLog.getVerticalScrollBar().getMaximum());
	}

	public JComboBox<String> getInputHost() {
		return inputHost;
	}

	public JTextField getInputPort() {
		return inputPort;
	}

	public void loadHosts() {
		inputHost.addItem("localhost");
	}

	public void setClientSettingsState(ClientSettingsState state) {
		switch (state) {
		case CONNECTED:
			inputHost.setEnabled(false);
			inputPort.setEnabled(false);
			btnConnect.setEnabled(true);
			btnConnect.setText("Disconnect");
			btnInstrument.setText("Instrument");
			btnInstrument.setEnabled(true);
			btnMonitoring.setEnabled(false);
			btnImportInstrumentationEntity.setEnabled(true);
			break;
		case CONNECTING:
			inputHost.setEnabled(false);
			inputPort.setEnabled(false);
			btnConnect.setEnabled(false);
			btnConnect.setText("Connecting..");
			btnInstrument.setEnabled(false);
			btnMonitoring.setEnabled(false);
			btnImportInstrumentationEntity.setEnabled(false);
			break;
		case DEFAULT:
		default:
			inputHost.setEnabled(true);
			inputPort.setEnabled(true);
			btnConnect.setEnabled(true);
			btnConnect.setText("Connect");
			btnInstrument.setEnabled(false);
			btnMonitoring.setEnabled(false);
			btnImportInstrumentationEntity.setEnabled(false);
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnInstrument) {
			if (btnInstrument.getText().equals("Instrument")) {
				// Instrument
				Core.instance().instrument();
				btnConnect.setEnabled(false);
				btnInstrument.setText("Uninstrument");
				btnMonitoring.setEnabled(true);
			} else {
				// Uninstrument
				Core.instance().uninstrument();
				btnConnect.setEnabled(true);
				btnInstrument.setText("Instrument");
				btnMonitoring.setEnabled(false);
			}
		} else if (e.getSource() == btnMonitoring) {
			if (btnMonitoring.getText().equals("Start Monitoring")) {
				// Start Monitoring
				btnInstrument.setEnabled(false);
				btnMonitoring.setText("Stop Monitoring");
			} else {
				// Stop Monitoring
				btnInstrument.setEnabled(true);
				btnMonitoring.setText("Start Monitoring");
			}
		}
	}

	public void updateInstrumentEntities(Collection<RawInstrumentationEntity> entities) {
		bciPanel.removeAll();
		bciPanel.setLayout(new GridLayout(entities.size(), 1, 5, 5));

		for (RawInstrumentationEntity raw : entities) {
			BCIComponent bci = new BCIComponent();
			bci.setRawEntity(raw);
			bciPanel.add(bci);
		}

		revalidate();
	}
}
