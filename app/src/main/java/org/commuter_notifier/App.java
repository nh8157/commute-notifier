package org.commuter_notifier;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class App {
    private static final Config CONFIG = Config.getInstance();

    private static final String MESSAGE_TEMPLATE = "From {0} to {1}, temperature ranges from {2} to {3} (feels like {4} to {5}).\n";

    private record Minutely15Forecast(
        List<LocalDateTime> time,
        List<Double> temperature_2m,
        List<Double> apparent_temperature,
        List<Integer> precipitation_probability,
        List<Double> precipitation
    ) {};

    private record OpenMeteoForecast(
        Minutely15Forecast minutely_15
    ) {};


    public record WeatherStats(
        LocalDateTime startTime,
        LocalDateTime endTime,
        double lowestTemp,
        double highestTemp,
        double lowestApparentTemp,
        double highestApparentTemp,
        double highestPrecipitation,
        double highestPrecipitationProb
    ) {
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
    };

    public static WeatherStats fromMinutely15(
        List<LocalDateTime> timestamps,
        List<Double> temp,
        List<Double> apparentTemp,
        List<Double> precipitation,
        List<Integer> precipitation_prob
    ) {
        return new WeatherStats(
            timestamps.getFirst(),
            timestamps.getLast(),
            Collections.min(temp),
            Collections.max(temp),
            Collections.min(apparentTemp),
            Collections.max(apparentTemp),
            Collections.max(precipitation),
            precipitation_prob.get(precipitation.indexOf(Collections.max(precipitation)))
        );
    }

    public static void main(String[] args) throws Exception{
        String forecastStr = WeatherForecastClient.fromOpenMeteo(
            CONFIG.openMeteo().homeLat(),
            CONFIG.openMeteo().homeLon(),
            CONFIG.openMeteo().forecastMode(),
            CONFIG.openMeteo().timeZone().toString(),
            CONFIG.openMeteo().forecastDurationHours()
        );

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        OpenMeteoForecast forecast = gson.fromJson(forecastStr, OpenMeteoForecast.class);
        Minutely15Forecast forecast15 = forecast.minutely_15;
        // step 2: take the information, gauge if weather is suitable for cycling / tube running as usual
        // the CyclingWeatherSummary can have
        // 1. temperature (highest, lowest, avg), chances of raining, and precipitation, if any
        // 2. risk factors
        // 3. gear advice (whether to wear mudguard)
        // 2 and 3 can be derived from 1
        WeatherStats stats = fromMinutely15(forecast15.time, forecast15.temperature_2m, forecast15.apparent_temperature, forecast15.precipitation, forecast15.precipitation_probability);
        System.out.println(stats.summary());

        // step 3: post the result to the user via AWS SNS
        SmsClient smsClient = new SmsClient(CONFIG.vonage().apiKey(), CONFIG.vonage().apiSecret(), CONFIG.vonage().phoneNum());
        smsClient.sendSms(stats.summary());
    }
}
