package es.deusto.ingenieria.ssdd.utils;

import javax.jms.JMSException;

import es.deusto.ingenieria.ssdd.database.GenerateDatabase;

public class ControllerThread implements Runnable {

	private CreateConexion createConexion;

	public CreateConexion getCreateConexion() {
		return createConexion;
	}

	public void setCreateConexion(CreateConexion createConexion) {
		this.createConexion = createConexion;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private boolean active = true;
	private String tipo;
	private String ip;
	private int port;
	private boolean creado = false;

	public ControllerThread(String tipo, String ip, int port) {
		this.tipo = tipo;
		this.ip = ip;
		this.port = port;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		createConexion = new CreateConexion(tipo, ip, port);
		Thread thread = new Thread(createConexion.getKa());
		Thread thread2 = new Thread(createConexion.getCa());
		Thread thread3 = new Thread(createConexion.getMt());
		thread.start();
		thread2.start();
		thread3.start();

		while (active) {
			try {

				thread.sleep(5000);
				if (createConexion.getTracker().areTrackers()) {
					createConexion.getTracker().setMaster(true);
					createConexion.getTracker().setId(1);
				}
				if (createConexion.getTracker().getId() > 1) {
					this.creado = true;
				}
				if (createConexion.getTracker().getId() == 1 && createConexion.getTracker().isMaster()
						&& !this.creado) {
					createConexion.getTracker().setGenerateDatabase(new GenerateDatabase("1-Torrent.db"));
					createConexion.getTracker().getGenerateDatabase().iniciateDatabase();
					this.creado = true;
				}

			} catch (Exception e) {

			}
		}
		if (createConexion.getConnection() != null) {
			createConexion.getCa().cancel();
			createConexion.getKa().cancel();
			try {
				createConexion.getConsumer().close();
				createConexion.getConsumer2().close();
				createConexion.getConsumer3().close();
				createConexion.getConsumer4().close();
				createConexion.getConnection().close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	public void cancel() {
		this.active = false;
		this.createConexion.getTracker().setMaster(false);
		try {
			createConexion.getConsumer().close();
			createConexion.getConsumer2().close();
			createConexion.getConsumer3().close();
			createConexion.getConsumer4().close();
			createConexion.getCa().cancel();
			createConexion.getMt().cancel();
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
