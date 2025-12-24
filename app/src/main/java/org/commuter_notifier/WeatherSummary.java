package org.commuter_notifier;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;

public record WeatherSummary(
    LocalDateTime startTime,
    LocalDateTime endTime,
    double lowestTemp,
    double highestTemp,
    double lowestApparentTemp,
    double highestApparentTemp
) {
    private static final String MESSAGE_TEMPLATE = "From {0} to {1}, temperature ranges from {2} to {3} (feels like {4} to {5}).\n";

    public String summary() {
        return MessageFormat.format(
            MESSAGE_TEMPLATE,
            this.startTime,
            this.endTime,
            this.lowestTemp,
            this.highestTemp,
            this.lowestApparentTemp,
            this.highestApparentTemp
        );
    }

    public WeatherSummary(WeatherForecastClient.OpenMeteoForecast forecast) {
        this(
            forecast.time().getFirst(),
            forecast.time().getLast(),
            Collections.min(forecast.temperature_2m()),
            Collections.max(forecast.temperature_2m()),
            Collections.min(forecast.apparent_temperature()),
            Collections.max(forecast.apparent_temperature())
        );
    }
};
