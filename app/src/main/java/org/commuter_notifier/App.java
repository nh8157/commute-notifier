package org.commuter_notifier;

import java.util.Map;

public class App {
    public static void run(Map<String, Object> input) throws Exception{
        final Config CONFIG = Config.getInstance(input);

        WeatherForecastClient.OpenMeteoForecast openMeteoForecast = WeatherForecastClient.getOpenMeteoForecast(
            CONFIG.openMeteo().lat(),
            CONFIG.openMeteo().lon(),
            CONFIG.openMeteo().forecastMode(),
            CONFIG.openMeteo().timeZone().toString(),
            CONFIG.openMeteo().forecastDurationHours()
        );

        TubeStatusClient.TubeStatus tubeStatus = TubeStatusClient.getTubeStatus("jubilee");

        CommuteSummary summary = new CommuteSummary(openMeteoForecast, tubeStatus);

        SmsClient smsClient = new SmsClient(
            CONFIG.vonage().apiKey(),
            CONFIG.vonage().apiSecret(),
            CONFIG.vonage().phoneNum()
        );
        smsClient.sendSms(summary.generateMetarSummary());
    }
}
