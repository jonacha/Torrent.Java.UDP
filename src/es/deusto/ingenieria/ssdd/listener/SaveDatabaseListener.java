package es.deusto.ingenieria.ssdd.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.w3c.dom.Document;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;

import es.deusto.ingenieria.ssdd.classes.Tracker;

public class SaveDatabaseListener implements MessageListener {

	private Tracker tracker;

	public SaveDatabaseListener(Tracker tracker) {
		this.tracker = tracker;
	}

	@SuppressWarnings("unused")
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
					String type = xml.getElementsByTagName("type").item(0).getTextContent();

					if (type.equals("SaveDB")) {
						this.tracker.saveDB();
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("# SaveDatabaseListener error: " + e.getMessage());
				}
			}
		}
	}
}
