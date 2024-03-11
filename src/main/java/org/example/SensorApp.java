package org.example;

import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SensorApp {
    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newHttpClient();
        Gson gson = new Gson();


        SensorService service = new SensorService(httpClient, gson);
        CompletableFuture<Void> cancelToken = new CompletableFuture<>();

        List<CompletableFuture<SensorData>> futures = service.fetchSensorData(
                List.of("http://localhost:8080/sensor"),
                5,
                TimeUnit.SECONDS,
                cancelToken
        );

        futures.forEach(future -> future.thenAccept(data -> {
            if (data.getError() != null) {
                System.out.println("Error: " + data.getError());
            } else {
                System.out.println("Sensor ID: " + data.getSensorId() + ", Temperature: " + data.getTemperatureC());
            }
        }));
    }
}
