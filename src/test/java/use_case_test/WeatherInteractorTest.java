package use_case_test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import frontend.controller.WeatherController;
import use_case.WeatherInteractor;

public class WeatherInteractorTest {

    private WeatherController weatherController;
    private WeatherInteractor weatherInteractor;

    @BeforeEach
    public void setUp() {
        // Create a WeatherController (not mocked)
        weatherController = new WeatherController();
        // Create the WeatherInteractor with the controller
        weatherInteractor = new WeatherInteractor(weatherController);
    }

    @Test
    public void testFetchWeatherForLocation_validLocation() {
        // Define the test input and output
        final String location = "Toronto";
        
        // Call the method under test
        final String actualWeather = weatherInteractor.fetchWeatherForLocation(location);

        // Verify the result is not null (assuming a real API call or pre-defined data is used)
        assertNotNull(actualWeather);
    }

    @Test
    public void testFetchWeatherForLocation_emptyLocation() {
        // Define the test input
        final String location = "";

        // Expect an IllegalArgumentException when calling the method with an empty location
        assertThrows(IllegalArgumentException.class, () -> {
            weatherInteractor.fetchWeatherForLocation(location);
        });
    }

    @Test
    public void testFetchWeatherForLocation_nullLocation() {
        // Define the test input
        final String location = null;

        // Expect an IllegalArgumentException when calling the method with a null location
        assertThrows(IllegalArgumentException.class, () -> {
            weatherInteractor.fetchWeatherForLocation(location);
        });
    }
}