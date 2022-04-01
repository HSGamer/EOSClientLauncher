package me.hsgamer.eosclientdownloader.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.hsgamer.eosclientdownloader.data.FileData;
import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class FileDataUtils {
    private static final String DATA_URL = "https://raw.githubusercontent.com/HSGamer/EOSClientDownloader/master/info/data.json";
    private static CompletableFuture<List<FileData>> future;

    private FileDataUtils() {
        // EMPTY
    }

    public static void runFuture() {
        if (future == null) {
            future = CompletableFuture.supplyAsync(() -> {
                List<FileData> data = new ArrayList<>();
                URLConnection connection;
                try {
                    connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(DATA_URL));
                } catch (IOException e) {
                    LoggerUtils.LOGGER.log(Level.SEVERE, "Failed to connect", e);
                    return data;
                }
                try (
                        InputStream inputStream = connection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream)
                ) {
                    Gson gson = new Gson();
                    JsonArray array = JsonParser.parseReader(inputStreamReader).getAsJsonArray();
                    for (int i = 0; i < array.size(); i++) {
                        data.add(gson.fromJson(array.get(i), FileData.class));
                    }
                } catch (IOException e) {
                    LoggerUtils.LOGGER.log(Level.SEVERE, "Failed to get data", e);
                }
                return data;
            });
        }
    }

    public static CompletableFuture<List<FileData>> getAvailableData() {
        runFuture();
        return future;
    }
}
