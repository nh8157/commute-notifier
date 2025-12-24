package org.commuter_notifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherForecastClient {
    private static final String OPENMETEO_URL = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&minutely_15=%s&timezone=%s&forecast_minutely_15=%s";

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
            System.out.println("OpenMeteo request: successful.");
            return response.body();
        } else {
            throw new Exception("OpenMeteo request: failed due to %s".formatted(response.statusCode()));
        }
    }
}
