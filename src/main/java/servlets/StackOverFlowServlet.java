package servlets;

import com.google.gson.Gson;
import json.Item;
import json.Root;
import json.Error;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StackOverFlowServlet extends HttpServlet {
    private String tag;
    private Gson gson;
    private Root root;

    public void setParameter(String tag, Gson gson, Root root){
        this.tag = tag;
        this.gson = gson;
        this.root = root;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            if (tag == null) {
                resp.setStatus(404);
            } else {
                List<Boolean> isAnswered = new ArrayList<>();
                for (Item root1 : root.items) {
                    if (root1.is_answered) {
                        isAnswered.add(true);
                    }
                }
                resp.getWriter().printf("\"%s\":{ \"total:\": %s, \"answered\": %s}\n", tag, gson.toJson(root.items.size()), isAnswered.size());
            }
        } catch (Exception e){
            e.printStackTrace();
            Error error = new Error(String.format("Kakaya-to huynya proizoshla: %s", e.getMessage()));
            resp.getWriter().print(gson.toJson(error));
            resp.setStatus(500);
        }
    }


}





