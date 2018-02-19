package es.deusto.ingenieria.ssdd.classes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import es.deusto.ingenieria.ssdd.database.GenerateDatabase;

public class Tracker extends Observable {
	private int id = 0;
	private String ip;
	private int port;
	private boolean isMaster = false;
	private Calendar KeepAlive = Calendar.getInstance();
	private boolean storage = false;
	private ArrayList<Tracker> trackerList;
	private HashMap<Integer, Peer> peerList;
	private GenerateDatabase generateDatabase;
	private ArrayList<Swarm> swarmList;

	public Tracker(int id) {
		this.id = id;
	}

	public Tracker(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.trackerList = new ArrayList<Tracker>();
		swarmList = new ArrayList<Swarm>();
		this.peerList = new HashMap<Integer, Peer>();
	}

	public Tracker(int id, String ip, int port, boolean isMaster) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.isMaster = isMaster;
		swarmList = new ArrayList<Swarm>();
		this.trackerList = new ArrayList<Tracker>();
		this.peerList = new HashMap<Integer, Peer>();
	}

	public Tracker(int id, String ip, int port, ArrayList<Tracker> trackerList) {
		super();
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.trackerList = trackerList;
		swarmList = new ArrayList<Swarm>();
		this.peerList = new HashMap<Integer, Peer>();
		;
	}

	public ArrayList<Swarm> getSwarmList() {
		return swarmList;
	}

	public void setSwarmList(ArrayList<Swarm> swarmList) {
		this.swarmList = swarmList;
	}

	public HashMap<Integer, Peer> getPeerList() {
		return peerList;
	}

	public void setPeerList(HashMap<Integer, Peer> peerList) {
		this.peerList = peerList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public boolean isMaster() {
		return isMaster;
	}

	public Calendar getKeepAlive() {
		return KeepAlive;
	}

	public void setKeepAlive(Calendar keepAlive) {
		this.KeepAlive = keepAlive;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public boolean getStorage() {
		return this.storage;
	}

	public void setStorage(boolean storage) {
		this.storage = storage;
	}

	public ArrayList<Tracker> getTrackerList() {
		return trackerList;
	}

	public void setTrackerList(ArrayList<Tracker> trackerList) {
		this.trackerList = trackerList;
	}

	public void actualizarDate() {
		Calendar fecha = Calendar.getInstance();
		this.KeepAlive = fecha;
	}

	public void addTracker(Tracker tracker) {
		tracker.setKeepAlive(Calendar.getInstance());
		this.trackerList.add(tracker);
	}

	public void notifyTrackerChanged(Tracker t) {
		setChanged();
		notifyObservers(t);
	}

	public void checkTracker() {

		long current = Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < this.trackerList.size(); i++) {
			if (this.trackerList.get(i).getId() != this.getId()) {
				if (current - this.getTrackerList().get(i).getKeepAlive().getTimeInMillis() > 10000) {

					if (this.trackerList.get(i).isMaster()) {
						this.trackerList.get(i).setMaster(false);
						this.trackerList.remove(i);

						this.changeMaster();
					} else {
						this.trackerList.remove(i);

					}
				}
			}
		}
	}

	public void checkSaveDB() {

		long current = Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < this.trackerList.size(); i++) {
			this.trackerList.get(i).setStorage(false);
			if (this.trackerList.get(i).getId() != this.getId()) {
				if (current - this.getTrackerList().get(i).getKeepAlive().getTimeInMillis() > 10000) {
					this.trackerList.get(i).setStorage(false);

				} else {
					this.getTrackerList().get(i).setStorage(true);
					System.out.println("cambiado el storage del tracker 1" + this.getTrackerList().get(i).id);
				}
			}

		}
	}

	public void checkStorage() {

		for (int i = 0; i < this.trackerList.size(); i++) {
			if (!this.trackerList.get(i).getStorage()) {
				if (this.trackerList.get(i).isMaster()) {
					this.trackerList.remove(i);
					this.changeMaster();
				} else {
					this.trackerList.remove(i);
				}
			} else {
				this.trackerList.get(i).setStorage(false);
			}
		}

	}

	public void saveDB() {
		this.generateDatabase.deletedDB();
		this.generateDatabase.iniciateDatabase();
		if (!this.peerList.isEmpty()) {
			@SuppressWarnings("rawtypes")
			Iterator it = this.peerList.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry peer = (Map.Entry) it.next();
				this.generateDatabase.newPeer((Peer) peer.getValue());

			}

		}
		if (!this.swarmList.isEmpty()) {
			for (int i = 0; i < this.swarmList.size(); i++) {
				this.generateDatabase.newSwarm(this.swarmList.get(i));

			}
		}

	}

	public boolean haveSwarm(String infohast) {
		boolean enc = false;
		for (int i = 0; i < this.swarmList.size(); i++) {
			if (this.swarmList.get(i).getInfoHash().equals(infohast)) {
				enc = true;
				break;
			}

		}

		return enc;
	}

	public void changeMaster() {
		int menor = 0;
		int idmenor = this.getId();
		if (this.id != 0 && this.trackerList.size() > 0) {

			for (int i = 0; i < this.trackerList.size(); i++) {
				if (idmenor > this.trackerList.get(i).getId()) {
					idmenor = this.trackerList.get(i).getId();
					menor = i;

				} else if (idmenor == this.trackerList.get(i).getId()) {
					menor = i;
				} else {

				}
			}
			this.trackerList.get(menor).setMaster(true);
			if (this.id == idmenor) {
				this.setMaster(true);
			}
		}
	}

	public boolean areTrackers() {
		if (this.trackerList.size() == 0) {
			return true;
		} else {
			return false;
		}

	}

	public GenerateDatabase getGenerateDatabase() {
		return generateDatabase;
	}

	public void setGenerateDatabase(GenerateDatabase generateDatabase) {
		this.generateDatabase = generateDatabase;
	}

	public Peer getPeerByTrans(int trans) {
		Peer peer = new Peer();
		if (!peerList.isEmpty()) {
			for (int i = 0; i < peerList.size(); i++) {
				if (peerList.get(i).getTransaction_id() == trans) {
					peer = peerList.get(i);
					break;
				}
			}
		}
		return peer;
	}
}