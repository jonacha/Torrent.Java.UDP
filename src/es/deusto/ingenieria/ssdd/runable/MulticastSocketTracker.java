package es.deusto.ingenieria.ssdd.runable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import bitTorrent.tracker.protocol.udp.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.ConnectRequest;
import bitTorrent.tracker.protocol.udp.ConnectResponse;
import bitTorrent.tracker.protocol.udp.Error;
import bitTorrent.tracker.protocol.udp.PeerInfo;
import bitTorrent.tracker.protocol.udp.ScrapeInfo;
import bitTorrent.tracker.protocol.udp.ScrapeRequest;
import bitTorrent.tracker.protocol.udp.ScrapeResponse;
import bitTorrent.tracker.protocol.udp.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.BitTorrentUDPMessage;
import es.deusto.ingenieria.ssdd.classes.Peer;
import es.deusto.ingenieria.ssdd.classes.Swarm;
import es.deusto.ingenieria.ssdd.classes.Tracker;

public class MulticastSocketTracker implements Runnable {

	private MulticastSocket socketMulticast;
	private DatagramSocket socketUDP;
	private InetAddress group;
	private DatagramPacket peerMensaje;
	volatile boolean cancel = false;
	
	private Tracker tracker;

	
	
	public MulticastSocketTracker(Tracker tracker) {
		try {
			this.tracker = tracker;
			this.socketMulticast = new MulticastSocket(7000);
			this.group = InetAddress.getByName("228.5.6.7");
			this.socketMulticast.joinGroup(group);

		} catch (IOException e) {
			System.out.println("ERROR creating and/or joining a multicast group in 'MulticastSocketTracker'!");
			e.printStackTrace();
		}
		;
		try {
			socketUDP = new DatagramSocket();
			System.out.println(
					"UDP socket Opened! In ip: " + group.getHostAddress() + " Port: " + socketMulticast.getPort());
		} catch (SocketException e) {
			System.out.println("ERROR creating a datagramsocket to the tracker with IP:"+tracker.getIp());
			e.printStackTrace();
		}
		this.peerMensaje = null;
	}

	@Override
	public void run() {
		while (!cancel) {

			System.out.println("MulticastSocket: waiting for  messages...");
			byte[] buffer = new byte[1024];
			this.peerMensaje = new DatagramPacket(buffer, buffer.length);
			try {
				this.socketMulticast.receive(peerMensaje);

				if (!peerMensaje.getAddress().equals(group)) {
					String ipPeer = peerMensaje.getAddress().getHostAddress();
					int portPeer = peerMensaje.getPort();
					// int destinationPort = portPeer;
					int tamMensaje = peerMensaje.getLength();
					if (tamMensaje == 16) {
						ConnectRequest coRequest = ConnectRequest.parse(peerMensaje.getData());
						if (coRequest != null) {
							long conecId = coRequest.getConnectionId();
							int transaction_id = coRequest.getTransactionId();
							Action action = coRequest.getAction();
							if (conecId == 4497486125440L) { //por defecto el conec 
								if (action.compareTo(Action.CONNECT) == 0) {
									System.out.println("ID: " + tracker.getId()
											+ " The UDP message received was a ConnectRequest!");
									Peer p = new Peer(portPeer, ipPeer);
									p.setTransaction_id(transaction_id);
									ConnectResponse Coresponse = conetResponse(transaction_id);
									p.setConnection_id(Coresponse.getConnectionId());
									tracker.getPeerList().put(transaction_id, p);
									if (this.tracker.isMaster()) {
										sendUDPPeer(Coresponse, ipPeer, portPeer);

									}

								} else {
									if (this.tracker.isMaster()) {
										ErrorToPeer("Action Incorret", transaction_id, ipPeer, portPeer);
									}
								}
							} else {
								if (this.tracker.isMaster()) {
									ErrorToPeer(
											"Conection id Incorret the conecction id is:" + conecId
													+ " and the correct should be:" + 4497486125440L,
											transaction_id, ipPeer, portPeer);
								}
							}

						} else {
							if (this.tracker.isMaster()) {
								ErrorToPeer("The mensaje conect request is doesn´t have conteind", 0, ipPeer, portPeer);
							}
						}

					} else if (tamMensaje == 98) {
						AnnounceRequest annoRequest = AnnounceRequest.parse(peerMensaje.getData());
						if (annoRequest != null) {
							System.out.println(
									"ID: " + tracker.getId() + "  The UDP message received was a AnnounceRequests!");
							String infoHash = annoRequest.getHexInfoHash();
							int transaction_id = annoRequest.getTransactionId();
							Action action = annoRequest.getAction();

							if (action.compareTo(Action.ANNOUNCE) == 0) {

								long connectionIdA = annoRequest.getConnectionId();
								long downloaded = annoRequest.getDownloaded();
								long uploaded = annoRequest.getUploaded();
								long left = annoRequest.getLeft();
								int tamaño = (int) (downloaded + left);
								Swarm swarm = new Swarm(infoHash, tamaño);

								if (!this.tracker.haveSwarm(swarm.getInfoHash())) {
									this.tracker.getSwarmList().add(swarm);
								}
								if (!tracker.getPeerList().isEmpty()) {
									if (tracker.getPeerList().containsKey(transaction_id)) {
										tracker.getPeerList().get(transaction_id).setDowload(downloaded);
										tracker.getPeerList().get(transaction_id).setUpload(uploaded);
										tracker.getPeerList().get(transaction_id).setLeft(left);
										tracker.getPeerList().get(transaction_id).setTime(Calendar.getInstance());

									}
								} else {

									Peer peer = new Peer(ipPeer, portPeer, transaction_id, downloaded, left);
									tracker.getPeerList().put(transaction_id, peer);
								}
								Peer temp = tracker.getPeerList().get(transaction_id);

								AnnounceResponse ann_response = AnnounceResponse(connectionIdA, transaction_id,
										infoHash, downloaded, uploaded, left, temp);

								if (this.tracker.isMaster()) {
									sendUDPPeer(ann_response, ipPeer, portPeer);
								}

							} else {
								if (this.tracker.isMaster()) {
									ErrorToPeer("The tipe doesnt is announce request messaje", transaction_id, ipPeer,
											portPeer);
								}
							}

						} else {
							if (this.tracker.isMaster()) {
								ErrorToPeer("The mensaje announce request is doesn´t have contein", 0, ipPeer, portPeer);
							}
						}
					}
					else {
						ScrapeRequest scraRequest = ScrapeRequest.parse(Arrays.copyOfRange(peerMensaje.getData(), 0, peerMensaje.getLength()));

						if (scraRequest != null) {
							int transaction_id = scraRequest.getTransactionId();
							System.out.println(
									"ID: " + tracker.getId() + "The UDP message received was a ScrapeRequest!With trasaction ID"+scraRequest.getTransactionId());

							Action action = scraRequest.getAction();
							if (action.compareTo(Action.SCRAPE) == 0) {

								Peer p = tracker.getPeerList().get(transaction_id);
//								tracker.getPeerList().remove(transaction_id);
								ScrapeResponse scrape_response = prepareScrapeResponse(transaction_id, null);
								if (this.tracker.isMaster()) {
									System.out.println("The peer with the trassationID:" + p.getTransaction_id()
											+ " Have been finished and deleted ");
									sendUDPPeer(scrape_response, ipPeer, portPeer);

								}

							} else {
								if (this.tracker.isMaster()) {
									ErrorToPeer("The tipe doesn 't is scrape request messaje", transaction_id, ipPeer,
											portPeer);
								}

							}

						} else {

							if (this.tracker.isMaster()) {
								ErrorToPeer("The mensaje scaperequest request is doesn´t have contein", 0, ipPeer,
										portPeer);
							}
						}

					}

				}

			} catch (IOException e) {
				System.out.println("ERROR reciving an incoming message in 'MulticastSocketTracker'!");
				e.printStackTrace();
			}
		}
	}


	public void sendUDPPeer(BitTorrentUDPMessage msg, String IP, int port) {
		InetAddress serverHost;
		try {
			serverHost = InetAddress.getByName(IP);
			byte[] byteMsg = msg.getBytes();

			DatagramPacket outgoingMessage = new DatagramPacket(byteMsg, byteMsg.length, serverHost, port);
			this.socketUDP.send(outgoingMessage);
		} catch (IOException e) {
			System.out.println("Error sending the mensaje to the ip:"+IP+" Port: " +port);
			e.printStackTrace();
		}
	}

	public void cancel() {
		cancel = true;
		Thread.currentThread().interrupt();
		try {
			this.socketMulticast.leaveGroup(this.group);
		} catch (IOException e) {
			System.out.println("ERROR leaving multicast group in 'MulticastSocketTracker'!");
			e.printStackTrace();
		}
	}

	public ConnectResponse conetResponse(int transactionId) {
		ConnectResponse response = new ConnectResponse();
		response.setAction(Action.CONNECT);
		response.setTransactionId(transactionId);
		response.setConnectionId(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE));
		return response;
	}

	public AnnounceResponse AnnounceResponse(long connectionId, int transactionId, String stringinfohash,
			long downloaded, long uploaded, long left, Peer peer) {
			AnnounceResponse response = new AnnounceResponse();
			response.setAction(Action.ANNOUNCE);
			response.setTransactionId(transactionId);	
			response.setLeechers(0);
			response.setSeeders(0);
			ArrayList<PeerInfo> peerInfos = new ArrayList<PeerInfo>();
			PeerInfo pf = new PeerInfo();
			pf.setIpAddress(convertIpAddressToInt(peer.getIp()));
			pf.setPort(peer.getPort());
			peerInfos.add(pf);
			response.setPeers(peerInfos);
		return response;
	}
	public int convertIpAddressToInt(String ip) {

		int result = 0;
		InetAddress temp = null;
		try {
			temp = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (temp != null) {
			for (byte b : temp.getAddress()) {
				result = result << 8 | (b & 0xFF);
			}
		}
		return result;

	}
	public ScrapeResponse prepareScrapeResponse(int transactionid, List<ScrapeInfo> scrapeInfoList) {
		ScrapeResponse response = new ScrapeResponse();
		response.setAction(Action.SCRAPE);
		response.setTransactionId(transactionid);
		return response;
	}

	public Error Error(String errormessage, int transactionId) {
		Error response = new Error();
		response.setAction(Action.ERROR);
		response.setTransactionId(transactionId);
		response.setMessage(errormessage);
		return response;
	}

	private void ErrorToPeer(String msg, int transactionID, String ip, int destinationport) {
		Error error = Error(msg, transactionID);
		sendUDPPeer(error, ip, destinationport);
	}




}
