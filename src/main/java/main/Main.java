package main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.StackOverFlowServlet;

public class Main {
    public static void main(String[] args) {
        ServletContextHandler context = new ServletContextHandler(1);
        context.addServlet(new ServletHolder(new StackOverFlowServlet()), "/*");

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

