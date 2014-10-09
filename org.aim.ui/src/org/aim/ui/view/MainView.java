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
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
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
import org.aim.ui.view.sampler.SamplerComponent;
import org.aim.ui.view.sampler.SamplerPanel;

/**
 * The application's main window.
 * 
 * @author Marius Oehler
 *
 */
public final class MainView extends JFrame implements ConnectionStateListener, ActionListener {

	/**
	 * Connetion states.
	 */
	public enum ClientSettingsState {
		CONNECTED, CONNECTING, DEFAULT
	}

	private static final int INSET_VALUE = 5;

	private static MainView instance;
	private static final Dimension INSTRUMENTATION_WIZARD_SIZE = new Dimension(400, 500);
	private static final Dimension MAIN_WINDOW_SIZE = new Dimension(750, 480);
	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the singleton instance of this class.
	 * 
	 * @return {@link MainView} instance
	 */
	public static MainView instance() {
		if (instance == null) {
			instance = new MainView();
		}
		return instance;
	}

	private JPanel bciPanel;
	private JButton btnAddIE;
	private JButton btnConnect;
	private JButton btnImportInstrumentationEntity;
	private JButton btnInstrument;
	private JButton btnMonitoring;
	private JComboBox<String> inputHost;
	private JTextField inputPort;
	private RestrictionPanel panelGlobalRestrictions;
	private SamplerPanel samplerPanel;

	private JButton btnDownloadDataset;

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
		// CHECKSTYLE:OFF
		inputHost.setPreferredSize(new Dimension(200, 20));
		// CHECKSTYLE:ON
		inputHost.setEditable(true);
		panel.add(inputHost);

		JLabel lblNewLabel1 = new JLabel("Port:");
		panel.add(lblNewLabel1);

		inputPort = new JTextField();
		inputPort.setText("1010");
		panel.add(inputPort);
		// CHECKSTYLE:OFF
		inputPort.setColumns(5);
		// CHECKSTYLE:ON

		btnConnect = new JButton("");
		btnConnect.setIcon(new ImageIcon(MainView.class.getResource("/icons/plug-disconnect.png")));
		btnConnect.addActionListener(this);
		panel.add(btnConnect);

		JPanel panel7 = new JPanel();
		FlowLayout flowLayout3 = (FlowLayout) panel7.getLayout();
		flowLayout3.setAlignment(FlowLayout.RIGHT);
		panel.add(panel7);

		btnInstrument = new JButton("Instrument");
		btnInstrument.addActionListener(this);
		btnInstrument.setEnabled(false);
		panel7.add(btnInstrument);

		btnMonitoring = new JButton("Start Monitoring");
		btnMonitoring.addActionListener(this);
		btnMonitoring.setEnabled(false);
		panel7.add(btnMonitoring);

		btnDownloadDataset = new JButton("Data Download");
		btnDownloadDataset.addActionListener(this);
		btnDownloadDataset.setIcon(new ImageIcon(MainView.class.getResource("/icons/disk-arrow.png")));
		panel7.add(btnDownloadDataset);

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel1 = new JPanel();
		tabbedPane.addTab("Instrumentation Entities", null, panel1, null);
		panel1.setLayout(new BorderLayout(0, 0));

		JScrollPane bciScrollPane = new JScrollPane();
		panel1.add(bciScrollPane);

		JPanel panel3 = new JPanel();
		bciScrollPane.setViewportView(panel3);
		GridBagLayout gblPanel3 = new GridBagLayout();
		// CHECKSTYLE:OFF
		gblPanel3.columnWidths = new int[] { 577, 0 };
		gblPanel3.rowHeights = new int[] { 241, 0 };
		// CHECKSTYLE:ON
		gblPanel3.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gblPanel3.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel3.setLayout(gblPanel3);

		bciPanel = new JPanel();
		GridBagConstraints gbcBciPanel = new GridBagConstraints();
		gbcBciPanel.insets = new Insets(INSET_VALUE, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcBciPanel.anchor = GridBagConstraints.NORTH;
		gbcBciPanel.fill = GridBagConstraints.HORIZONTAL;
		gbcBciPanel.gridx = 0;
		gbcBciPanel.gridy = 0;
		panel3.add(bciPanel, gbcBciPanel);
		// CHECKSTYLE:OFF
		bciPanel.setLayout(new GridLayout(2, 1, 5, 5));
		// CHECKSTYLE:ON

		JPanel panel2 = new JPanel();
		FlowLayout flowLayout2 = (FlowLayout) panel2.getLayout();
		flowLayout2.setAlignment(FlowLayout.RIGHT);
		panel1.add(panel2, BorderLayout.SOUTH);

		btnAddIE = new JButton("Add Instrumentation Entity");
		btnAddIE.setIcon(new ImageIcon(MainView.class.getResource("/icons/plus-circle.png")));
		btnAddIE.addActionListener(this);

		btnImportInstrumentationEntity = new JButton("Import Instrumentation Entity");
		btnImportInstrumentationEntity.setIcon(new ImageIcon(MainView.class.getResource("/icons/document-import.png")));
		btnImportInstrumentationEntity.setEnabled(false);
		btnImportInstrumentationEntity.addActionListener(this);
		panel2.add(btnImportInstrumentationEntity);
		panel2.add(btnAddIE);

		samplerPanel = new SamplerPanel();
		samplerPanel.getBtnAddSampler().setIcon(new ImageIcon(MainView.class.getResource("/icons/plus-circle.png")));
		tabbedPane.addTab("Sampler", null, samplerPanel, null);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Global Restriction", null, scrollPane, null);

		panelGlobalRestrictions = new RestrictionPanel();
		scrollPane.setViewportView(panelGlobalRestrictions);

		setSize(MAIN_WINDOW_SIZE);

		loadHosts();

		onDisconnection();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAddIE) {
			InstrumentationEntityWizard dialog = new InstrumentationEntityWizard();
			dialog.setSize(INSTRUMENTATION_WIZARD_SIZE);
			dialog.setModal(true);
			dialog.setLocationRelativeTo(MainView.this);
			dialog.setVisible(true);
		} else if (e.getSource() == btnConnect) {
			Main.getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					ClientManager.instance().actionButton();
				}
			});
		} else if (e.getSource() == btnImportInstrumentationEntity) {
			Core.instance().importRawInstrumentationEntityFromFile();
		} else if (e.getSource() == btnInstrument) {
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
				ClientManager.instance().startMonitoring();
			} else {
				// Stop Monitoring
				btnInstrument.setEnabled(true);
				btnMonitoring.setText("Start Monitoring");
				ClientManager.instance().stopMonitoring();
			}
		} else if (e.getSource() == btnDownloadDataset) {
			Core.instance().downloadDataset();
		}
	}

	/**
	 * Prints the given string to the log box.
	 * 
	 * @param message
	 *            - text to print
	 */
	public void addLogMessage(String message) {
		// SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
		// textLog.setText(textLog.getText() + "\n" + format.format(new Date())
		// +
		// " - " + message);
		// scrollPaneLog.getVerticalScrollBar().setValue(scrollPaneLog.getVerticalScrollBar().getMaximum());
	}

	/**
	 * Returns the list including all {@link SamplerComponent}s.
	 * 
	 * @return lits containing {@link SamplerComponent}
	 */
	public List<SamplerComponent> getAllSamplerComponents() {
		return samplerPanel.getAllSamplerComponents();
	}

	/**
	 * Returns the global restriction settings.
	 * 
	 * @return global restrictions
	 */
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

	/**
	 * Returns the agent's host.
	 * 
	 * @return agent's host
	 */
	public JComboBox<String> getInputHost() {
		return inputHost;
	}

	/**
	 * Returns the agent's port.
	 * 
	 * @return agent's port
	 */
	public JTextField getInputPort() {
		return inputPort;
	}

	/**
	 * Loads recently hosts to the host input field.
	 */
	public void loadHosts() {
		inputHost.addItem("localhost");
	}

	@Override
	public void onConnection() {
		btnAddIE.setEnabled(true);

		if (ClientManager.instance().isMonitoringEnabled()) {
			// Monitoring is enabled
			btnInstrument.setEnabled(false);
			btnMonitoring.setEnabled(true);
			btnInstrument.setText("Uninstrument");
			btnMonitoring.setText("Stop Monitoring");
		}
	}

	@Override
	public void onDisconnection() {
		btnAddIE.setEnabled(false);

		setClientSettingsState(ClientSettingsState.DEFAULT);
	}

	/**
	 * Sets a new {@link ClientSettingsState}.
	 * 
	 * @param state
	 *            - new state
	 */
	public void setClientSettingsState(ClientSettingsState state) {
		switch (state) {
		case CONNECTED:
			inputHost.setEnabled(false);
			inputPort.setEnabled(false);
			btnConnect.setEnabled(true);
			//btnConnect.setText("Disconnect");
			btnConnect.setIcon(new ImageIcon(MainView.class.getResource("/icons/plug-connect.png")));
			btnDownloadDataset.setEnabled(true);
			btnInstrument.setText("Instrument");
			btnInstrument.setEnabled(true);
			btnMonitoring.setEnabled(false);
			btnImportInstrumentationEntity.setEnabled(true);
			samplerPanel.getBtnAddSampler().setEnabled(true);
			break;
		case CONNECTING:
			inputHost.setEnabled(false);
			inputPort.setEnabled(false);
			btnConnect.setEnabled(false);
			btnDownloadDataset.setEnabled(false);
			//btnConnect.setText("Connecting..");
			btnInstrument.setEnabled(false);
			btnMonitoring.setEnabled(false);
			btnImportInstrumentationEntity.setEnabled(false);
			samplerPanel.getBtnAddSampler().setEnabled(false);
			break;
		case DEFAULT:
		default:
			inputHost.setEnabled(true);
			inputPort.setEnabled(true);
			btnConnect.setEnabled(true);
			btnDownloadDataset.setEnabled(false);
			//btnConnect.setText("Connect");
			btnConnect.setIcon(new ImageIcon(MainView.class.getResource("/icons/plug-disconnect.png")));
			btnInstrument.setEnabled(false);
			btnMonitoring.setEnabled(false);
			btnImportInstrumentationEntity.setEnabled(false);
			samplerPanel.getBtnAddSampler().setEnabled(false);
			break;
		}
	}

	/**
	 * Sets the collection of {@link RawInstrumentationEntity} to display.
	 * 
	 * @param entities
	 *            - set of entites to display
	 */
	public void updateInstrumentEntities(Collection<RawInstrumentationEntity> entities) {
		bciPanel.removeAll();
		// CHECKSTYLE:OFF
		bciPanel.setLayout(new GridLayout(entities.size(), 1, 5, 5));
		// CHECKSTYLE:ON

		for (RawInstrumentationEntity raw : entities) {
			BCIComponent bci = new BCIComponent();
			bci.setRawEntity(raw);
			bciPanel.add(bci);
		}

		revalidate();
	}
}
