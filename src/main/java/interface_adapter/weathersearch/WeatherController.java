package interface_adapter.weathersearch;

import use_case.weathersearch.WeatherInteractor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controller for handling weather-related requests.
 */
public class WeatherController {
    private final WeatherInteractor weatherService;

    public WeatherController() {
        this.weatherService = new WeatherInteractor();
    }

    /**
     * Fetches and returns formatted weather data for a given location.
     * @param location the location to fetch weather for
     * @return formatted weather data
     */
    public String getFormattedWeather(String location) {
        final String result;
        if (location == null || location.trim().isEmpty()) {
            result = "Location cannot be empty!";
        }
        else {
            // Fetch current weather and forecast data
            final String currentWeather = weatherService.getWeather(location);
            final List<String> rawForecasts = weatherService.getWeatherWithForecast(location);

            // Format the data
            result = formatWeather(currentWeather, rawForecasts);
        }
        return result;
    }

    /**
     * Formats current weather and forecast into a user-friendly string.
     * @param currentWeather the current weather condition
     * @param rawForecasts the raw list of forecast strings
     * @return a formatted weather string
     */
    private String formatWeather(String currentWeather, List<String> rawForecasts) {
        final StringBuilder formattedWeather = new StringBuilder();

        // Format current weather
        formattedWeather.append("Current Weather:\n");
        formattedWeather.append("- ").append(currentWeather).append("\n\n");

        // Format forecast
        formattedWeather.append("Forecast for the next 24 hours:\n");
        for (String rawForecast : rawForecasts) {
            // Assuming rawForecast is formatted as "timestamp|condition|temperature"
            final String[] parts = rawForecast.split("\\|");
            final int expectedPartsLength = 3;
            if (parts.length == expectedPartsLength) {
                final String time = formatTime(parts[0]);
                final String condition = parts[1];
                final String temperature = parts[2];

                formattedWeather.append("- ").append(time).append(": ")
                        .append(condition).append(", ")
                        .append(temperature).append(" degrees C\n");
            }
        }

        return formattedWeather.toString();
    }

    /**
     * Helper method to format time into a more readable format.
     * @param rawTime the raw timestamp (e.g., "2024-12-03 18:00:00")
     * @return formatted time as a string (e.g., "6:00 PM")
     */
    private String formatTime(String rawTime) {
        String formattedTime;
        try {
            final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");
            final Date date = inputFormat.parse(rawTime);
            formattedTime = outputFormat.format(date);
        }
        catch (ParseException parseException) {
            parseException.printStackTrace();
            // Return raw time if formatting fails
            formattedTime = rawTime;
        }
        return formattedTime;
    }
}