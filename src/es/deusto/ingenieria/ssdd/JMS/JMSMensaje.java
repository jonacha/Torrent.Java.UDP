package es.deusto.ingenieria.ssdd.JMS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import es.deusto.ingenieria.ssdd.classes.*;

public class JMSMensaje {
	public JMSMensaje() {

	}

	/*
	 * Para cada uno de los datos se deve poner una etiqueta para poder sacar el
	 * string por medio del documento xml
	 */

	/*
	 * Mediante este metodo podemos hacer los mensajes de : Update base de datos
	 * Fallar el update de la base de datos Aceptar update base de datos Seleccionar
	 * master send id mensaje killing alive mensajes
	 */
	public String messagesString(Tracker tracker, String tMensaje) {
		Document doc = new Document();
		Element xml = new Element("message");
		Element type = new Element("type");
		Element newId = new Element("newid");
		Element db = new Element("DB");
		Element trackerId = new Element("id");
		Element isMaster = new Element("IsMaster");
		Element trackerIp = new Element("ip");
		Element trackerPort = new Element("port");
		type.addContent(tMensaje);
		trackerIp.addContent(tracker.getIp() + "");
		trackerId.addContent(tracker.getId() + "");
		trackerPort.addContent(tracker.getPort() + "");
		isMaster.addContent(tracker.isMaster() + "");
		if (tMensaje.equals("sendId")) {
			if (tracker.getTrackerList().size() > 0 && tracker.isMaster()) {
				int newid = tracker.getTrackerList().get(tracker.getTrackerList().size() - 1).getId() + 1;
				Tracker t = new Tracker(newid);
				tracker.getTrackerList().add(t);
				newId.addContent(Integer.toString(newid) + "");

			}
		} else if (tMensaje.equals("SaveDB")) {
			db.addContent("Save");
			xml.addContent(db);
		} else {
			newId.addContent("0");
		}
		xml.addContent(type);
		xml.addContent(trackerId);
		xml.addContent(trackerIp);
		xml.addContent(newId);
		xml.addContent(isMaster);
		xml.addContent(trackerPort);

		doc.addContent(xml);
		System.out.println("La id del tracker: " + tracker.getId() + " Tracker con IP:" + tracker.getIp()
				+ " Envia mensaje tipo: " + tMensaje);
		return new XMLOutputter().outputString(doc);
	}

	public org.w3c.dom.Document messagesXML(String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		InputStream stream = null;
		org.w3c.dom.Document anotherDocument = null;
		try {
			stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			try {
				anotherDocument = builder.parse(stream);
			} catch (SAXException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		anotherDocument.getDocumentElement().normalize();
		return anotherDocument;
	}

	public String askMaster(boolean isMaster) {
		if (isMaster) {
			return "Master";
		} else {
			return "No Master";
		}

	}

}
