package org.commuter_notifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WeatherForecastClient {
    private static final String OPENMETEO_URL = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&minutely_15=%s&timezone=%s&forecast_minutely_15=%s";

    public record OpenMeteoForecast (
        List<LocalDateTime> time,
        List<Double> temperature_2m,
        List<Double> apparent_temperature,
        List<Integer> precipitation_probability,
        List<Double> precipitation
    ) {};
    public record OpenMeteoResp (
        OpenMeteoForecast minutely_15
    ) {};

    public static String fromOpenMeteo(String lat, String lon, String forecastMode, String tz, Integer forecastDurationHours) throws Exception {
        // provide lat, lon, etc.
        // send get request to API endpoint
        // return weather forecast
        Integer forecastDurationMinutes = forecastDurationHours * 4;
        String requestUrl = OPENMETEO_URL.formatted(lat, lon, forecastMode, tz, forecastDurationMinutes.toString());

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("✅ OpenMeteo request: successful.");
            return response.body();
        } else {
            throw new Exception("❌ OpenMeteo request: due to %s".formatted(response.statusCode()));
        }
    }

    public static OpenMeteoForecast parseOpenMeteoResp(String respStr) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        OpenMeteoResp respObj = gson.fromJson(respStr, OpenMeteoResp.class);
        return respObj.minutely_15;
    }

    public static OpenMeteoForecast getOpenMeteoForecast(String lat, String lon, String forecastMode, String tz, Integer forecastDurationHours) throws Exception {
        String resp = fromOpenMeteo(lat, lon, forecastMode, tz, forecastDurationHours);
        return parseOpenMeteoResp(resp);
    }
}
