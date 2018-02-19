package es.deusto.ingenieria.ssdd.utils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.TorrentInfo;
import bitTorrent.metainfo.handler.MetainfoHandler;
import bitTorrent.metainfo.handler.MetainfoHandlerSingleFile;
import bitTorrent.tracker.protocol.udp.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.ConnectRequest;
import bitTorrent.tracker.protocol.udp.ConnectResponse;
import bitTorrent.tracker.protocol.udp.PeerInfo;
import bitTorrent.tracker.protocol.udp.ScrapeRequest;
import bitTorrent.tracker.protocol.udp.ScrapeResponse;
import bitTorrent.tracker.protocol.udp.AnnounceRequest.Event;
import bitTorrent.tracker.protocol.udp.BitTorrentUDPMessage.Action;

public class PeerController {

	private ConnectResponse connectResponse;
	private AnnounceResponse announceResponse;
	private ScrapeResponse scrapeResponse;
	private DatagramSocket socketSendTracker;
	private DatagramSocket socketGetTracker;
	private ServerSocket peerListenerSocket;
	private MulticastSocket socketMulticast;
	private int falta;
	private boolean stop = false;

	private int idPeer = ThreadLocalRandom.current().nextInt(1000000000, Integer.MAX_VALUE);
	private InetAddress multicastIP;
	private InetAddress group;

	private static long connectionId = 4497486125440L;
	private static int transactionID = -1;
	private static final int UdpPort = 7000;
	private static int peerListenerPort;
	private int donwload = 0;
	private int donwloadGauge = 0;
	private int total = 0;
	@SuppressWarnings("unused")
	private MetainfoHandlerSingleFile torrentSingleFile;

	public void connect(File torrent) {
		TorrentInfo torrentInfo = new TorrentInfo();
		@SuppressWarnings("rawtypes")
		MetainfoHandler metaInfoFromTorrent = torrentInfo.extractInformationFromFile(torrent);

		if (metaInfoFromTorrent instanceof MetainfoHandlerSingleFile) {
			MetainfoHandlerSingleFile file = (MetainfoHandlerSingleFile) metaInfoFromTorrent;
			torrentSingleFile = file;
			System.out.println("File data: \n" + file.getMetainfo());

			if (socketSendTracker == null) {
				try {

					try {
						this.socketMulticast = new MulticastSocket(7000);
						this.group = InetAddress.getByName("228.5.6.7");
						this.socketMulticast.joinGroup(group);
						multicastIP = InetAddress.getByName("228.5.6.7");
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					socketSendTracker = new DatagramSocket();

					socketGetTracker = socketSendTracker;

					Random rn = new Random();
					int random = 0 + rn.nextInt(32766 - 0 + 1);

					boolean itsfreeport = false;
					while (!itsfreeport) {
						try {
							peerListenerSocket = new ServerSocket(random);
							System.out.println("Puerto: " + random);
							itsfreeport = true;
						} catch (Exception ex) {
							itsfreeport = false;
						}
					}
					peerListenerPort = peerListenerSocket.getLocalPort();
				} catch (SocketException e) {
					System.err.println("Error creating conexion UDP to the peer.");
					e.printStackTrace();
				}
			}
			transactionID = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
			receiveConnetTracker(file, socketSendTracker, socketGetTracker);
			this.stop = true;
			System.out.println("Transaction id of the connect resposnse recive of the Tracker: "
					+ connectResponse.getTransactionId());
			this.falta = (int) file.getMetainfo().getInfo().getLength();
			this.total = falta;
			sendAnnounceRequest(file);
		}
	}

	public void receiveConnetTracker(MetainfoHandlerSingleFile file, DatagramSocket socketSendTracker,
			DatagramSocket UDPListener) {
		try {
			UDPListener.setSoTimeout(10000);

			byte[] buffer = new byte[1024];

			boolean Conenctresponse = false;
			do {
				try {
					ConnectRequest requestConnection = createConnectRequest();
					byte[] requestBytes = requestConnection.getBytes();
					DatagramPacket connectRequest = new DatagramPacket(requestBytes, requestBytes.length, multicastIP,
							UdpPort);
					socketSendTracker.send(connectRequest);
					System.out.println(" Sending ConnectRequest  " + connectRequest.getAddress().getHostAddress() + ":"
							+ connectRequest.getPort());
					DatagramPacket responseTracker = new DatagramPacket(buffer, buffer.length);
					this.socketMulticast.receive(responseTracker);
					UDPListener.receive(responseTracker);
					if (responseTracker.getLength() >= 16) {
						connectResponse = ConnectResponse.parse(responseTracker.getData());
						if (connectResponse.getAction() != null) {
							if (connectResponse.getAction().equals(Action.CONNECT)) {
								if (connectResponse.getTransactionId() == transactionID) {
									System.out.println("Receive ConnectResponse of the tracke with the transaction:  "
											+ transactionID);
									connectionId = connectResponse.getConnectionId();
									Conenctresponse = true;
								}

							}
						}
					}
				} catch (SocketTimeoutException e) {
					System.err.println("TimeOut receiving conect response of the tracker ");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (!Conenctresponse);
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
	}

	private ConnectRequest createConnectRequest() {
		ConnectRequest request = new ConnectRequest();
		request.setAction(Action.CONNECT);
		request.setTransactionId(transactionID);
		return request;

	}

	public void sendAnnounceRequest(MetainfoHandlerSingleFile single) {
		Thread worker = new Thread() {

			public void run() {

				while (stop) {
					try {
						receivelAnnounceTracker(single, socketSendTracker, socketGetTracker);

						Thread.sleep(5000);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				createScrapeResponse();
			}

		};

		worker.start();
	}

	public void receivelAnnounceTracker(MetainfoHandlerSingleFile file, DatagramSocket socketSendTracker,
			DatagramSocket UDPListener) {
		try {
			UDPListener.setSoTimeout(8000);
			byte[] buffer = new byte[1024];
			boolean responseReceivedAnnounce = false;

			do {
				try {

					InfoDictionarySingleFile info = file.getMetainfo().getInfo();
					this.falta = info.getLength() - this.donwload;
			
					AnnounceRequest request = createAnnounceRequest(info.getInfoHash(),
							convertIpAddressToInt(InetAddress.getLocalHost().getAddress()), peerListenerPort);
					byte[] requestBytes = request.getBytes();
					DatagramPacket messageOut = new DatagramPacket(requestBytes, requestBytes.length, multicastIP,
							UdpPort);
					socketSendTracker.send(messageOut);

					System.out.println("Sending a AnnounceRequest to IP:" + messageOut.getAddress().getHostAddress()
							+ " Port: " + messageOut.getPort());
					System.out
							.println("Sending AnnounceRequest with the trasacction id: " + request.getTransactionId());
					DatagramPacket responseAnnounceTracker = new DatagramPacket(buffer, buffer.length);
					UDPListener.receive(responseAnnounceTracker);
					if (responseAnnounceTracker.getLength() >= 16) {
						announceResponse = AnnounceResponse.parse(responseAnnounceTracker.getData());
						if (announceResponse != null) {
							if (announceResponse.getTransactionId() == transactionID) {

								this.donwload = this.donwload + 3000000;
								responseReceivedAnnounce = true;
								System.out.println("Response of the tracker master receive with the trasaction id: "
										+ announceResponse.getTransactionId());
							}
						}
					}
				} catch (SocketTimeoutException e) {
					System.out.println("Timeout reiving announce response of tracker ");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (!responseReceivedAnnounce);

		} catch (SocketException e2) {
			e2.printStackTrace();
		}

	}

	private AnnounceRequest createAnnounceRequest(byte[] infoHash, int ipaddress, int port) {
		AnnounceRequest AnnounceRequest = new AnnounceRequest();
		AnnounceRequest.setConnectionId(connectionId);
		AnnounceRequest.setAction(Action.ANNOUNCE);
		AnnounceRequest.setTransactionId(transactionID);
		AnnounceRequest.setPeerId(idPeer + "");
		AnnounceRequest.setInfoHash(infoHash);
		AnnounceRequest.setDownloaded(this.donwload);
		AnnounceRequest.setLeft(this.falta);
		AnnounceRequest.setUploaded(0);
		AnnounceRequest.setEvent(Event.NONE);
		PeerInfo pi = new PeerInfo();
		pi.setIpAddress(ipaddress);
		pi.setPort(port);
		AnnounceRequest.setPeerInfo(pi);
		AnnounceRequest.setKey(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
		AnnounceRequest.setNumWant(-1);
		return AnnounceRequest;

	}

	public void createScrapeResponse() {
		try {
			socketGetTracker.setSoTimeout(8000);
			byte[] buffer = new byte[1024];
			boolean responseReceived = false;
			while (!responseReceived) {
				ScrapeRequest request = createScrapeRequest();
				byte[] requestBytes = request.getBytes();
				DatagramPacket messageOut = new DatagramPacket(requestBytes, requestBytes.length, multicastIP, UdpPort);
				System.out.println("UDP ScrapeRequest message sent to the multicast group "+ multicastIP.getHostAddress() + ":" + UdpPort );
				System.out.println("Send ScrapeRequest message with trassactionid: " + request.getTransactionId());
				try {
					socketSendTracker.send(messageOut);
				} catch (IOException e) {
					System.err.println("# Socket Error ('sendScrapeRequest'): " + e.getMessage());
					e.printStackTrace();
				}
				DatagramPacket response = new DatagramPacket(buffer, buffer.length);
				try {
					socketGetTracker.receive(response);
					if (response.getLength() >= 4) {
						scrapeResponse = ScrapeResponse.parse(response.getData());
						if (scrapeResponse != null) {
							if (scrapeResponse.getAction().equals(Action.SCRAPE)) {
								if (scrapeResponse.getTransactionId() == transactionID) {
									responseReceived = true;
									System.out.println(
											"ScrapeResponse received! the peer have download: " + ((this.donwload/8)/1024)+"Kb");
								}
							}
						}
					}
				} catch (SocketTimeoutException e1) {
					System.out.println("ERROR: Timeout exception in 'ScrapeResponseReceivedLoop'");
				}
			}
		} catch (IOException e) {
			System.out.println("ERROR occurred receiving from the listening socket in 'ScrapeResponseReceivedLoop'");
			e.printStackTrace();
		}
	}

	private ScrapeRequest createScrapeRequest() {
		ScrapeRequest scrape = new ScrapeRequest();
		scrape.setConnectionId(connectionId);
		scrape.setTransactionId(transactionID);
		scrape.setAction(Action.SCRAPE);
		
		scrape.addInfoHash("523F166396E347E4B86D096065481D314EA5B0DB"); 

		return scrape;
	}

	public String openFileToString(byte[] _bytes)
	{
	    String file_string = "";

	    for(int i = 0; i < _bytes.length; i++)
	    {
	        file_string += (char)_bytes[i];
	    }

	    return file_string;    
	}
	public void stopSendAnnounceRequest() {
		this.stop = false;

	}

	public int convertIpAddressToInt(byte[] ip) {

		int result = 0;
		if (ip != null) {
			for (byte b : ip) {
				result = result << 8 | (b & 0xFF);
			}
		}
		return result;

	}

	public static int getTransactionID() {
		return transactionID;
	}

	public int getFalta() {
		return falta;
	}

	public void setFalta(int falta) {
		this.falta = falta;
	}

	public int getDonwload() {
		return donwload;
	}

	public void setDonwload(int donwload) {
		this.donwload = donwload;
	}

	public int getDonwloadGauge() {
		if (this.donwload != 0) {
			this.donwloadGauge = (this.donwload / 100) / (this.total / 10000);
		}
		return donwloadGauge;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}