package com.oracle.java;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QueueSender {
	
    public final static String SERVER = "t3://newcoc5-soa01:8101";
    public final static String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";
    public final static String JMS_FACTORY = "jms/aia/aiaResourceCF";
	public final static String QUEUE = "jms/aia/AIA_SALESORDERJMSQUEUE";
    public final static String USERNAME = "weblogic";
    public final static String PASSWORD = "welcome11";

    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private Queue queue;

    public void init(Context context, String queuename) throws NamingException, JMSException {
        System.out.println("init");
        queueConnectionFactory = (QueueConnectionFactory) context.lookup(JMS_FACTORY);
        queueConnectionFactory.createContext();
        queueConnection = queueConnectionFactory.createQueueConnection();
        queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = (Queue) context.lookup(queuename);
        queueConnection.start();
    }

    public void sendMessage(String messageText) throws JMSException {
        System.out.println("Sending Message");
        MessageProducer producer = queueSession.createProducer(queue);
        TextMessage message = queueSession.createTextMessage(messageText);
        producer.send(message);
        System.out.println("Message Sent: " + message.getText());
        producer.close();
    }

    public void close() throws JMSException {
        System.out.println("close");
        queueSession.close();
        queueConnection.close();
    }

    public static InitialContext getInitialContext() throws NamingException {
        System.out.println("initialcontext");
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, SERVER);
        env.put(Context.SECURITY_PRINCIPAL, USERNAME);
        env.put(Context.SECURITY_CREDENTIALS, PASSWORD);

        for (int i = 0; i < 10; i++) {
            try {
                return new InitialContext(env);
            } catch (NamingException e) {
                System.err.println("Failed to obtain InitialContext. Retrying... (" + (i + 1) + "/10)");
                if (i == 9) {
                    throw e; 
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Main");
        InitialContext initialContext = getInitialContext();
        System.out.println("InitialContext created, connected");
        QueueSender queueSender = new QueueSender();
        queueSender.init(initialContext, QUEUE);
        queueSender.sendMessage("Hello, this is a test message");
        queueSender.close();
    }
}
