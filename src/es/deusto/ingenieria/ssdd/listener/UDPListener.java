package es.deusto.ingenieria.ssdd.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@SuppressWarnings("unused")
public class UDPListener {
	private static final String DEFAULT_IP = "127.0.0.1";
	private static final int DEFAULT_PORT = 7000;

	UDPListener() throws UnknownHostException {
		String serverIP = InetAddress.getLocalHost().getHostAddress();

		int serverPort = 7000;
		int clientCount = 0;
		try (DatagramSocket udpSocket = new DatagramSocket(serverPort, InetAddress.getByName(serverIP))) {
			DatagramPacket request = null;
			DatagramPacket reply = null;
			byte[] buffer = new byte[1024];

			System.out.println(" - Waiting for connections '" + udpSocket.getLocalAddress().getHostAddress() + ":"
					+ serverPort + "' ...");

			while (true) {
				request = new DatagramPacket(buffer, buffer.length);
				udpSocket.receive(request);
				System.out.println(
						" - Received a request from '" + request.getAddress().getHostAddress() + ":" + request.getPort()
								+ "' -> " + new String(request.getData()) + " [" + request.getLength() + " byte(s)]");

				reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
						request.getPort());
				udpSocket.send(reply);
				System.out.println(" - Sent a reply to '" + reply.getAddress().getHostAddress() + ":" + reply.getPort()
						+ "' -> " + new String(reply.getData()) + " [" + reply.getLength() + " byte(s)]");
			}
		} catch (SocketException e) {
			System.err.println("# UDPServer Socket error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("# UDPServer IO error: " + e.getMessage());
		}
	}

	public void UDPMessage() {

	}
}
