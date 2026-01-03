package org.commuter_notifier;

import java.time.ZoneId;

import com.google.gson.Gson;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

class AwsParameterManager {
    public static SsmClient INSTANCE;

    public static synchronized SsmClient getClient() {
        if (INSTANCE == null) {
            INSTANCE = SsmClient.builder().region(Region.of("eu-west-2")).build();
        }
        return INSTANCE;
    }

    public static String fromSsm(String... paths) {
        String secretPath = "/commuter-notifier";
        for (String path: paths) {
            secretPath += "/" + path;
        }
        return getClient().getParameter(
            GetParameterRequest.builder()
            .name(secretPath).withDecryption(true)
            .build()).parameter().value();
    }
}

public record Config(
    OpenMeteoConfig openMeteo,
    VonageConfig vonage
) {
    private static Config INSTANCE;

    public static synchronized Config getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Config(
                OpenMeteoConfig.getInstance(),
                VonageConfig.getInstance()
            );
        }
        return INSTANCE;
    }
};

record OpenMeteoConfig(
    String officeLat,
    String officeLon,
    String homeLat,
    String homeLon,
    String forecastMode,
    Integer forecastDurationHours,
    ZoneId timeZone
) {
    record Coord(
        String lat,
        String lon
    ) {};

    private static OpenMeteoConfig INSTANCE;

    private final static String DEFAULT_FORECAST_MODE = "temperature_2m,precipitation_probability,precipitation,apparent_temperature";
    private final static Integer DEFAULT_FORECAST_DURATION_HOURS = 2;
    private final static String DEFAULT_TIME_ZONE = "Europe/London";

    public static synchronized OpenMeteoConfig getInstance() {
        if (INSTANCE == null) {
            Gson gson = new Gson();
            String home_coord_str = AwsParameterManager.fromSsm("coord", "home");
            String office_coord_str = AwsParameterManager.fromSsm("coord", "office");
            Coord office_coord = gson.fromJson(office_coord_str, Coord.class);
            Coord home_coord = gson.fromJson(home_coord_str, Coord.class);
            INSTANCE = new OpenMeteoConfig(
                office_coord.lat,
                office_coord.lon,
                home_coord.lat,
                home_coord.lon,
                DEFAULT_FORECAST_MODE,
                DEFAULT_FORECAST_DURATION_HOURS,
                ZoneId.of(DEFAULT_TIME_ZONE)
            );
        }
        return INSTANCE;
    }
};

record VonageConfig(
    String phoneNum,
    String apiKey,
    String apiSecret,
    CommMethod commMethod
) {
    record Creds(
        String key,
        String secret
    ) {};

    record Config(
        String phoneNumber
    ) {};

    private static VonageConfig INSTANCE;

    private final static String DEFAULT_COMM_METHOD = "SMS";

    public static synchronized VonageConfig getInstance() {
        if (INSTANCE == null) {
            Gson gson = new Gson();
            String creds_str = AwsParameterManager.fromSsm("vonage", "creds");
            String config_str = AwsParameterManager.fromSsm("vonage", "config");
            Creds creds = gson.fromJson(creds_str, Creds.class);
            Config config = gson.fromJson(config_str, Config.class);
            INSTANCE = new VonageConfig(
                config.phoneNumber,
                creds.key,
                creds.secret,
                DEFAULT_COMM_METHOD.equals("SMS") ? CommMethod.SMS : CommMethod.WHATSAPP
            );
        }
        return INSTANCE;
    }
};

enum CommMethod {
    SMS,
    WHATSAPP
}
