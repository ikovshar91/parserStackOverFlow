package main;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import json.Result;
import json.Root;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class StackOverFlowApi {
    private static final Gson gson = new Gson();

    public static Result<Root> getStackOverFlow(String tag) {
        URL url = null;
        try {
            url = new URL("https://api.stackexchange.com/2.2/search?pagesize=100&order=desc&sort=creation&tagged=" + tag + "&site=stackoverflow");
        } catch (MalformedURLException e) {
           return new Result<>(e);
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            return new Result<>(e);
        }

        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            return new Result<>(e);
        }

        connection.setRequestProperty("Content-Type", "application/json");

        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(connection.getInputStream());
        } catch (IOException e) {
            return new Result<>(e);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));

        String inputLine;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append((inputLine));
            }
            bufferedReader.close();
        } catch (IOException e) {
            return new Result<>(e);
        }
        try {
            return new Result<>(gson.fromJson(stringBuilder.toString(), Root.class));
        } catch (JsonSyntaxException e) {
           return new Result<>(e);
        }
    }
}
