package interface_adapter.chatbot;

import use_case.chatbot.ChatbotService;
import entity.ChatbotSession;
import view.ChatbotPanel;

/**
 * Controller for handling chatbot interactions.
 * Manages user messages, chatbot responses, and stores conversation history.
 */
public class ChatbotController {
    private final ChatbotPanel chatbotPanel;
    private final ChatbotService chatbotService;
    private ChatbotSession currentSession;

    public ChatbotController(ChatbotPanel chatbotPanel, ChatbotService chatbotService) {
        this.chatbotPanel = chatbotPanel;
        this.chatbotService = chatbotService;

        // Add listener for the send button in the panel
        this.chatbotPanel.addSendButtonListener(e -> handleSendButtonClick());
    }

    /**
     * Starts a new chatbot session or retrieves an existing one based on the session ID.
     *
     * @param sessionId unique identifier for the session
     */
    public void startChatSession(String sessionId) {
        currentSession = new ChatbotSession(sessionId);
    }

    /**
     * Handles the user's message to the chatbot.
     * Sends the message to the chatbot service and updates the conversation history.
     *
     * @param userMessage the message sent by the user
     */
    public void handleUserMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return;
        }

        // Add the user message to the session's conversation history
        currentSession.addMessage("User: " + userMessage);
        chatbotPanel.addMessage(userMessage, true);

        // Get the response from the chatbot service asynchronously (to avoid blocking the UI)
        new Thread(() -> {
            final String chatbotResponse = chatbotService.getChatbotResponse(currentSession.getSessionId(),

                    userMessage);


            // Add the chatbot's response to the session's conversation history
            currentSession.addMessage("AI: " + chatbotResponse);
            chatbotPanel.addMessage(chatbotResponse, false);
        }).start();

        // Clear the input field after sending the message
        chatbotPanel.clearInputField();
    }

    /**
     * Handles the send button click event.
     */
    private void handleSendButtonClick() {
        final String userMessage = chatbotPanel.getUserInput();
        handleUserMessage(userMessage);
    }

    /**
     * Handles the scenario when the user closes the chat or finishes the session.
     */
    public void endChatSession() {
        // Optionally, you can clear the session history when the chat ends
        if (currentSession != null) {
            currentSession.clearHistory();
        }
    }
}