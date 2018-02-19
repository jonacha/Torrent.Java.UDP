package es.deusto.ingenieria.ssdd.utils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import es.deusto.ingenieria.ssdd.classes.Tracker;

import es.deusto.ingenieria.ssdd.listener.KeepAliveListener;
import es.deusto.ingenieria.ssdd.listener.SaveDatabaseListener;
import es.deusto.ingenieria.ssdd.listener.StoreIdListener;
import es.deusto.ingenieria.ssdd.listener.UpdateDatabaseListener;
import es.deusto.ingenieria.ssdd.runable.ComprobarActivadosRunnable;
import es.deusto.ingenieria.ssdd.runable.KeepAliveRunnable;
import es.deusto.ingenieria.ssdd.runable.MensajeSender;
import es.deusto.ingenieria.ssdd.runable.MulticastSocketTracker;

public class CreateConexion {
	private Topic topic;
	private Topic topicSDB;
	private Tracker tracker;
	private MessageProducer messageProducer;
	private MessageProducer messageProducerDB;
	private MensajeSender meS;
	private MensajeSender meSDB;
	private MessageConsumer consumer;
	private MessageConsumer consumer2;
	private MessageConsumer consumer3;
	private MessageConsumer consumer4;
	private ConnectionFactory connectionFactory = null;
	private MulticastSocketTracker mt;

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public MensajeSender getMeS() {
		return meS;
	}

	public void setMeS(MensajeSender meS) {
		this.meS = meS;
	}

	private Connection connection = null;
	private Session session = null;
	private String path = "tcp://localhost:61616";
	private KeepAliveRunnable ka;
	private ComprobarActivadosRunnable ca;

	public CreateConexion() {
		this.connectionFactory = new ActiveMQConnectionFactory(path);
		try {
			this.connection = connectionFactory.createConnection();
			this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			e.printStackTrace();
			System.err.println("Fail creating conexion");
		}
	}

	public CreateConexion(String tipo, String ip, int port) {

		this.connectionFactory = new ActiveMQConnectionFactory(path);
		try {

			this.connection = connectionFactory.createConnection();

			this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			this.topic = this.session.createTopic(tipo);

			this.topicSDB = this.session.createTopic("DB");

			this.tracker = new Tracker(ip, port);

			this.messageProducer = this.session.createProducer(topic);
			this.meS = new MensajeSender(tracker, messageProducer, this.session, topic.getTopicName());

			messageProducerDB = this.session.createProducer(topicSDB);
			meSDB = new MensajeSender(tracker, messageProducerDB, this.session, topicSDB.getTopicName());

			this.consumer = this.session.createConsumer(topic);
			this.consumer2 = this.session.createConsumer(topic);
			this.consumer3 = this.session.createConsumer(topicSDB);
			this.consumer4 = this.session.createConsumer(topic);
			this.consumer.setMessageListener(new KeepAliveListener(tracker, meS));
			this.consumer2.setMessageListener(new StoreIdListener(tracker));
			this.consumer3.setMessageListener(new UpdateDatabaseListener(meSDB));
			this.consumer4.setMessageListener(new SaveDatabaseListener(tracker));
			this.connection.start();
			this.ka = new KeepAliveRunnable(meS);
			this.ca = new ComprobarActivadosRunnable(tracker);
			this.mt = new MulticastSocketTracker(tracker);
		} catch (JMSException e) {
			e.printStackTrace();
			System.err.println("Fail creating conexion");
		}

	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Session getSession() {
		return session;
	}

	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public MulticastSocketTracker getMt() {
		return mt;
	}

	public void setMt(MulticastSocketTracker mt) {
		this.mt = mt;
	}

	public KeepAliveRunnable getKa() {
		return ka;
	}

	public void setKa(KeepAliveRunnable ka) {
		this.ka = ka;
	}

	public ComprobarActivadosRunnable getCa() {
		return ca;
	}

	public void setCa(ComprobarActivadosRunnable ca) {
		this.ca = ca;
	}

	public Tracker getTracker() {
		return tracker;
	}

	public void setTracker(Tracker tracker) {
		this.tracker = tracker;
	}

	public MessageConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(MessageConsumer consumer) {
		this.consumer = consumer;
	}

	public MessageConsumer getConsumer2() {
		return consumer2;
	}

	public void setConsumer2(MessageConsumer consumer2) {
		this.consumer2 = consumer2;
	}

	public MessageConsumer getConsumer3() {
		return consumer3;
	}

	public void setConsumer3(MessageConsumer consumer3) {
		this.consumer3 = consumer3;
	}

	public MessageConsumer getConsumer4() {
		return consumer4;
	}

	public void setConsumer4(MessageConsumer consumer4) {
		this.consumer4 = consumer4;
	}

}
