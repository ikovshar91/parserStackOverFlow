package servlets;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns={"/asyncioservlet"}, asyncSupported=true)
public class AsyncServer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AsyncContext ctxt = req.startAsync();
        String[] tags = req.getParameterValues("tag");

        for (String tag : tags) {
            ctxt.start(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   StackOverFlowServlet servlet = new StackOverFlowServlet();
                                   servlet.setTag(tag);
                                   servlet.doGet(req, resp);
                                   ctxt.complete();
                               } catch (ServletException | IOException e) {
                                   e.printStackTrace();
                               }
                           }
                       }
            );
        }
    }
}