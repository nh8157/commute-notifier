package org.commuter_notifier;

import java.time.ZoneId;

import io.github.cdimascio.dotenv.Dotenv;

public record Config(
    OpenMeteoConfig openMeteo,
    VonageConfig vonage
) {
    private static Config INSTANCE;

    public static synchronized Config getInstance() {
        if (INSTANCE == null) {
            Dotenv dotenv = Dotenv.load();
            INSTANCE = new Config(
                OpenMeteoConfig.getInstance(dotenv),
                VonageConfig.getInstance(dotenv)
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

    public static synchronized OpenMeteoConfig getInstance(Dotenv dotenv) {
        if (INSTANCE == null) {
            INSTANCE = new OpenMeteoConfig(
                dotenv.get("OFFICE_LAT"),
                dotenv.get("OFFICE_LON"),
                dotenv.get("HOME_LAT"),
                dotenv.get("HOME_LON"),
                dotenv.get("FORECAST_MODE"),
                Integer.valueOf(dotenv.get("FORECAST_DURATION_HOURS")),
                ZoneId.of(dotenv.get("TIME_ZONE"))
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

    public static synchronized VonageConfig getInstance(Dotenv dotenv) {
        if (INSTANCE == null) {
            INSTANCE = new VonageConfig(
                dotenv.get("PHONE_NUM"),
                dotenv.get("VONAGE_API_KEY"),
                dotenv.get("VONAGE_API_SECRET"),
                dotenv.get("VONAGE_COMM_MODE", "SMS").equals("SMS") ? CommMethod.SMS : CommMethod.WHATSAPP
            );
        }
        return INSTANCE;
    }
};

enum CommMethod {
    SMS,
    WHATSAPP
}
