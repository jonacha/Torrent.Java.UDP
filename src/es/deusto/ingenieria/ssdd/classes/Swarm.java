package es.deusto.ingenieria.ssdd.classes;

import java.util.ArrayList;
import java.util.List;

import bitTorrent.tracker.protocol.udp.PeerInfo;

public class Swarm {
	private int swarm;
	private String content;
	private List<PeerInfo> peerList;

	private String infoHash;
	private String swarmFile;

	private int totalSeeders;
	private int totalLeecher;
	private long downloaded;
	private long uploaded;
	private long left;

	public Swarm(String infoHash, int size) {
		this.infoHash = infoHash;
		this.peerList = new ArrayList<PeerInfo>();
		this.size = size;
	}

	public Swarm(int swarm, String content, int size) {
		this.swarm = swarm;
		this.content = content;
		this.peerList = new ArrayList<PeerInfo>();
		this.size = size;
	}

	public Swarm(String infoHash, String file, int size) {
		this.infoHash = infoHash;
		this.swarmFile = file;
		this.size = size;
		this.totalLeecher = 0;
		this.totalSeeders = 0;
		this.downloaded = 0;
		this.uploaded = 0;
		this.left = 0;
		this.peerList = new ArrayList<>();
	}

	public Swarm(String infohash2) {
		this.infoHash = infohash2;
		this.swarmFile = "";
		this.totalLeecher = 0;
		this.totalSeeders = 0;
		this.size = 0;
		this.downloaded = 0;
		this.uploaded = 0;
		this.left = 0;
		this.peerList = new ArrayList<>();
	}

	public List<PeerInfo> getPeerList() {
		return peerList;
	}

	public void setPeerList(List<PeerInfo> list) {
		this.peerList = list;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	private int size;

	public int getSwarm() {
		return swarm;
	}

	public void setSwarm(int swarm) {
		this.swarm = swarm;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(String infoHash) {
		this.infoHash = infoHash;
	}

	public String getSwarmFile() {
		return swarmFile;
	}

	public void setSwarmFile(String swarmFile) {
		this.swarmFile = swarmFile;
	}

	public int getTotalSeeders() {
		return totalSeeders;
	}

	public void setTotalSeeders(int totalSeeders) {
		this.totalSeeders = totalSeeders;
	}

	public int getTotalLeecher() {
		return totalLeecher;
	}

	public void setTotalLeecher(int totalLeecher) {
		this.totalLeecher = totalLeecher;
	}

	public long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}

	public long getUploaded() {
		return uploaded;
	}

	public void setUploaded(long uploaded) {
		this.uploaded = uploaded;
	}

	public long getLeft() {
		return left;
	}

	public void setLeft(long left) {
		this.left = left;
	}

}
