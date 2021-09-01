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
import java.util.zip.GZIPInputStream;

public class StackOverFlowServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();
        String tag = req.getParameter("tag");
        try {
            if (tag == null) {
                resp.setStatus(404);
            } else {
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

                Root root = gson.fromJson(stringBuilder.toString(), Root.class);
                for (Item root1 : root.items) {
                    resp.getWriter().print(gson.toJson(root1));
                }

//            }
            }
        } catch (Exception e){
            e.printStackTrace();
            Error error = new Error(String.format("Kakaya-to huynya proizoshla: %s", e.getMessage()));
            resp.getWriter().print(gson.toJson(error));
            resp.setStatus(500);
        }
    }
}
