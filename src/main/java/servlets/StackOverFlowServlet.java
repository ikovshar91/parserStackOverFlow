package servlets;

import com.google.gson.Gson;
import json.Item;
import json.Root;
import json.Error;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class StackOverFlowServlet extends HttpServlet {
    private String tag;
    Gson gson = new Gson();

    public void setTag(String tag){
        this.tag = tag;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            if (tag == null) {
                resp.setStatus(404);
            } else {

                Root root = getStackOverFlow(tag);
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

    public Root getStackOverFlow(String tag) throws IOException {
        URL url = new URL("https://api.stackexchange.com/2.2/search?pagesize=100&order=desc&sort=creation&tagged=" + tag + "&site=stackoverflow");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        GZIPInputStream gis = new GZIPInputStream(connection.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));

        String inputLine;

        StringBuilder stringBuilder = new StringBuilder();

        while ((inputLine = bufferedReader.readLine()) != null) {
            stringBuilder.append((inputLine));
        }

        bufferedReader.close();

        return gson.fromJson(stringBuilder.toString(), Root.class);
    }




}





