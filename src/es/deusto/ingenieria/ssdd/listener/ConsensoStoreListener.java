package es.deusto.ingenieria.ssdd.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.w3c.dom.Document;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;
import es.deusto.ingenieria.ssdd.classes.Tracker;
import es.deusto.ingenieria.ssdd.runable.MensajeSender;

public class ConsensoStoreListener implements MessageListener {

	private Tracker tracker;
	private MensajeSender ms;

	public ConsensoStoreListener(Tracker tracker, MensajeSender ms) {
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

					if (this.tracker.getId() != id) {
						for (int i = 0; i < this.tracker.getTrackerList().size(); i++) {
							Tracker t = this.tracker.getTrackerList().get(i);
							if (t.getId() == id) {
								this.tracker.getTrackerList().get(i).setStorage(true);
								if (t.isMaster()) {
									ms.mensajeString();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("# ConsensoStoreListener error: " + e.getMessage());
				}
			}
		}
	}

}
