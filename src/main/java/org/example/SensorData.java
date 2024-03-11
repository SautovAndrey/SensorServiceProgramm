package org.example;

public class SensorData {
    private String sensorId;
    private Double temperatureC;
    private String error;


    public SensorData(String sensorId, Double temperatureC) {
        this.sensorId = sensorId;
        this.temperatureC = temperatureC;
        this.error = null;
    }

    public SensorData(String sensorId, Double temperatureC, String error) {
        this.sensorId = sensorId;
        this.temperatureC = temperatureC;
        this.error = error;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Double getTemperatureC() {
        return temperatureC;
    }

    public String getError() {
        return error;
    }
}
