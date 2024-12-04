package use_case.chatbot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

public class ChatbotService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = loadApiKey();
    private static final int HTTP_STATUS_OK = 200;

    // Thread-safe session management
    private final Map<String, List<String>> sessionHistories = new ConcurrentHashMap<>();

    public ChatbotService() { 

    }

    private static String loadApiKey() {
        final Dotenv dotenv = Dotenv.configure()
                                    .filename(".env")
                                    .directory("/Users/jackli/Downloads/HikeOn")
                                    .load();
        final String apiKey = dotenv.get("OPEN_AI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API Key not found in OpenAI.env file");
        }
        return apiKey;
    }

    /**
     * Starts a new session and returns the session ID.
     *
     * @return the session ID
     */
    public String startSession() {
        final String sessionId = UUID.randomUUID().toString();
        sessionHistories.put(sessionId, new ArrayList<>());
        return sessionId;
    }

    /**
     * Retrieves the chatbot response for a given session and user message.
     *
     * @param sessionId the session ID
     * @param userMessage the user's message
     * @return the chatbot's response
     * @throws IllegalArgumentException if the session ID is invalid
     */
    public String getChatbotResponse(String sessionId, String userMessage) {
        String chatbotResponse = "An error occurred. Please try again later.";
        if (!sessionHistories.containsKey(sessionId)) {
            throw new IllegalArgumentException("Invalid session ID.");
        }

        sessionHistories.get(sessionId).add("User: " + userMessage);

        final String requestBody = """
                {
                    "model": "gpt-4o-mini",
                    "messages": [
                        {"role": "system", "content": "You are a hiking and activity assistant. Note that the season is winters"},
                        {"role": "user", "content": "%s"}
                    ]
                }
                """.formatted(userMessage);

        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            final HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HTTP_STATUS_OK) {
                chatbotResponse = extractResponse(response.body());
                sessionHistories.get(sessionId).add("AI: " + chatbotResponse);
            }
            else {
                throw new RuntimeException("API call failed with status " + response.statusCode()
                    + ": " + response.body());
            }
        }
        catch (IOException | InterruptedException ex) {
            sessionHistories.get(sessionId).add("AI: Error occurred while processing your request.");
        }
        return chatbotResponse;
    }

    /**
     * Retrieves the conversation history for a given session.
     *
     * @param sessionId the session ID
     * @return the conversation history as a list of messages
     */
    public List<String> getConversationHistory(String sessionId) {
        return sessionHistories.getOrDefault(sessionId, Collections.emptyList());
    }

    /**
     * Extracts the chatbot response from the API response body.
     *
     * @param responseBody the response body from the API
     * @return the extracted chatbot response
     */
    private String extractResponse(String responseBody) {
        String result;
        try {
            final JsonNode rootNode = new ObjectMapper().readTree(responseBody);
            final JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                result = choicesNode.get(0).path("message").path("content").asText();
            }
            else {
                throw new IOException("Invalid JSON response structure.");
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
            result = "Error parsing response.";
        }
        return result;
    }
}
