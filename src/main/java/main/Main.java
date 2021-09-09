package main;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.StackOverFlowServlet;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ServletContextHandler context = new ServletContextHandler(1);
        Executor executor = Executors.newFixedThreadPool(1);

        context.addServlet(new ServletHolder(new StackOverFlowServlet(executor)), "/*");
        Server server = new Server(8080);

        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

