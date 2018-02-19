package es.deusto.ingenieria.ssdd.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.w3c.dom.Document;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;
import es.deusto.ingenieria.ssdd.runable.MensajeSender;

public class UpdateDatabaseListener implements MessageListener {

	private MensajeSender ms;

	public UpdateDatabaseListener(MensajeSender ms) {
		this.ms = ms;

	}

	@Override
	public void onMessage(Message message) {
		System.out.println("Me cree");
		if (message != null) {
			if (message instanceof TextMessage) {
				try {
					boolean exec = false;
					TextMessage txtmessage = (TextMessage) message;
					JMSMensaje parser = new JMSMensaje();
					Document xml = parser.messagesXML(txtmessage.getText());
					int id = Integer.parseInt(xml.getElementsByTagName("id").item(0).getTextContent());
					System.out.println("Soy: " + id + " y vamos a guardar en BD");
					if (ms.getTracker().getId() != id) {
						for (int i = 0; i < ms.getTracker().getTrackerList().size(); i++) {
							if (ms.getTracker().getTrackerList().get(i).getId() == id) {
								ms.getTracker().getTrackerList().get(i).setStorage(true);
								if (xml.getElementsByTagName("IsMaster").item(0).getTextContent().equals("true")) {
									exec = true;
									System.out.println("Si, era master");
									ms.mensajeString();
								}
								break;
							}
						}
						if (exec) {
							Thread.sleep(10000);
							ms.getTracker().checkStorage();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("# UpdateDatabaseListener error: " + e.getMessage());
				}
			}
		}
	}
}
