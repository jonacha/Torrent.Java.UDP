package es.deusto.ingenieria.ssdd.listener;

import java.util.ArrayList;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.w3c.dom.Document;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;
import es.deusto.ingenieria.ssdd.classes.Peer;
import es.deusto.ingenieria.ssdd.classes.Tracker;
import es.deusto.ingenieria.ssdd.runable.MensajeSender;

public class KeepAliveListener implements MessageListener {

	private Tracker tracker;
	private MensajeSender ms;

	public KeepAliveListener(Tracker tracker, MensajeSender ms) {
		this.tracker = tracker;
		this.ms = ms;
	}

	@Override
	public void onMessage(Message message) {
		if (message != null) {
			if (message instanceof TextMessage) {
				try {
					TextMessage txtmessage = (TextMessage) message;
					JMSMensaje parser = new JMSMensaje();
					Document xml = parser.messagesXML(txtmessage.getText());
					int id = Integer.parseInt(xml.getElementsByTagName("id").item(0).getTextContent());
					boolean isMaster = xml.getElementsByTagName("IsMaster").item(0).getTextContent().equals("true");
					String ip = xml.getElementsByTagName("ip").item(0).getTextContent();
					int port = Integer.parseInt(xml.getElementsByTagName("port").item(0).getTextContent());

					System.out.println(" - Es master:" + tracker.isMaster() + " - Id:" + tracker.getId() + " - Name:"
							+ tracker.getIp());

					if (id == 0) {
						if (tracker.isMaster()) {
							System.out.println("Master poniendo ID");
							ms.setTipo("sendId");
							ArrayList<Peer> peers = new ArrayList<Peer>();
							if (this.tracker.getGenerateDatabase() != null
									&& this.tracker.getGenerateDatabase().selectPeers() != null) {
								peers = this.tracker.getGenerateDatabase().selectPeers();
							}
							ms.mensajeObject(peers);
						}
					} else {
						ms.setTipo("keepAlive");
						boolean encontrado = false;
						for (int i = 0; i < tracker.getTrackerList().size(); i++) {
							if (tracker.getTrackerList().get(i).getId() == id) {
								encontrado = true;
								tracker.getTrackerList().get(i).setIp(ip);
								tracker.getTrackerList().get(i).setPort(port);
								tracker.getTrackerList().get(i).actualizarDate();
								break;
							}
						}
						if (!encontrado) {
							Tracker t = new Tracker(id, ip, port, isMaster);
							tracker.addTracker(t);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("# KeepALiveListener error: " + e.getMessage());
				}
			}
		}
	}
}
