package es.deusto.ingenieria.ssdd.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import es.deusto.ingenieria.ssdd.utils.PeerController;

@SuppressWarnings("serial")
public class PeerWindow extends JFrame {

	private JButton bSelect;
	private JButton bStart;
	private JPanel panel;
	private JProgressBar progressBar;
	private JLabel Leachers;
	private JLabel seders;
	private JFileChooser chooser;
	private JLabel lblNombre;
	private JLabel lblIp;
	private JLabel lblPort;
	private JLabel labelPort;
	private PeerController controller;

	public PeerWindow(PeerController controller) {

		super("BitTorrent Peer");
		getContentPane().setBackground(Color.CYAN);
		this.controller = controller;
		setBackground(Color.LIGHT_GRAY);
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(TrackerConf.class.getResource("/resources/uTorrentIcon2-1.png")));
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		bSelect = new JButton("Select Torrent");
		bSelect.setBounds(67, 12, 154, 26);
		panel_1.add(bSelect);
		bStart = new JButton("Stop Dowloading");
		bStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				

				
					labelPort.setText(""+0);
			
					lblIp.setText("Select new peer");

					controller.stopSendAnnounceRequest();
					bStart.setEnabled(false);
				
			}
		});
		bStart.setBounds(67, 69, 154, 26);
		panel_1.add(bStart);
		bStart.setEnabled(false);
		panel = new JPanel();
		panel.setBounds(300, 0, 284, 201);
		panel_1.add(panel);
		panel.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setForeground(Color.GREEN);
		progressBar.setFont(new Font("Dialog", Font.BOLD, 12));
		progressBar.setStringPainted(true);
		progressBar.setBounds(140, 140, 123, 14);
		panel.add(progressBar);

		JLabel lblLeechers = new JLabel("Download:");
		lblLeechers.setBounds(30, 12, 65, 16);
		panel.add(lblLeechers);

		JLabel label = new JLabel("Left:");
		label.setBounds(30, 45, 65, 16);
		panel.add(label);

		JLabel label_1 = new JLabel("Downloaded %");
		label_1.setBounds(30, 140, 92, 16);
		panel.add(label_1);

		Leachers = new JLabel(""+controller.getDonwload());
		Leachers.setBounds(94, 12, 100, 16);
		panel.add(Leachers);

		seders = new JLabel(""+controller.getFalta());
		seders.setBounds(94, 45, 100, 16);
		panel.add(seders);

		JLabel lblFileName = new JLabel("File Name:");
		lblFileName.setBounds(30, 87, 92, 16);
		panel.add(lblFileName);

		lblNombre = new JLabel("");
		lblNombre.setFont(new Font("Dialog", Font.PLAIN, 9));
		lblNombre.setBounds(32, 104, 240, 27);
		panel.add(lblNombre);
		progressBar.setValue(0);
		lblIp = new JLabel("228.5.6.7");
		lblIp.setBounds(67, 107, 154, 16);
		panel_1.add(lblIp);

		JLabel lbPORT = new JLabel("IP:");
		lbPORT.setBounds(12, 107, 55, 16);
		panel_1.add(lbPORT);

		lblPort = new JLabel("PORT:");
		lblPort.setBounds(12, 145, 55, 16);
		panel_1.add(lblPort);

		labelPort = new JLabel("7000");
		labelPort.setBounds(77, 145, 144, 16);
		panel_1.add(labelPort);
		bStart.addMouseListener(new MouseAdapter() {

		});
		chooser = new JFileChooser();
		chooser.getSelectedFile();
		bSelect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select torrent file...");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter torrentFilter = new FileNameExtensionFilter("Torrent files (*.torrent)",
						"torrent");
				chooser.setFileFilter(torrentFilter);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					lblNombre.setText(chooser.getSelectedFile().getName());
					bStart.setEnabled(true);
					realizarControl(chooser.getSelectedFile());
					updateGauge();
				}

				else {
					System.out.println("Nothing Selected");
				}
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(100, 100, 600, 240);
		this.setVisible(true);
	}

	public void realizarControl(File a) {
		Thread worker = new Thread() {

			public void run() {

				controller.connect(a);

			}

		};

		worker.start();
	}

	public void updateGauge() {
		Thread worker = new Thread() {

			public void run() {
				int minimum = progressBar.getMinimum();
				int maximum = progressBar.getMaximum();
				for (int i = minimum; i < maximum; i++) {
					try {
						int value = controller.getDonwloadGauge();
						progressBar.setValue(value);
						if(controller.getDonwload()!=0&&controller.getFalta()!=0) {
						Leachers.setText(""+((controller.getDonwload()/8)/1024)+ "Kb");
						seders.setText(""+((controller.getFalta()/8)/1024)+"Kb");
						}
						Thread.sleep(10000);

					} catch (InterruptedException ignoredException) {
					}
				}
			}

		};

		worker.start();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Error updating look & feel: " + ex);
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				new PeerWindow(new PeerController());

			}

		});

	}
}
