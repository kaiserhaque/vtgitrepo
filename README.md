import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class JMSQueueSender {
    public static void main(String[] args) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
            env.put(Context.PROVIDER_URL, "t3://localhost:7001");

            Context ctx = new InitialContext(env);
            QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup("jms/ConnectionFactory");
            QueueConnection qc = qcf.createQueueConnection();
            QueueSession qs = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) ctx.lookup("jms/Queue");

            QueueSender sender = qs.createSender(queue);
            TextMessage message = qs.createTextMessage("Hello, WebLogic JMS!");

            qc.start();
            sender.send(message);
            System.out.println("Message sent successfully!");

            sender.close();
            qs.close();
            qc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
