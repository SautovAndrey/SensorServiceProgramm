package org.example;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SensorServiceTest {

    private HttpClient mockHttpClient;
    private Gson gson;
    private SensorService sensorService;

    @BeforeEach
    public void setUp() throws Exception {
        mockHttpClient = mock(HttpClient.class);
        gson = new Gson();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("{\"sensorId\": \"123\", \"temperatureC\": 25.0}");


        when(mockHttpClient.send(any(HttpRequest.class), Mockito.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(mockResponse);

        sensorService = new SensorService(mockHttpClient, gson);
    }

    @Test
    public void testFetchSensorData() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> cancelToken = new CompletableFuture<>();
        List<CompletableFuture<SensorData>> futures = sensorService.fetchSensorData(
                List.of("http://localhost:8080/sensor"),
                5,
                TimeUnit.SECONDS,
                cancelToken
        );

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (CompletableFuture<SensorData> future : futures) {
            SensorData data = future.get();
            Assertions.assertNotNull(data.getSensorId());
            Assertions.assertEquals(25.0, data.getTemperatureC());
            Assertions.assertNull(data.getError());
        }
        System.out.println("Тест testFetchSensorData() выполнен успешно.");
    }
}
