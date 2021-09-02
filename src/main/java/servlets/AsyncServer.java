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
        if (req.getParameter("tag1") != null) {
            startAsync(req, resp, "tag1");
        if(req.getParameter("tag2")!= null) {
            startAsync(req, resp, "tag2");
            if (req.getParameter("tag3") != null) {
                startAsync(req, resp, "tag3");
            }
        }
        }
    }

    public void startAsync(HttpServletRequest req, HttpServletResponse resp, String tag){
        final AsyncContext ctxt = req.startAsync();
        ctxt.start(new Runnable() {
            @Override
            public void run() {
                try {
                    StackOverFlowServlet servlet = new StackOverFlowServlet();
                   servlet.setTag(req.getParameter(tag));
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
