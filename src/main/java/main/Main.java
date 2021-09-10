package main;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import servlets.StackOverFlowServlet;


public class Main {
    public static void main(String[] args) {
        ServletContextHandler context = new ServletContextHandler(1);
        QueuedThreadPool pool = new QueuedThreadPool(10);
        context.addServlet(new ServletHolder(new StackOverFlowServlet(pool)), "/*");

        Server server = new Server(pool);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

