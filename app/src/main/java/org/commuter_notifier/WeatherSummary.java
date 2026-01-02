package org.commuter_notifier;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public record WeatherSummary(
    LocalDateTime startTime,
    LocalDateTime endTime,
    double lowestTemp,
    double highestTemp,
    double lowestApparentTemp,
    double highestApparentTemp,
    double highestPrecipitationRate,
    double highestPrecipitationProb
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String BASE_TEMPLATE = "{0}-{1} T{2}/{3} F{4}/{5}";
    private static final String PRECIPITATION_TEMPLATE = "RA{0}% {1}";

    private String generateMetarBaseMsg() {
        return MessageFormat.format(BASE_TEMPLATE,
            this.startTime.format(FORMATTER),
            this.endTime.format(FORMATTER),
            this.lowestTemp,
            this.highestTemp,
            this.lowestApparentTemp,
            this.highestApparentTemp
        );
    }

    private String generateMetarPrecipitationMsg() {
        String rainIntensity;
        if (highestPrecipitationRate == 0) {
            return "";
        } else if (highestPrecipitationRate < 2.5) {
            rainIntensity = "-RA";
        } else if (highestPrecipitationRate <= 7.6) {
            rainIntensity = "RA";
        } else {
            rainIntensity = "+RA";
        }
        return MessageFormat.format(PRECIPITATION_TEMPLATE,
            highestPrecipitationProb * 100,
            rainIntensity
        );
    }

    public String generateMetarSummary() {
        return String.join(" ", generateMetarBaseMsg(), generateMetarPrecipitationMsg()).trim();
    }

    public WeatherSummary(WeatherForecastClient.OpenMeteoForecast forecast) {
        this(
            forecast.time().get(0),
            forecast.time().get(forecast.time().size() - 1),
            Collections.min(forecast.temperature_2m()),
            Collections.max(forecast.temperature_2m()),
            Collections.min(forecast.apparent_temperature()),
            Collections.max(forecast.apparent_temperature()),
            Collections.max(forecast.precipitation()) * 4,
            Collections.max(forecast.precipitation_probability())
        );
    }
};
