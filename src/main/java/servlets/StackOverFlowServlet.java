package servlets;

import com.google.gson.Gson;
import json.Error;
import json.Result;
import json.Stats;
import main.Repository;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;



public class StackOverFlowServlet extends HttpServlet {
private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        AsyncContext context = req.startAsync();
        context.start(() -> {
            Result<List<Stats>> statsResult = Repository.getStats(req.getParameterValues("tag"));
            if (statsResult.result != null) {
                List<Stats> stats = statsResult.result;
                try {
                    resp.getWriter().print(gson.toJson(stats));
                    resp.setStatus(200);
                } catch (IOException e) {
                    e.printStackTrace();
                    resp.setStatus(500);
                }
            } else {
                resp.setStatus(500);
                Error error = new Error(statsResult.exception);
                try {
                    resp.getWriter().print(gson.toJson(error));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            context.complete();
        });
    }
}





