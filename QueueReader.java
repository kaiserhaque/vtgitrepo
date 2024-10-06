package QueueReader;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QueueReader implements MessageListener {

	public final static String SERVER = "t3://localhost:7001";
	// public final static String SERVER="t3://phx-0077.snphxprshared1.gbucdsint02phx.oraclevcn.com:7011";
	public final static String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";
	public final static String JMS_FACTORY = "inventoryWSQueueCF";
	public final static String QUEUE = "SSGetServiceProfileRequestQueue";

	private QueueConnectionFactory queueConnectionFactory;
	private QueueConnection queueConnection;
	private QueueSession queueSession;
	private QueueReceiver queueReceiver;
	private Queue queue;
	private Boolean quit = false;

	public void init(Context context, String queuename) throws NamingException, JMSException {
		System.out.println("init");
		queueConnectionFactory = (QueueConnectionFactory) context.lookup(JMS_FACTORY);
		queueConnection = queueConnectionFactory.createQueueConnection();
		queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = (Queue) context.lookup(queuename);
		queueReceiver = queueSession.createReceiver(queue);
		queueReceiver.setMessageListener(this);
		queueConnection.start();
	}

	public void close() throws JMSException {
		System.out.println("close");
		queueReceiver.close();
		queueSession.close();
		queueConnection.close();
	}

	public static InitialContext getInitialContext() throws NamingException {
		System.out.println("initialcontext");
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, SERVER);
		return new InitialContext(env);
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Main");
		InitialContext initialContext = getInitialContext();
		QueueReader queueReader = new QueueReader();
		queueReader.init(initialContext, QUEUE);
		System.out.println("Waiting for Mesaage");
		synchronized (queueReader) {
			while (!queueReader.quit) {
				try {
					queueReader.wait();
				} catch (InterruptedException ie) {}
			}
			queueReader.close();
		}
	}

	@Override
	public void onMessage(Message msg) {
		try {
			System.out.println("onmessage");
			String msgText;
			if (msg instanceof TextMessage) {
				msgText = ((TextMessage) msg).getText();
				System.out.println(msg.getJMSCorrelationID());
				System.out.println(msg.getJMSReplyTo());
			} else {
				msgText = msg.toString();
			}
			System.out.println("Message Received: " + msgText);
			if (msgText.equals("quit")) {
				quit = true;
				this.notifyAll();
			}
		} catch (JMSException jmsException) {
			System.err.println("Exception:- " + jmsException.getMessage());
		}
	}
}