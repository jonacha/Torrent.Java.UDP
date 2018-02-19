package es.deusto.ingenieria.ssdd.runable;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import es.deusto.ingenieria.ssdd.JMS.JMSMensaje;

public class KeepAliveRunnable implements Runnable {

	private MensajeSender ms;
	private boolean cancel = false;

	public KeepAliveRunnable(MensajeSender ms) {
		this.ms = ms;
	}

	@Override
	public void run() {
		while (!cancel) {
			String keepalivestr = new JMSMensaje().messagesString(ms.getTracker(), "KeepAlive");
			TextMessage msg = null;
			try {
				msg = ms.getSession().createTextMessage();
				msg.setText(keepalivestr);
				ms.getProducer().send(msg);
				Thread.sleep(2000);
			} catch (JMSException | InterruptedException e) {
				System.out.println("Error JMS with the KeepALiveSender");
				System.out.println("ERRRO with Thread sleep in keep alive sending");
				e.printStackTrace();
			}
		}

	}

	public void cancel() {
		this.cancel = true;
	}
}