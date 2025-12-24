package org.commuter_notifier;


public class App {
    private static final Config CONFIG = Config.getInstance();

    public static void main(String[] args) throws Exception{
        WeatherForecastClient.OpenMeteoForecast openMeteoForecast = WeatherForecastClient.getOpenMeteoForecast(
            CONFIG.openMeteo().homeLat(),
            CONFIG.openMeteo().homeLon(),
            CONFIG.openMeteo().forecastMode(),
            CONFIG.openMeteo().timeZone().toString(),
            CONFIG.openMeteo().forecastDurationHours()
        );

        WeatherSummary summary = new WeatherSummary(openMeteoForecast);

        SmsClient smsClient = new SmsClient(
            CONFIG.vonage().apiKey(),
            CONFIG.vonage().apiSecret(),
            CONFIG.vonage().phoneNum()
        );
        smsClient.sendSms(summary.summary());
    }
}
