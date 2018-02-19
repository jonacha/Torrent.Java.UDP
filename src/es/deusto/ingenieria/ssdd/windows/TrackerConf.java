package es.deusto.ingenieria.ssdd.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import es.deusto.ingenieria.ssdd.classes.Peer;
import es.deusto.ingenieria.ssdd.classes.Swarm;
import es.deusto.ingenieria.ssdd.classes.Tracker;
import es.deusto.ingenieria.ssdd.runable.ComprobarStorage;
import es.deusto.ingenieria.ssdd.runable.MensajeSender;
import es.deusto.ingenieria.ssdd.utils.ControllerThread;

public class TrackerConf extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private MyModel dtmPeers;
	private MyModel dtmTrackers;
	private MyModel dtmSwarm;
	private JTabbedPane jtp;
	private JPanel jp1;
	private JPanel jp2;
	private JPanel jp3;
	private JPanel jp4;
	private JTextArea IpTextArea;
	private JTextArea textArea_Port;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnSaveDB;
	private boolean active = false;
	private String tipo;
	private String ip;
	private int port;
	boolean repintar=false;
	private Runnable r;
	private ControllerThread controllerThread;
	private ComprobarStorage cs;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean getActive() {
		return active;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					TrackerConf window = new TrackerConf();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Runnable getR() {
		return r;
	}

	public void setR(Runnable r) {
		this.r = r;
	}

	public class MyModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Metodo utilizado para no editar la tabla
		 */
		public boolean isCellEditable(int row, int column) {
			if (column >= 0)
				return false;
			return true;
		}
	}

	/**
	 * Create the application.
	 */
	public TrackerConf() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();

		frame.setIconImage(
				Toolkit.getDefaultToolkit().getImage(TrackerConf.class.getResource("/resources/uTorrentIcon2-1.png")));
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 1080, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setTitle("BitTorrent Tracker");
		frame.setResizable(false);
		jtp = new JTabbedPane();
		jtp.setBackground(new Color(255, 255, 255));

		frame.getContentPane().add(jtp);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if(controllerThread!=null) {
				controllerThread.getCreateConexion().getTracker().getGenerateDatabase().closeDB();

				Path path = Paths.get("./" + controllerThread.getCreateConexion().getTracker().getId() + "-Torrent.db");

				try {
					Files.delete(path);
				} catch (IOException e) {
					System.out.println("Soy un inutil: " + e);
				}
				controllerThread.cancel();
				controllerThread.getCreateConexion().getTracker().getTrackerList().clear();
				}System.exit(0);
			}
		});
		jp1 = new JPanel();
		jp1.setBackground(new Color(204, 255, 255));
		jp4 = new JPanel();
		jp4.setBackground(new Color(204, 255, 255));
		jp1.setLayout(null);

		JPanel jp11 = new JPanel();
		jp11.setBackground(new Color(204, 255, 255));
		jp11.setBounds(15, 158, 1023, 472);
		jp1.add(jp11);
		jp11.setLayout(null);

		JLabel lb1 = new JLabel("Ip");
		lb1.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lb1.setBounds(151, 61, 28, 36);
		jp11.add(lb1);

		IpTextArea = new JTextArea();
		IpTextArea.setFont(new Font("Tahoma", Font.PLAIN, 20));
		IpTextArea.setBounds(279, 60, 480, 37);
		jp11.add(IpTextArea);

		JLabel lblPort = new JLabel("Port");
		lblPort.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblPort.setBounds(151, 125, 69, 20);
		jp11.add(lblPort);

		textArea_Port = new JTextArea();
		textArea_Port.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textArea_Port.setBounds(279, 113, 480, 37);
		jp11.add(textArea_Port);
		IpTextArea.setText("228.5.6.7");
		textArea_Port.setText("8161");

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (textArea_Port.getText().equals("") || IpTextArea.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "ERROR, please fill all the information",
							"Complete all fields", JOptionPane.ERROR_MESSAGE);
				} else {
					String Ip = IpTextArea.getText();
					int Port = Integer.parseInt(textArea_Port.getText());
					if (!validIP(Ip)) {
						JOptionPane.showMessageDialog(frame, "ERROR, please insert correct IP", "Error in IP adress",
								JOptionPane.ERROR_MESSAGE);
					} else {
						if (Port <= 99999 && Port > 1024) {
							textArea_Port.setEditable(false);
							IpTextArea.setEditable(false);
							repintar=true;
							controllerThread = new ControllerThread("KeepAlive", Ip, Port);
							btnConnect.setEnabled(false);
							btnDisconnect.setEnabled(true);
							realizarControl(controllerThread);
							updateTabla();
						} else {
							JOptionPane.showMessageDialog(frame, "ERROR, please insert correct Port", "Error in Port",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}

			}
		});
		btnConnect.setBounds(151, 252, 155, 109);
		btnConnect.setBackground(Color.GREEN);
		btnConnect.setContentAreaFilled(true);
		btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 25));
		jp11.add(btnConnect);

		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setEnabled(false);
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteDatabase();
				IpTextArea.setText("");
				textArea_Port.setText("");
				dtmTrackers.getDataVector().removeAllElements();
				dtmSwarm.getDataVector().removeAllElements();
				dtmPeers.getDataVector().removeAllElements();
				controllerThread.cancel();
				controllerThread.getCreateConexion().getTracker().getTrackerList().clear();
				controllerThread=null;
				textArea_Port.setEditable(true);
				IpTextArea.setEditable(true);
				btnConnect.setEnabled(true);
				btnDisconnect.setEnabled(false);
				btnSaveDB.setEnabled(false);
			}
		});
		btnDisconnect.setBounds(604, 252, 155, 109);
		btnDisconnect.setBackground(Color.RED);
		btnDisconnect.setContentAreaFilled(true);
		btnDisconnect.setFont(new Font("Tahoma", Font.PLAIN, 25));
		jp11.add(btnDisconnect);

		btnSaveDB = new JButton("Save DB");
		btnSaveDB.setEnabled(false);
		btnSaveDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MensajeSender ms = new MensajeSender(controllerThread.getCreateConexion().getTracker(),
						controllerThread.getCreateConexion().getMessageProducer(),
						controllerThread.getCreateConexion().getSession(), "DB");

				cs = new ComprobarStorage(controllerThread.getCreateConexion().getTracker(), ms);
				Thread thread = new Thread(cs);
				thread.start();

			}
		});
		btnSaveDB.setBounds(383, 252, 155, 109);
		btnSaveDB.setBackground(Color.YELLOW);
		btnSaveDB.setContentAreaFilled(true);
		btnSaveDB.setFont(new Font("Tahoma", Font.PLAIN, 25));
		jp11.add(btnSaveDB);

		dtmPeers = new MyModel();

		String[] columnNames1 = { "ID Transaction", "IP", "Port", "Download Kb", "Left Kb", "Time" };
		dtmPeers.setColumnIdentifiers(columnNames1);

		dtmTrackers = new MyModel();

		String[] columnNames2 = { "Id", "Ip Address", "Port", "Master", "Last Keep Alive" };
		dtmTrackers.setColumnIdentifiers(columnNames2);

		dtmSwarm = new MyModel();

		String[] columnNames3 = { "Infohash", "Size", "Leachers", "Seeders" };
		dtmSwarm.setColumnIdentifiers(columnNames3);

		JTable table3 = new JTable();
		table3.setBackground(Color.WHITE);
		table3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table3.setModel(dtmPeers);
		JPanel jp44 = new JPanel();
		jp44.setBackground(Color.WHITE);
		JScrollPane jsp44 = new JScrollPane(table3);
		jsp44.setBackground(Color.WHITE);
		jp44.setBounds(15, 158, 1023, 472);
		jp4.add(jp44);
		jp44.setLayout(new BorderLayout(0, 0));
		jp44.add(jsp44, BorderLayout.CENTER);
		jp44.setBackground(Color.WHITE);

		JLabel label1 = new JLabel();
		label1.setBounds(279, 63, 498, 55);
		label1.setFont(new Font("Tahoma", Font.BOLD, 45));
		label1.setText("Tracker Configuration");
		jp1.add(label1);
		jtp.addTab("Configuration", jp1);
		jp3 = new JPanel();
		jp3.setBackground(new Color(204, 255, 255));

		JTable table2 = new JTable(dtmTrackers);
		table2.setBackground(Color.WHITE);
		table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table2.setModel(dtmTrackers);
		JPanel jp33 = new JPanel();
		jp33.setBackground(Color.WHITE);
		JScrollPane jsp33 = new JScrollPane(table2);
		jsp33.setBackground(Color.WHITE);
		jp33.setBounds(15, 158, 1023, 472);
		jp3.add(jp33);
		jp33.setLayout(new BorderLayout(0, 0));
		jp33.add(jsp33, BorderLayout.CENTER);
		jp33.setBackground(Color.WHITE);
		jp3.setLayout(null);

		JLabel label3 = new JLabel();
		label3.setBounds(357, 63, 498, 55);
		label3.setFont(new Font("Tahoma", Font.BOLD, 45));
		label3.setText("Tracker List");
		jp3.add(label3);
		jtp.addTab("Trackers", jp3);
		jp4.setLayout(null);

		JLabel label4 = new JLabel();
		label4.setBounds(397, 63, 498, 55);
		label4.setFont(new Font("Tahoma", Font.BOLD, 45));
		label4.setText("Peer List");
		jp4.add(label4);
		jtp.addTab("Peers", jp4);
		jp2 = new JPanel();
		jp2.setBackground(new Color(204, 255, 255));

		JTable table = new JTable();
		table.setBackground(Color.WHITE);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(dtmSwarm);
		JPanel jp22 = new JPanel();
		jp22.setBackground(Color.WHITE);
		JScrollPane jsp22 = new JScrollPane(table);
		jsp22.setBackground(Color.WHITE);
		jp22.setBounds(15, 158, 1023, 472);
		jp2.add(jp22);
		jp22.setLayout(new BorderLayout(0, 0));
		jp22.add(jsp22);
		jp22.setBackground(Color.WHITE);
		jp2.setLayout(null);

		JLabel label2 = new JLabel();
		label2.setBounds(370, 63, 498, 55);
		label2.setFont(new Font("Tahoma", Font.BOLD, 45));
		label2.setText("Swarm List");
		jp2.add(label2);
		jtp.addTab("Swarms", jp2);

	}

	public void realizarControl(ControllerThread a) {
		Thread worker = new Thread() {

			public void run() {

				a.run();

			}

		};

		worker.start();
	}

	public void updateTabla() {
		Thread worker = new Thread() {

			public void run() {

				while (repintar) {
					try {

						Thread.sleep(5000);
						updateTableTrackerInfo();
						updateTablePeerInfo();
						updateTableSwarmInfo();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		};

		worker.start();
	}

	public void updateTableTrackerInfo() {
		if(controllerThread!=null) {
		frame.setTitle(" ID_TRACKER: " + controllerThread.getCreateConexion().getTracker().getId() + " MASTER: "
				+ controllerThread.getCreateConexion().getTracker().isMaster());
		ArrayList<Tracker> Trackers = new ArrayList<Tracker>();
		Trackers = controllerThread.getCreateConexion().getTracker().getTrackerList();
		if (controllerThread.getCreateConexion().getTracker().isMaster()) {
			btnSaveDB.setEnabled(true);
		}
		if (Trackers.size() != 0) {

			dtmTrackers.getDataVector().removeAllElements();
			for (int i = 0; i < Trackers.size(); i++) {
				dtmTrackers.addRow(
						new Object[] { Trackers.get(i).getId(), Trackers.get(i).getIp(), Trackers.get(i).getPort(),
								Trackers.get(i).isMaster(), Trackers.get(i).getKeepAlive().getTime().toString() });
			}

		}}
		else {
			frame.setTitle(" ID_TRACKER: " + 0 + " MASTER: "+ "FALSE ||| Generate new TRACKER ") ;
			repintar=false;
		}

	}

	public void updateTableSwarmInfo() {
		if(controllerThread!=null) {
		ArrayList<Swarm> swarm = new ArrayList<Swarm>();
		swarm = controllerThread.getCreateConexion().getTracker().getSwarmList();
		dtmSwarm.getDataVector().removeAllElements();
		if (swarm.size() != 0) {

			dtmSwarm.getDataVector().removeAllElements();
			for (int i = 0; i < swarm.size(); i++) {
				dtmSwarm.addRow(new Object[] { swarm.get(i).getInfoHash(), swarm.get(i).getSize(),
						swarm.get(i).getTotalLeecher(), swarm.get(i).getTotalSeeders() });
			}

		}}

	}

	public void updateTablePeerInfo() {
		if(controllerThread!=null) {
		HashMap<Integer, Peer> peerList = controllerThread.getCreateConexion().getTracker().getPeerList();

		if (!peerList.isEmpty()) {

			@SuppressWarnings("rawtypes")
			Iterator it = peerList.entrySet().iterator();
			dtmPeers.getDataVector().removeAllElements();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry e = (Map.Entry) it.next();
				Peer peer = (Peer) e.getValue();
				System.out.println(e.getKey() + " " + e.getValue());
				dtmPeers.addRow(new Object[] { peer.getTransaction_id(), peer.getIp(), peer.getPort(),
						peer.getDownloadMb(), peer.getLeftMb(), peer.getTime().getTime().toString() });

			}
		}
		}

	}

	public boolean validIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public void deleteDatabase() {
		controllerThread.getCreateConexion().getTracker().getGenerateDatabase().closeDB();

		Path path = Paths.get("./" + controllerThread.getCreateConexion().getTracker().getId() + "-Torrent.db");

		try {
			Files.delete(path);
		} catch (IOException e) {
			System.out.println("Soy un inutil: " + e);
		}
	}

	public void closeProcess() {

	}

	public void cancel() {
		this.active = false;
	}
}