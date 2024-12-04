package helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP client utility class.
 *
 * @null
 */
public class HttpClient {

    /**
     * Timeout value for the connection and read operations in milliseconds.
     */
    private static final int TIMEOUT = 5000;

    /**
     * Sends a GET request to the specified URL and returns the response as a string.
     *
     * @param urlString the URL to send the GET request to
     * @return the response from the API as a string
     */
    public static String sendGetRequest(String urlString) {
        final StringBuilder response = new StringBuilder();
        try {
            // Create a URL object from the string
            final URL url = UrlConverter.convertToUrl(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Set the connection and read timeouts
            connection.setConnectTimeout(TIMEOUT);
            // Timeout while reading data
            connection.setReadTimeout(TIMEOUT);

            // Get the response code
            final int responseCode = connection.getResponseCode();
            // If the response code is OK, read the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } 
            else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } 
        catch (java.net.MalformedURLException malformedUrlException) {
            malformedUrlException.printStackTrace();
        }
        catch (java.io.IOException ioException) {
            ioException.printStackTrace();
        }
        // Return the API response as a string
        return response.toString();
    }
}

