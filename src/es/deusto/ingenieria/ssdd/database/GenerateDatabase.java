package es.deusto.ingenieria.ssdd.database;

import java.sql.*;
import java.util.ArrayList;
import es.deusto.ingenieria.ssdd.classes.*;

public class GenerateDatabase {
	public Connection conexion;

	/*
	 * https://bitbucket.org/xerial/sqlite-jdbc Inicia la base de datos con todas
	 * las tablas en caso de que no existan
	 */
	public GenerateDatabase(String DB) {
		try {
			Class.forName("org.sqlite.JDBC");
			conexion = DriverManager.getConnection("jdbc:sqlite:" + DB);
			System.out.println("Opened database successfully");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	public void iniciateDatabase() {
		Statement stmt = null;
		int result;

		try {
			stmt = conexion.createStatement();

			try {
				String q1 = "CREATE TABLE  PEER" + "(ID INTEGER PRIMARY KEY   AUTOINCREMENT ,"
						+ " TRANSACTION_ID INT NOT NULL, IP          TEXT    NOT NULL, "
						+ " PORT            INT     NOT NULL, DONWLOAD INT NOT NULL, LEFT INT  NOT NULL); ";
				result = stmt.executeUpdate(q1);
				if (result == 0) {
					System.out.println("The table PEER have been created");
				} else {
					System.err.println("The table PEER haven't been created");
				}

			} catch (Exception a) {
				System.err.println(a.getClass().getName() + ": " + a.getMessage() + "  Fail creating database PEER");
				System.exit(0);
			}

			try {
				String q2 = "CREATE TABLE  SWARM" + "(IDSWARM INTEGER PRIMARY KEY  AUTOINCREMENT  ,"
						+ " INFOHASH  TEXT    NOT NULL,"
						+ " TOTALLEEACHER  TEXT    NOT NULL, TOTALSEEDERS  TEXT    NOT NULL) ;";
				result = stmt.executeUpdate(q2);
				if (result == 0) {
					System.out.println("The table SWARM have been created");
				} else {
					System.err.println("The table SWARM haven't been created");
				}
			} catch (Exception a) {
				System.err.println(a.getClass().getName() + ": " + a.getMessage() + "  Fail creating database PEER");
				System.exit(0);
			}
		} catch (Exception a) {
			System.err.println(a.getClass().getName() + ": " + a.getMessage() + "  Fail creating database PEER");
			System.exit(0);
		}

	}

	public void updatePeer(Peer p) {
		Statement stmt = null;
		int result = 0;

		String q1 = "UPDATE PEER  SET DONWLOAD=" + p.getDowload() + ",LEFT=" + p.getLeft() + "  WHERE  TRANSACTION_ID="
				+ p.getTransaction_id() + ";";
		try {
			stmt = conexion.createStatement();
			try {
				result = stmt.executeUpdate(q1);
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage() + "  Fail Updating peer new PEER");
			}
			if (result >= 0) {
				System.out.println("Update Peer");
			} else {
				System.err.println("Fail insert Peer " + result);
			}

		} catch (SQLException e1) {
			System.err.println(e1.getClass().getName() + ": " + e1.getMessage() + "  Fail conecting database");
		}
	}

	public ArrayList<Peer> selectPeers() {
		Statement stmt = null;
		ArrayList<Peer> peerList = null;

		String q1 = "Select * from PEER";
		try {
			stmt = conexion.createStatement();
			ResultSet rs = stmt.executeQuery(q1);
			Peer P;
			peerList = new ArrayList<Peer>();
			while (rs.next()) {
				P = new Peer(rs.getInt("ID"), rs.getString("IP"), rs.getInt("Port"), rs.getInt("TRANSACTION_ID"),
						rs.getInt("DONWLOAD"), rs.getInt("LEFT"));

				System.out.println(
						"Getting the Peer whith port:" + P.getPort() + "  trasaction:" + P.getTransaction_id());
				peerList.add(P);
			}
			return peerList;

		} catch (SQLException e1) {
			System.err.println("Error return database");
		}
		return peerList;

	}

	public void newPeer(Peer p) {
		Statement stmt = null;
		int result = 0;
		String q1 = "INSERT INTO PEER (IP,TRANSACTION_ID,PORT,DONWLOAD,LEFT) VALUES ('" + p.getIp() + "',"
				+ p.getTransaction_id() + "," + p.getPort() + "," + p.getDowload() + "," + p.getLeft() + ");";
		try {
			stmt = conexion.createStatement();
			try {
				result = stmt.executeUpdate(q1);
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage() + "  Fail inserting new PEER");
			}
			if (result >= 0) {
				System.out.println("Insert of Peer OK");
			} else {
				System.err.println("Fail insert Peer " + result);
			}

		} catch (SQLException e1) {
			System.err.println(e1.getClass().getName() + ": " + e1.getMessage() + "  Fail conecting database");
		}

	}

	public void deletedPeer(Integer ID) {
		Statement stmt = null;
		int result = 0;
		String q1 = "DELETE FROM PEER WHERE TRANSACTION_ID= " + ID + ";";
		try {
			stmt = conexion.createStatement();
			try {
				result = stmt.executeUpdate(q1);
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage() + "  Fail deleting new PEER");
			}
			if (result >= 0) {
				System.out.println("Deleted PEER With id=" + ID);
			} else {
				System.err.println("Fail deleteing Peer");
			}

		} catch (SQLException e1) {
			System.err.println(e1.getClass().getName() + ": " + e1.getMessage() + "  Fail conecting database");
		}

	}

	public ArrayList<Swarm> selectSwarms() {

		Statement stmt = null;
		ArrayList<Swarm> swarmList = null;

		String q1 = "Select * from SWARM";
		try {
			stmt = conexion.createStatement();
			ResultSet rs = stmt.executeQuery(q1);
			Swarm swarm;
			swarmList = new ArrayList<Swarm>();
			while (rs.next()) {
				swarm = new Swarm(rs.getString("INFOHASH"));
				swarmList.add(swarm);
				System.out.println("Getting the Swarm with " + swarm.getInfoHash());
			}
			return swarmList;

		} catch (SQLException e1) {
			System.err.println("Error return database");
		}
		return swarmList;

	}

	public void newSwarm(Swarm s) {
		Statement stmt = null;
		int result = 0;
		String q1 = "INSERT INTO SWARM (TOTALLEEACHER, INFOHASH,TOTALSEEDERS) VALUES (" + s.getTotalLeecher() + ",'"
				+ s.getInfoHash() + "'," + s.getTotalSeeders() + ");";
		try {
			stmt = conexion.createStatement();
			result = stmt.executeUpdate(q1);
			if (result >= 0) {
				System.out.println("Insert of SWARM OK");
			} else {
				System.out.println("Fail Inserting SWARM  ");
			}

		} catch (SQLException e1) {
			System.err.println(e1.getClass().getName() + ": " + e1.getMessage() + "  Fail conecting database");
		}

	}

	public void deleteSwarm(String infohast) {
		Statement stmt = null;
		int result = 0;
		String q1 = "DELETE FROM SWARM WHERE INFOHASH= '" + infohast + "';";
		try {
			stmt = conexion.createStatement();
			result = stmt.executeUpdate(q1);
			if (result >= 0) {
				System.out.println("Deleted SWARM With INFOHASH=" + infohast);
			} else {
				System.out.println("Fail deleteing SWARM");
			}
			try {
				result = stmt.executeUpdate(q1);
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage() + "  Fail deleting new SWARM");
			}

		} catch (SQLException e1) {
			System.err.println(e1.getClass().getName() + ": " + e1.getMessage() + "  Fail conecting database");
		}
	}
	
	public void deletedDB() {
		Statement stmt = null;
		int result1 = 0;
		int result2 = 0;
		String q1 = "DROP TABLE PEER";
		String q2 = "DROP TABLE SWARM";

		try {
			stmt = conexion.createStatement();
			result1 = stmt.executeUpdate(q1);
			result2 = stmt.executeUpdate(q2);
			if (result1 == 0) {
				System.out.println("TABLE PEER DELETED");
			} else {
				System.out.println("Fail deleteing SWARM_PEER ");
			}

			if (result2 == 0) {
				System.out.println("TABLE SWARM DELETED");
			} else {
				System.out.println("FAIL DELETING TABLE SWARM ");
			}
		} catch (SQLException e1) {
			System.err.println("ERROR DELETING DATABASE");
		}
	}

	public void closeDB() {
		try {
			conexion.close();
			System.out.println("[DBManager] Database connection was closed");
		} catch (Exception e) {
			System.err.println("ERROR/EXCEPTION. Error closing database connection: " + e.getMessage());
		}

	}

	public static void main(String[] args) {
		GenerateDatabase db = new GenerateDatabase("Torrent.db");
		Tracker t = new Tracker("127.0.0", 8080);
		db.deletedDB();
		db.iniciateDatabase();
		t.setGenerateDatabase(db);

		// Prueva de Peers
		Peer peer = new Peer(8080, "127.0.0");
		peer.setTransaction_id(94859849);
		peer.setDowload(12);
		peer.setLeft(1555);

		db.newPeer(peer);
		db.selectPeers();
		peer.setDowload(20);
		db.updatePeer(peer);
		db.selectPeers();
		db.deletedPeer(94859849);
		db.selectPeers();
		Swarm swarm = new Swarm("Apuntes Para distribuidos");
		db.newSwarm(swarm);
		db.selectSwarms();
		db.deleteSwarm("Apuntes Para distribuidos");
		db.selectSwarms();
		t.getPeerList().put(peer.getTransaction_id(), peer);
		t.getSwarmList().add(swarm);
		t.saveDB();
		t.getGenerateDatabase().selectPeers();
		t.getGenerateDatabase().selectSwarms();
		System.out.println("Datos para poner el tracker 1 del master y ver que se guandan cosas sino peta");
		GenerateDatabase db2 = new GenerateDatabase("1-Torrent.db");
		db2.selectPeers();
		db2.selectSwarms();

	}

}
