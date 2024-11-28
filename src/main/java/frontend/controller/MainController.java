package frontend.controller;

import java.io.IOException;
import java.util.List;

import backend.service.ChatbotService;
//import backend.service.LocationService;
import backend.service.WeatherService;
import frontend.model.Location;
import frontend.model.Weather;
import frontend.view.panels.ChatbotPanel;
import frontend.view.panels.HomePanel;

/**
 * MainController class to handle the main operations of the application.
 *
 * @null This class does not accept null values for its dependencies.
 */
public class MainController {
    private final HomePanel homePanel;
    private final WeatherService weatherService;
    //private final LocationService locationService;
    private final ChatbotService chatbotService;

    /**
     * Constructs the MainController with its dependencies and initializes
     * the weather display when the app starts.
     *
     * @param homePanel the main panel of the application
     * @param weatherService the service for fetching weather data
     * @param locationService the service for location-related operations
     * @param chatbotService the service for chatbot interactions
     * @throws IOException if there is an issue during initialization
     */
    public MainController(HomePanel homePanel, WeatherService weatherService, ChatbotService chatbotService) throws IOException {
        this.homePanel = homePanel;
        this.weatherService = weatherService;
        //this.locationService = locationService;
        this.chatbotService = chatbotService;
    }

    /**
     * Handles search requests from the user.
     *
     * @param query the search query entered by the user
     */
    // public void handleSearchRequest(String query) {
    //     // Fetch locations based on the search query
    //     final List<Location> locations = locationService.searchLocations(query);
    //     homePanel.displayLocationResults(locations);
    // }

    /**
     * Updates the weather data for the specified city and displays it in the HomePanel's WeatherPanel.
     *
     * @param city the name of the city for which to fetch and display weather data
     * @throws IOException if there is an issue during the weather data update
     * @throws IllegalArgumentException if the city name is wrongly entered
     */
    public void updateWeatherData(String city) throws IOException {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or empty.");
        }
        final Weather currentWeather = weatherService.getCurrentWeather(city.trim());
        if (currentWeather != null) {
            homePanel.updateWeatherPanel(currentWeather);
        }
    }

/**
 * Handles messages to/from the chatbot.
 *
 * @param userMessage the message entered by the user
 */
public void handleChatbotRequest(String userMessage) {
    // Ensure the user message is not null or empty
    if (userMessage == null || userMessage.trim().isEmpty()) {
        homePanel.getChatbotPanel().displayConversation("System: Please enter a valid message.\n");
        return;
    }

    try {
        // Ensure a session ID is created for chatbot interactions
        String sessionId = chatbotService.startSession(); // Manage sessions better in a real implementation

        // Get the chatbot response
        String chatbotResponse = chatbotService.getChatbotResponse(sessionId, userMessage.trim());

        // Append user and AI messages to the conversation area
        ChatbotPanel chatbotPanel = homePanel.getChatbotPanel();
        chatbotPanel.displayConversation(
            chatbotPanel.getUserInput() + "User: " + userMessage + "\nAI: " + chatbotResponse + "\n"
        );

        // Clear the input field
        chatbotPanel.clearInputField();

    } catch (Exception e) {
        // Handle errors gracefully
        homePanel.getChatbotPanel().displayConversation(
            "System: An error occurred while processing your request. Please try again.\n"
        );
        e.printStackTrace();
    }
}

public void initializeChatbotPanel() {
    ChatbotPanel chatbotPanel = homePanel.getChatbotPanel();

    // Add a listener to handle send button clicks
    chatbotPanel.addSendButtonListener(event -> {
        String userMessage = chatbotPanel.getUserInput();
        handleChatbotRequest(userMessage);
    });
}

}
