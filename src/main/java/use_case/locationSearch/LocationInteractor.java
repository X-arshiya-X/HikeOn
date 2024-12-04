package use_case.locationSearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.GeoLocation;
import entity.HikingSpot;
import helpers.HttpClient;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * The concrete implementation of the user case of searching location.
 */
public class LocationInteractor implements LocationInputBoundary {
    private static final String GOOGLE_API_KEY = loadGoogleApiKey();
    private static final String GEOMETRY = "geometry";
    private static final String LOCATION = "location";
    private final LocationOutputBoundary presenter;

    public LocationInteractor(LocationOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public LocationOutputData searchHikingSpots(LocationInputData inputData) {
        final GeoLocation geoLocation = getCoordinates(inputData.getLocation());
        final List<HikingSpot> spots = findNearbyHikingSpots(geoLocation, inputData.getLocation());
        final LocationOutputData outputData = new LocationOutputData(spots, inputData.getLocation());
        presenter.presentLocationResults(outputData);
        return outputData;
    }

    @Override
    public List<String> suggestLocations(String input) {
        final String apiUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                + prepareUrl(input) + GOOGLE_API_KEY;
        final String response = HttpClient.sendGetRequest(apiUrl);
        final JSONObject responseJson = new JSONObject(response);
        final JSONArray predictions = responseJson.getJSONArray("predictions");

        final List<String> suggestions = new ArrayList<>();
        for (int i = 0; i < predictions.length(); i++) {
            suggestions.add(predictions.getJSONObject(i).getString("description"));
        }
        return suggestions;
    }

    public GeoLocation getCoordinates(String location) {
        final String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + prepareUrl(location) + GOOGLE_API_KEY;
        final String response = HttpClient.sendGetRequest(apiUrl);
        final JSONObject responseJson = new JSONObject(response);
        final JSONArray results = responseJson.getJSONArray("results");

        if (!results.isEmpty()) {
            final JSONObject locationJson = results.getJSONObject(0)
                    .getJSONObject(GEOMETRY).getJSONObject(LOCATION);
            return new GeoLocation(locationJson.getDouble("lat"), locationJson.getDouble("lng"));
        }
        else {
            throw new IllegalArgumentException("Invalid location!");
        }
    }

    public List<HikingSpot> findNearbyHikingSpots(GeoLocation geoLocation, String location) {
        final String apiUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?"
                + "query=hiking+trails" + location.replace(" ", "+") + "&location="
                + geoLocation.getLatitude() + "," + geoLocation.getLongitude()
                + "&radius=50000" + "&key=" + GOOGLE_API_KEY;
        final String response = HttpClient.sendGetRequest(apiUrl);
        final JSONObject responseJson = new JSONObject(response);
        final JSONArray results = responseJson.getJSONArray("results");

        final List<HikingSpot> hikingSpots = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            final JSONObject spotJson = results.getJSONObject(i);
            hikingSpots.add(new HikingSpot(
                    spotJson.getString("name"),
                    spotJson.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble("lat"),
                    spotJson.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble("lng"),
                    spotJson.optDouble("rating", -1),
                    spotJson.optInt("user_ratings_total", -1)
            ));
        }
        return hikingSpots;
    }

    /**
     * Retrieve the api key for google.
     * @return the api key.
     */
    private static String loadGoogleApiKey() {
        final Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .directory("/Users/jackli/Downloads/HikeOn")
                .load();
        return dotenv.get("Google_API_KEY");
    }

    private static String prepareUrl(String input) {
        return input.replace(" ", "+") + "&key=";
    }
}
