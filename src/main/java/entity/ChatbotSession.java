package entity;

import java.util.ArrayList;
import java.util.List;

public class ChatbotSession {

    private String sessionId;  // Unique ID for the session
    private List<String> conversationHistory;  // Stores the conversation history

    // Constructor
    public ChatbotSession(String sessionId) {
        this.sessionId = sessionId;
        this.conversationHistory = new ArrayList<>();
    }

    // Getter for sessionId
    public String getSessionId() {
        return sessionId;
    }

    // Getter for conversationHistory
    public List<String> getConversationHistory() {
        return conversationHistory;
    }

    // Method to add a message to the conversation history
    public void addMessage(String message) {
        conversationHistory.add(message);
    }

    // Optionally, method to clear history (if needed)
    public void clearHistory() {
        conversationHistory.clear();
    }
}
