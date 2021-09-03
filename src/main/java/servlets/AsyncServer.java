package servlets;

import com.google.gson.Gson;
import json.Root;

import javax.servlet.AsyncContext;
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

import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;


public class AsyncServer extends HttpServlet {
    Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AsyncContext ctxt = req.startAsync();
        String [] tags = req.getParameterValues("tag");
        // Какой -то тег
        String tag = null;
            ctxt.start(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   StackOverFlowServlet servlet = new StackOverFlowServlet();
                                   Root root = getStackOverFlow(tag);
                                   servlet.setParameter(tag, gson, root);
                                   servlet.doGet(req, resp);
                                   ctxt.complete();
                               } catch (ServletException | IOException e) {
                                   e.printStackTrace();
                               }
                           }
                       }
            );

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