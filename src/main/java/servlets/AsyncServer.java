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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;


public class AsyncServer extends HttpServlet {
    Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AsyncContext ctxt = req.startAsync();
        List<String> tags = Arrays.asList(req.getParameterValues("tag"));


        //При написании такой конструкции на вызове метода getStackOverFlow требует его обернуть в трай блок и далее подобная ебанина
        List<CompletableFuture<Root>> futures = tags.stream().map(tag -> CompletableFuture.supplyAsync(() -> {
            try {
                return getStackOverFlow(tag);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return null;
        }));


        //А дэдлок в моей голове это вопрос того где правильно вытаскивать этот Root, в реализации метода run, т.к. он там работает непосредственно с каждым рутом в отдельности
        // но в то же время нужно вне реализации метода нужно вытащить эти футуры и подставить к каждой этот метод ран для параллелизма
        //ну либо я даунич и неправильно понимаю для чего вообще нужен этот CompletableFuture



        //List<Root> roots = CompletableFuture.allOf(futures);

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