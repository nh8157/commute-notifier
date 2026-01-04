package org.commuter_notifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TubeStatusClient {
    private static final String URL_TEMPLATE = "https://api.tfl.gov.uk/Line/%s/Status";

    record LineStatus(
        Integer statusSeverity,
        String statusSeverityDescription
    ) {};

    record TubeStatus(
        String name,
        List<LineStatus> lineStatuses
    ) {};

    private static String fromTflApi(String lineName) throws Exception {
        String requestUrl = URL_TEMPLATE.formatted(lineName);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("✅ Tube status request: successful.");
            return response.body();
        } else {
            throw new Exception("❌ OpenMeteo request: due to %s".formatted(response.statusCode()));
        }
    }

    private static TubeStatus parseTflResp(String resp) {
        Gson gson = new Gson();
        List<TubeStatus> parsed = gson.fromJson(resp, new TypeToken<List<TubeStatus>>() {}.getType());
        return parsed.get(0);
    }

    public static TubeStatus getTubeStatus(String lineName) throws Exception {
        String resp = fromTflApi(lineName);
        return parseTflResp(resp);
    }
}
