package es.deusto.ingenieria.ssdd.runable;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;
import es.deusto.ingenieria.ssdd.classes.Tracker;

public class ComprobarStorage implements Runnable {

	private Tracker tracker;
	private boolean cancel = false;
	private MensajeSender ms;

	public ComprobarStorage(Tracker tracker, MensajeSender ms) {
		this.tracker = tracker;
		this.ms = ms;

	}

	@Override
	public void run() {
		while (!cancel) {
			this.tracker.checkSaveDB();

			try {
				Thread.sleep(11000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String saveDBstr = new JMSMensaje().messagesString(ms.getTracker(), "SaveDB");
			TextMessage msg = null;
			try {
				msg = ms.getSession().createTextMessage();
				msg.setText(saveDBstr);
				ms.getProducer().send(msg);
			} catch (JMSException e) {
				System.out.println("Error JMS with the KeepALiveSender");
				System.out.println("ERRRO with Thread sleep in keep alive sending");
				e.printStackTrace();
			}
			System.out.println("cancel");
			this.cancel = true;

		}
	}

	public void cancel() {
		this.cancel = true;
	}
}
