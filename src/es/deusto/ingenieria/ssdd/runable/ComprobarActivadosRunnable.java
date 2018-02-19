package es.deusto.ingenieria.ssdd.runable;

import es.deusto.ingenieria.ssdd.classes.Tracker;

public class ComprobarActivadosRunnable implements Runnable {

	private Tracker tracker;
	private boolean cancel = false;

	public ComprobarActivadosRunnable(Tracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public void run() {
		while (!cancel) {
			this.tracker.checkTracker();
		}
	}

	public void cancel() {
		this.cancel = true;
	}
}
