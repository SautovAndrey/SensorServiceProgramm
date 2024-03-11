package org.example;



import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SensorService {
    private final HttpClient httpClient;
    private final Gson gson;

    public SensorService(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    public List<CompletableFuture<SensorData>> fetchSensorData(List<String> sensorUrls, long timeout, TimeUnit unit, CompletableFuture<Void> cancelToken) {
        List<CompletableFuture<SensorData>> futures = new ArrayList<>();

        for (String url : sensorUrls) {
            CompletableFuture<SensorData> future = CompletableFuture.supplyAsync(() -> {
                        if (cancelToken.isCompletedExceptionally()) {
                            throw new CancellationException("Operation was cancelled");
                        }

                        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                        try {
                            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                            return parseSensorData(response.body());
                        } catch (Exception e) {
                            return new SensorData(null, null, "Error fetching or parsing data from " + url + ": " + e.getMessage());
                        }
                    }).orTimeout(timeout, unit)
                    .exceptionally(ex -> {
                        if (ex instanceof CancellationException) {
                            return new SensorData(null, null, "Operation cancelled");
                        } else {
                            return new SensorData(null, null, "Timeout or error for URL: " + url + ": " + ex.getMessage());
                        }
                    });

            cancelToken.exceptionally(ex -> {
                future.cancel(true);
                return null;
            });

            futures.add(future);
        }

        return futures;
    }

    private SensorData parseSensorData(String responseBody) {
        try {
            return gson.fromJson(responseBody, SensorData.class);
        } catch (Exception e) {
            return new SensorData(null, null, "Error parsing JSON: " + e.getMessage());
        }
    }
}
