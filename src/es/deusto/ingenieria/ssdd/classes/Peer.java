package es.deusto.ingenieria.ssdd.classes;

import java.util.Calendar;

public class Peer {

	private int port;
	private String ip;
	private int transaction_id;
	private Long connection_id;
	private long left;
	private long leftMb;
	private long dowload;
	private long dowloadMb;
	private long upload;
	private Calendar time = Calendar.getInstance();
	private int id;

	public Peer() {}

	public Peer(int id, String ip, int Port) {
		this.id = id;
		this.ip = ip;
		this.port = Port;
	}

	public Peer(int id, String ip, int Port, int trasaction, int dowload, int left) {
		this.id = id;
		this.ip = ip;
		this.port = Port;
		this.transaction_id = trasaction;
		this.dowload = dowload;
		this.left = left;
	}

	public Peer(String ip, int Port, int trasaction, int dowload, int left) {
		this.ip = ip;
		this.port = Port;
		this.transaction_id = trasaction;
		this.dowload = dowload;
		this.left = left;
	}

	public Peer(String ip, int Port, int trasaction, long dowload, long left) {
		this.ip = ip;
		this.port = Port;
		this.transaction_id = trasaction;
		this.dowload = dowload;
		this.left = left;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Peer(int port, String ip) {
		this.port = port;
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getTransaction_id() {
		return transaction_id;
	}

	public long getLeft() {
		return left;
	}

	public void setLeft(long left) {
		this.left = left;
	}

	public long getDowload() {
		return dowload;
	}

	public long getDownloadMb() {
		this.dowloadMb = (dowload / 8);
		this.dowloadMb = this.dowloadMb / 1024;
		return dowloadMb;
	}

	public long getLeftMb() {

		this.leftMb = (left / 8);
		this.leftMb = this.leftMb / 1024;
		return leftMb;
	}

	public void setDowload(long downloaded) {
		this.dowload = downloaded;
	}

	public long getUpload() {
		return upload;
	}

	public void setUpload(long upload) {
		this.upload = upload;
	}

	public void setTransaction_id(int transaction_id) {
		this.transaction_id = transaction_id;
	}

	public Long getConnection_id() {
		return connection_id;
	}

	public void setConnection_id(Long connection_id) {
		this.connection_id = connection_id;
	}

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

}
