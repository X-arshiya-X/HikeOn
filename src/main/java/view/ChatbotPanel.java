package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import helpers.MarkDownUtils;

/**
 * ChatbotPanel is a JPanel that contains a conversation area, input field, and send button.
 */
public class ChatbotPanel extends JPanel {
    private static final int BORDER_LAYOUT_GAP = 10;
    private static final int FONT_SIZE = 14;
    private static final Color LIGHT_GREY = new Color(240, 240, 240);
    private static final Color USER_BUBBLE_COLOR = new Color(173, 216, 230);
    private static final Color AI_BUBBLE_COLOR = new Color(211, 211, 211);
    private static final String FONT_FAMILY = "Arial";

    private final JPanel conversationPanel;
    private final JScrollPane scrollPane;
    private final JTextField inputField;
    private final JButton sendButton;

    public ChatbotPanel() {
        setLayout(new BorderLayout(BORDER_LAYOUT_GAP, BORDER_LAYOUT_GAP));
        setBackground(LIGHT_GREY);

        conversationPanel = new JPanel();
        conversationPanel.setLayout(new BoxLayout(conversationPanel, BoxLayout.Y_AXIS));
        conversationPanel.setBackground(LIGHT_GREY);
        conversationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(conversationPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        inputField = new JTextField();
        inputField.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
        inputField.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)

        ));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE));

        final JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Adds a message bubble to the conversation panel.
     *
     * @param message the message to display
     * @param isUser  true if the message is from the user, false if from the AI
     */
    public void addMessage(String message, boolean isUser) {
        final JPanel messageBubble = new JPanel();
        messageBubble.setLayout(new BorderLayout());
        messageBubble.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final String htmlMessage = formatToHtml(message);
        final JLabel messageLabel = new JLabel(htmlMessage);
        messageLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(isUser ? USER_BUBBLE_COLOR : AI_BUBBLE_COLOR);
        messageLabel.setForeground(Color.BLACK);

        if (isUser) {
            messageBubble.add(messageLabel, BorderLayout.EAST);
        }

        else {
            messageBubble.add(messageLabel, BorderLayout.WEST);
        }

        conversationPanel.add(messageBubble);
        conversationPanel.add(Box.createVerticalStrut(10));
        conversationPanel.revalidate();
        conversationPanel.repaint();
    }

    /**
     * Converts Markdown to HTML using MarkdownUtils.
     *
     * @param message the message in Markdown
     * @return the formatted HTML string
     */
    private String formatToHtml(String message) {
        return "<html><body style='width: 200px; font-size: 14px; line-height: 1.6; padding: 5px;'>" +
                MarkDownUtils.markdownToHtml(message) +
                "</body></html>";
    }

    public String getUserInput() {
        return inputField.getText();
    }

    /**
     * Clears the input field.
     */
    public void clearInputField() {
        inputField.setText("");
    }

    /**
     * Adds an ActionListener to the send button.
     *
     * @param listener the ActionListener to add
     */
    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

}

