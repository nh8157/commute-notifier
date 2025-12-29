package org.commuter_notifier;

import java.time.ZoneId;

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

    public static String fromSsm(String secretName) {
        return getClient().getParameter(
            GetParameterRequest.builder()
            .name("/commuter-notifier/%s".formatted(secretName)).withDecryption(true)
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
    private static OpenMeteoConfig INSTANCE;

    private final static String DEFAULT_FORECAST_MODE = "temperature_2m,precipitation_probability,precipitation,apparent_temperature";
    private final static Integer DEFAULT_FORECAST_DURATION_HOURS = 2;
    private final static String DEFAULT_TIME_ZONE = "Europe/London";

    public static synchronized OpenMeteoConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpenMeteoConfig(
                AwsParameterManager.fromSsm("OFFICE_LAT"),
                AwsParameterManager.fromSsm("OFFICE_LON"),
                AwsParameterManager.fromSsm("HOME_LAT"),
                AwsParameterManager.fromSsm("HOME_LON"),
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
    private static VonageConfig INSTANCE;

    private final static String DEFAULT_COMM_METHOD = "SMS";

    public static synchronized VonageConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VonageConfig(
                AwsParameterManager.fromSsm("PHONE_NUM"),
                AwsParameterManager.fromSsm("VONAGE_API_KEY"),
                AwsParameterManager.fromSsm("VONAGE_API_SECRET"),
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
