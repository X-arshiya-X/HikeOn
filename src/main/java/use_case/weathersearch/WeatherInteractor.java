package use_case.weathersearch;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Service for fetching weather data.
 */
public class WeatherInteractor {
    private static final String API_KEY = loadWeatherApiKey();
    private static final String WEATHER_API_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    private static final String FORECAST_API_URL =
            "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&appid=%s";

    /**
     * Fetches the current weather data for a given location.
     *
     * @param location the location to fetch weather for
     * @return a string describing the current weather
     */
    public String getWeather(String location) {
        String result;
        try {
            final String requestUrl = String.format(WEATHER_API_URL, location, API_KEY);
            final JSONObject weatherData = fetchWeatherData(requestUrl);

            // Parse and return current weather
            final String description = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
            final double temperature = weatherData.getJSONObject("main").getDouble("temp");
            result = String.format("Weather: %s, Temperature: %.2f degrees Celsius", description, temperature);
        }
        catch (IOException ioException) {
            result = "Error fetching current weather: " + ioException.getMessage();
        }
        return result;
    }

    /**
     * Fetches the weather and forecast data for a given location.
     *
     * @param location the location to fetch weather and forecast for
     * @return a string describing the current weather and forecast
     */
    public List<String> getWeatherWithForecast(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty!");
        }
        final List<String> forecastList = new ArrayList<>();
        try {
            // Current weather (you can skip this part if already handled elsewhere)
            final String forecastUrl = String.format(
                    "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&appid=%s",
                    location, API_KEY
            );

            // Fetch forecast data
            final JSONObject forecastData = fetchWeatherData(forecastUrl);
            final JSONArray forecastArray = forecastData.getJSONArray("list");

            // Process and store the next 5 forecast intervals
            for (int i = 0; i < 8; i++) {
                final JSONObject forecastEntry = forecastArray.getJSONObject(i);
                final String timestamp = forecastEntry.getString("dt_txt");
                final String condition = forecastEntry.getJSONArray("weather").getJSONObject(0)
                        .getString("description");
                final double temp = forecastEntry.getJSONObject("main").getDouble("temp");

                forecastList.add(String.format("%s|%s|%.1f", timestamp, condition, temp));
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            forecastList.add("Unable to fetch forecast data.");
        }
        return forecastList;
    }

    private static String loadWeatherApiKey() {
        final Dotenv dotenv = Dotenv.configure()
                // Change the filename if necessary
                .filename(".env")
                // Specify the correct directory
                .directory("/Users/jackli/Downloads/HikeOn")
                .load();
        final String apiKey = dotenv.get("OPENWEATHER_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Weather API Key not found in Weather_key.env file");
        }
        return apiKey;
    }

    /**
     * Helper method to fetch JSON data from the OpenWeather API.
     *
     * @param requestUrl the API request URL
     * @return the response as a JSONObject
     * @throws IOException if the request fails
     */
    private JSONObject fetchWeatherData(String requestUrl) throws IOException {
        final URI uri = URI.create(requestUrl);
        final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != 200) {
            throw new IOException("Failed to fetch data: HTTP " + connection.getResponseCode());
        }

        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            final StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            return new JSONObject(response.toString());
        }
    }
}
