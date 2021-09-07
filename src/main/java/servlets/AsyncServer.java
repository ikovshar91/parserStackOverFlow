package servlets;

import com.google.gson.Gson;
import json.Root;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
/*
    Делаю по аналогии с твоим кодом:
    Вначале пытаюсь добавить в лист комплитблфутуры с дженериками по рутам.(Как я понял в моем случае необходимо туда вставить метод ГЕТСТЭКОВЕРФЛОУ чтобы вытащить оттуда JSON
    и вставить в каждый root свой запрос. Затем по каждому тэгу необходимо прогнать метод sequence и там уже вставлять метод servlet.setParameter() и servlet.doGet() и да будет мне счастье
    однако чето нихуя не выходит
 */

public class AsyncServer extends HttpServlet {
    static Gson gson = new Gson();
    @Override

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] tags = req.getParameterValues("tag");
        Executor executor = Executors.newFixedThreadPool(3);
        List<CompletableFuture<Root>> futureList = new ArrayList<>();
        for (String tag : tags){
            futureList.add(doShit(tag, executor));
        }


        //СТАРЫЙ КОД
//        for (String tag : tags) {
//            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
//                        try {
//                            StackOverFlowServlet servlet = new StackOverFlowServlet();
//                            Root root = getStackOverFlow(tag);
//                            servlet.setParameter(tag, gson, root);
//                            servlet.doGet(req, resp);
//                        } catch (ServletException | IOException e) {
//                            e.printStackTrace();
//                        }
//                    },executor);
//            try {
//                completableFuture.get();
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static Root getStackOverFlow(String tag) throws IOException {
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


    private static CompletableFuture<Root> doShit(String tag, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
               Root root = getStackOverFlow(tag);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            // Должен возвращать рут но метод getstackoverflow надо обработать на исключения и так чет нихуя не выходит
            return root;
        }, executor);
    }


}