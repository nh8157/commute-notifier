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
    double highestApparentTemp
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String MESSAGE_TEMPLATE = "{0}-{1} T{2}/{3} F{4}/{5})";

    public String summary() {
        return MessageFormat.format(MESSAGE_TEMPLATE,
            this.startTime.format(FORMATTER),
            this.endTime.format(FORMATTER),
            this.lowestTemp,
            this.highestTemp,
            this.lowestApparentTemp,
            this.highestApparentTemp
        );
    }

    public WeatherSummary(WeatherForecastClient.OpenMeteoForecast forecast) {
        this(
            forecast.time().get(0),
            forecast.time().get(forecast.time().size() - 1),
            Collections.min(forecast.temperature_2m()),
            Collections.max(forecast.temperature_2m()),
            Collections.min(forecast.apparent_temperature()),
            Collections.max(forecast.apparent_temperature())
        );
    }
};
