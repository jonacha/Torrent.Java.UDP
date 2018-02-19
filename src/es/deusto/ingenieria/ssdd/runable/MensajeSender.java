package es.deusto.ingenieria.ssdd.runable;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;
import es.deusto.ingenieria.ssdd.classes.Tracker;

public class MensajeSender {

	private Tracker tracker;
	private MessageProducer producer;
	private Session session;
	private String tipo;

	public MensajeSender(Tracker tracker, MessageProducer producer, Session session, String tipo) {
		this.tracker = tracker;
		this.producer = producer;
		this.session = session;
		this.tipo = tipo;
	}

	public void mensajeString() {
		String str = new JMSMensaje().messagesString(tracker, tipo);
		TextMessage msg = null;
		try {
			msg = session.createTextMessage();
			msg.setText(str);

			if (!tracker.isMaster()) {
				Thread.sleep(5000);
				producer.send(msg);
			} else {
				Thread.sleep(1000);
				producer.send(msg);
			}
		} catch (JMSException | InterruptedException e) {
			System.out.println("Error JMS with the KeepALiveSender");
			System.out.println("ERRRO with Thread sleep in keep alive sending");
			e.printStackTrace();
		}
	}

	public void mensajeObject(Object object) {
		String str = new JMSMensaje().messagesString(tracker, tipo);
		System.out.println("Hola str" + str);
		try {
			ObjectMessage objectMessage = session.createObjectMessage();
			objectMessage.setObject((Serializable) object);
			objectMessage.setStringProperty("str", str);
			producer.send(objectMessage);
			Thread.sleep(1000);
		} catch (JMSException | InterruptedException e) {
			System.out.println("Error JMS with the KeepALiveSender");
			System.out.println("ERRRO with Thread sleep in keep alive sending");
			e.printStackTrace();
		}
	}

	public Tracker getTracker() {
		return tracker;
	}

	public void setTracker(Tracker tracker) {
		this.tracker = tracker;
	}

	public MessageProducer getProducer() {
		return producer;
	}

	public void setProducer(MessageProducer producer) {
		this.producer = producer;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
