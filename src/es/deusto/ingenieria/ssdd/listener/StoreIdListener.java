package es.deusto.ingenieria.ssdd.listener;

import java.util.ArrayList;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.w3c.dom.Document;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;
import es.deusto.ingenieria.ssdd.classes.Peer;
import es.deusto.ingenieria.ssdd.classes.Tracker;
import es.deusto.ingenieria.ssdd.database.GenerateDatabase;

public class StoreIdListener implements MessageListener {
	private Tracker tracker;

	public StoreIdListener(Tracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public void onMessage(Message message) {
		if (message != null) {
			if (message instanceof ObjectMessage) {

				try {
					if (tracker.getId() == 0) {
						String str = message.getStringProperty("str");
						if (str != null) {
							@SuppressWarnings("unchecked")
							ArrayList<Peer> peerList = ((ArrayList<Peer>) ((ObjectMessage) message).getObject());
							JMSMensaje parser = new JMSMensaje();
							Document xml = parser.messagesXML(str);
							int id = Integer.parseInt(xml.getElementsByTagName("id").item(0).getTextContent());

							if (this.tracker.getId() != id) {
								int newId = 0;
								if (xml.getElementsByTagName("newid").item(0).getTextContent() != ""
										&& this.tracker.getId() == 0) {
									newId = Integer
											.parseInt(xml.getElementsByTagName("newid").item(0).getTextContent());
									System.out.println(this.tracker.getId() + " Cambiada por " + newId);
									if (newId != 0) {
										this.tracker.setId(newId);

										this.tracker.setGenerateDatabase(new GenerateDatabase(newId + "-Torrent.db"));
										this.tracker.getGenerateDatabase().iniciateDatabase();
										for (int i = 0; i < peerList.size(); i++) {
											this.tracker.getGenerateDatabase().newPeer(peerList.get(i));
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("# StoreIdListener error: " + e.getMessage());
				}
			}
		}
	}
}
