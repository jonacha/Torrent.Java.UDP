package es.deusto.ingenieria.ssdd.runable;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;

public class UpdateDatabaseRunnable implements Runnable {
	private MensajeSender ms;

	public UpdateDatabaseRunnable(MensajeSender ms) {
		this.ms = ms;
	}

	@Override
	public void run() {
		try {
			String updateDatabasetr = new JMSMensaje().messagesString(ms.getTracker(), ms.getTipo());
			TextMessage msg = ms.getSession().createTextMessage();
			msg.setText(updateDatabasetr);

			Thread.sleep(2000);

		} catch (JMSException | InterruptedException e) {
			System.out.println("Error JMS with the UpdateDatabaseSender");
			System.out.println("ERRRO with Thread sleep in update database sending");
			e.printStackTrace();
		}

	}
}