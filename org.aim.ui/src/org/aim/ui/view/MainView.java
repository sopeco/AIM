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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

	private final JPanel bciPanel;
	private final JButton btnAddIE;
	private final JButton btnConnect;
	private final JButton btnDownloadDataset;
	private final JButton btnImportInstrumentationEntity;
	private final JButton btnInstrument;
	private final JButton btnMonitoring;
	private final JComboBox<String> inputHost;
	private final JTextField inputPort;
	private final RestrictionPanel panelGlobalRestrictions;

	private final SamplerPanel samplerPanel;

	private MainView() {
		ClientManager.instance().addConnectionStateListener(this);

		setTitle("AIM Control");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				Main.exit();
			}
		});

		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		final JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		final JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Main.exit();
			}
		});
		mnFile.add(mntmExit);

		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "InstrumentationClient", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		getContentPane().add(panel, BorderLayout.NORTH);
		final FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		final JLabel lblNewLabel = new JLabel("Host:");
		panel.add(lblNewLabel);

		inputHost = new JComboBox<String>();
		// CHECKSTYLE:OFF
		// inputHost.setPreferredSize(new Dimension(200, 20));
		// CHECKSTYLE:ON
		inputHost.setEditable(true);
		panel.add(inputHost);

		final JLabel lblNewLabel1 = new JLabel("Port:");
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

		final JPanel panelb = new JPanel();
		panelb.setBorder(
				new TitledBorder(null, "Actions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panelb, BorderLayout.SOUTH);
		final FlowLayout flowLayoutb = (FlowLayout) panelb.getLayout();
		flowLayoutb.setAlignment(FlowLayout.LEFT);

		btnInstrument = new JButton("Instrument");
		btnInstrument.addActionListener(this);
		btnInstrument.setEnabled(false);
		panelb.add(btnInstrument);

		btnMonitoring = new JButton("Start Monitoring");
		btnMonitoring.addActionListener(this);
		btnMonitoring.setEnabled(false);
		panelb.add(btnMonitoring);

		btnDownloadDataset = new JButton("Data Download");
		btnDownloadDataset.addActionListener(this);
		btnDownloadDataset.setIcon(new ImageIcon(MainView.class.getResource("/icons/disk-arrow.png")));
		panelb.add(btnDownloadDataset);

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		final JPanel panel1 = new JPanel();
		tabbedPane.addTab("Instrumentation Entities", null, panel1, null);
		panel1.setLayout(new BorderLayout(0, 0));

		final JScrollPane bciScrollPane = new JScrollPane();
		panel1.add(bciScrollPane);

		final JPanel panel3 = new JPanel();
		bciScrollPane.setViewportView(panel3);
		final GridBagLayout gblPanel3 = new GridBagLayout();
		// CHECKSTYLE:OFF
		gblPanel3.columnWidths = new int[] { 577, 0 };
		gblPanel3.rowHeights = new int[] { 241, 0 };
		// CHECKSTYLE:ON
		gblPanel3.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gblPanel3.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel3.setLayout(gblPanel3);

		bciPanel = new JPanel();
		final GridBagConstraints gbcBciPanel = new GridBagConstraints();
		gbcBciPanel.insets = new Insets(INSET_VALUE, INSET_VALUE, INSET_VALUE, INSET_VALUE);
		gbcBciPanel.anchor = GridBagConstraints.NORTH;
		gbcBciPanel.fill = GridBagConstraints.HORIZONTAL;
		gbcBciPanel.gridx = 0;
		gbcBciPanel.gridy = 0;
		panel3.add(bciPanel, gbcBciPanel);
		// CHECKSTYLE:OFF
		bciPanel.setLayout(new GridLayout(2, 1, 5, 5));
		// CHECKSTYLE:ON

		final JPanel panel2 = new JPanel();
		final FlowLayout flowLayout2 = (FlowLayout) panel2.getLayout();
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

		final JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Global Restriction", null, scrollPane, null);

		panelGlobalRestrictions = new RestrictionPanel();
		scrollPane.setViewportView(panelGlobalRestrictions);

		setSize(MAIN_WINDOW_SIZE);

		init();
	}

	private void init() {
		loadHosts();

		onDisconnection();

		final List<String> connectionHistory = getConnectionHistory();
		for (final String s : connectionHistory) {
			final String host = s.split(":")[0];
			if (host.equals("localhost")) {
				continue;
			}
			inputHost.addItem(host);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == btnAddIE) {
			final InstrumentationEntityWizard dialog = new InstrumentationEntityWizard();
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
	public void addLogMessage(final String message) {
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

	private List<String> getConnectionHistory() {
		final List<String> list = new ArrayList<String>();
		final File historyFile = new File("history.txt");
		if (historyFile.exists()) {
			try {
				final BufferedReader r = new BufferedReader(new FileReader(historyFile));
				String in;
				while ((in = r.readLine()) != null) {
					list.add(in);
				}
				r.close();
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * Returns the global restriction settings.
	 * 
	 * @return global restrictions
	 */
	public Restriction getGlobalRestriction() {
		final Restriction restriction = new Restriction();
		for (final int mod : panelGlobalRestrictions.getExcludedModifiers()) {
			restriction.addModifierExclude(mod);
		}
		for (final int mod : panelGlobalRestrictions.getIncludedModifiers()) {
			restriction.addModifierInclude(mod);
		}
		for (final String pge : panelGlobalRestrictions.getExcludedPackages()) {
			restriction.addPackageExclude(pge);
		}
		for (final String pge : panelGlobalRestrictions.getIncludedPackages()) {
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
		pushConnectionHistory(inputHost.getSelectedItem() + ":" + inputPort.getText());

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

	private void pushConnectionHistory(final String newItem) {
		final List<String> history = getConnectionHistory();
		if (!history.contains(newItem)) {
			try {
				final BufferedWriter w = new BufferedWriter(new FileWriter("history.txt"));
				for (final String s : history) {
					w.write(s);
					w.write(System.getProperty("line.separator"));
				}
				w.write(newItem);
				w.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets a new {@link ClientSettingsState}.
	 * 
	 * @param state
	 *            - new state
	 */
	public void setClientSettingsState(final ClientSettingsState state) {
		switch (state) {
		case CONNECTED:
			inputHost.setEnabled(false);
			inputPort.setEnabled(false);
			btnConnect.setEnabled(true);
			// btnConnect.setText("Disconnect");
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
			// btnConnect.setText("Connecting..");
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
			// btnConnect.setText("Connect");
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
	public void updateInstrumentEntities(final Collection<RawInstrumentationEntity> entities) {
		bciPanel.removeAll();
		// CHECKSTYLE:OFF
		bciPanel.setLayout(new GridLayout(entities.size(), 1, 5, 5));
		// CHECKSTYLE:ON

		for (final RawInstrumentationEntity raw : entities) {
			final BCIComponent bci = new BCIComponent();
			bci.setRawEntity(raw);
			bciPanel.add(bci);
		}

		revalidate();
	}
}
