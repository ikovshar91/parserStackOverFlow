package servlets;
import com.google.gson.Gson;
import json.Error;
import json.OnlyStats;
import json.Result;
import json.Stats;
import main.Repository;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


public class StackOverFlowServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    Executor executor;
    public StackOverFlowServlet(Executor executor){
        this.executor = executor;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        resp.setContentType("application/json");
        AsyncContext context = req.startAsync();
        context.start(() -> {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Result<List<Stats>> statsResult = Repository.getStats(req.getParameterValues("tag"));
                    if (statsResult.result != null) {
                        List<Stats> stats = statsResult.result;
                        Map<String, OnlyStats> map = stats.stream().
                                collect(Collectors.toMap(x -> x.tag, x -> new OnlyStats(x.total,x.answered)));
                        try {
                            resp.getWriter().print(gson.toJson(map));
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
                }
            });
        });
    }
}





