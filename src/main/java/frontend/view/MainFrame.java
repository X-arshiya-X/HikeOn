package frontend.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import backend.service.ChatbotService;
import frontend.controller.ChatbotController;
import frontend.controller.LocationController;
import frontend.controller.WeatherController;
import frontend.view.panels.ChatbotPanel;

/**
 * Main frame for the HikeOn application.
 *
 * @null
 */
public class MainFrame {

    private static final String FONT_NAME = "Arial";
    private static final int TITLE_FONT_SIZE = 24;
    private static final int FOOTER_FONT_SIZE = 12;
    private static final int TITLE_COLOR_RED = 34;
    private static final int TITLE_COLOR_GREEN = 139;
    private static final int TITLE_COLOR_BLUE = 34;
    private static final int LOCATION_FONT_SIZE = 16;
    private static final int BUTTON_FONT_SIZE = 14;

    private static final int INSET_SIZE = 10;

    private static final String LOCATIONTEXTFIELD = "locationTextField";
    private static final int LOCATION_FIELD_COLUMNS = 15;

    /**
     * The main method to launch the HikeOn application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final JFrame frame = createMainFrame();
        frame.add(createTitlePanel(), BorderLayout.NORTH);
        frame.add(createCenterPanel(), BorderLayout.CENTER);
        frame.add(createFooterPanel(), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static JFrame createMainFrame() {
        final JFrame frame = new JFrame("HikeOn Outdoor Event Planner");
        final int frameWidth = 600;
        final int frameHeight = 600;
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        return frame;
    }

    private static JPanel createTitlePanel() {
        final JLabel titleLabel = new JLabel("HikeOn");
        titleLabel.setForeground(new Color(TITLE_COLOR_RED, TITLE_COLOR_GREEN, TITLE_COLOR_BLUE));
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, TITLE_FONT_SIZE));
        final JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);
        return titlePanel;
    }

    private static JPanel createCenterPanel() {
        final JPanel centerPanel = new JPanel(new GridBagLayout());
        final int borderSize = 20;
        centerPanel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
        final GridBagConstraints gbc = createGridBagConstraints();

        addLocationComponents(centerPanel, gbc);
        addWeatherComponents(centerPanel, gbc);
        addButtons(centerPanel, gbc);

        return centerPanel;
    }

    public static int getInsetSize() {
        return INSET_SIZE;
    }

    private static GridBagConstraints createGridBagConstraints() {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET_SIZE, INSET_SIZE, INSET_SIZE, INSET_SIZE);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private static void addLocationComponents(JPanel panel, GridBagConstraints gbc) {
        final JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(new Font(FONT_NAME, Font.PLAIN, LOCATION_FONT_SIZE));

        final JTextField locationField = new JTextField(LOCATION_FIELD_COLUMNS);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(locationLabel, gbc);
        gbc.gridx = 1;
        panel.add(locationField, gbc);

        panel.putClientProperty(LOCATIONTEXTFIELD, locationField);
    }

    private static void addWeatherComponents(JPanel panel, GridBagConstraints gbc) {
        final JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        weatherPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
        weatherPanel.setBackground(new Color(240, 248, 255));

        // Label for the weather
        final JLabel weatherLabel = new JLabel("Weather & Forecast:");
        weatherLabel.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        weatherLabel.setForeground(new Color(70, 130, 180));
        weatherPanel.add(weatherLabel);

        // Text area for displaying weather data and forecast
        final JTextArea weatherDisplay = new JTextArea(10, 30); // Multi-line display
        weatherDisplay.setEditable(false);
        weatherDisplay.setLineWrap(true);
        weatherDisplay.setWrapStyleWord(true);
        weatherDisplay.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        weatherDisplay.setBackground(new Color(255, 255, 255));
        weatherDisplay.setForeground(new Color(50, 50, 50));

        // Scroll pane to handle overflow
        final JScrollPane scrollPane = new JScrollPane(weatherDisplay);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 200));
        weatherPanel.add(scrollPane);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(weatherPanel, gbc);

        panel.putClientProperty("WeatherDisplay", weatherDisplay);
}

    private static void addButtons(JPanel panel, GridBagConstraints gbc) {
        addWeatherButton(panel, gbc);
        addGoogleMapsButton(panel, gbc);
        addHikeOnAiButton(panel, gbc);
    }

    private static void addWeatherButton(JPanel panel, GridBagConstraints gbc) {
        final JButton fetchWeatherButton = createStyledButton("Get Weather", new Color(70, 130, 180),
                new Color(100, 149, 237));
    
        // Create an instance of the WeatherController
        final WeatherController weatherController = new WeatherController();
    
        // Action listener for the "Get Weather" button
        fetchWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String location = ((JTextField) panel.getClientProperty(LOCATIONTEXTFIELD)).getText();
                final JTextArea weatherDisplay = (JTextArea) panel.getClientProperty("WeatherDisplay");
    
                if (location.isEmpty()) {
                    weatherDisplay.setText("Please enter a location.");
                    return;
                }
    
                // Fetch the weather using the WeatherController
                new Thread(() -> {
                    final String weatherData = weatherController.getFormattedWeather(location);
                    SwingUtilities.invokeLater(() -> weatherDisplay.setText(weatherData));
                }).start();
            }
        });
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(fetchWeatherButton, gbc);
    }

    private static void addGoogleMapsButton(JPanel panel, GridBagConstraints gbc) {
        final JButton googleMapsButton = createStyledButton("Find Nearby Hiking Trails", new Color(34, 139, 34),
                new Color(60, 179, 113));

        // Action listener for the "Find Nearby Hiking Trails" button
        googleMapsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String location = ((JTextField) panel.getClientProperty(LOCATIONTEXTFIELD)).getText();
                final LocationController locationController = new LocationController();
                locationController.searchHikingSpots(location);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(googleMapsButton, gbc);
    }

    private static void addHikeOnAiButton(JPanel panel, GridBagConstraints gbc) {
        final JButton hikeOnAiButton = createStyledButton("HikeOn AI", new Color(255, 165, 0),
                new Color(255, 200, 0));

        // Action listener for the "HikeOn AI" button
        hikeOnAiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openChatbotWindow();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(hikeOnAiButton, gbc);
    }

    private static void openChatbotWindow() {
        final JFrame chatbotFrame = new JFrame("HikeOn AI Chatbot");
        final int chatbotFrameWidth = 600;
        final int chatbotFrameHeight = 400;
        chatbotFrame.setSize(chatbotFrameWidth, chatbotFrameHeight);
        chatbotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatbotFrame.setLayout(new BorderLayout());

        // Create instances of ChatbotPanel and ChatbotService
        final ChatbotPanel chatbotPanel = new ChatbotPanel();
        final ChatbotService chatbotService = new ChatbotService();

        // Create the ChatbotController
        final ChatbotController chatbotController = new ChatbotController(chatbotPanel, chatbotService);

        // Start a new chatbot session
        final String sessionId = chatbotService.startSession();
        chatbotController.startChatSession(sessionId);

        // Add the chatbot panel to the frame
        chatbotFrame.add(chatbotPanel, BorderLayout.CENTER);

        // Add functionality to the send button
        chatbotPanel.addSendButtonListener(Send -> {
            final String userInput = chatbotPanel.getUserInput();
            if (!userInput.isEmpty()) {
                chatbotController.handleUserMessage(userInput);
                chatbotPanel.clearInputField();
            }
        });

        chatbotFrame.setVisible(true);
    }

    private static JPanel createFooterPanel() {
        final JPanel footerPanel = new JPanel();
        final JLabel footerLabel = new JLabel("Stay safe and enjoy the outdoors!");
        footerLabel.setFont(new Font(FONT_NAME, Font.ITALIC, FOOTER_FONT_SIZE));
        footerPanel.add(footerLabel);
        return footerPanel;
    }

    private static JButton createStyledButton(String text, Color defaultColor, Color hoverColor) {
        final JButton button = new JButton(text);
        button.setBackground(defaultColor);
        button.setFont(new Font(FONT_NAME, Font.BOLD, BUTTON_FONT_SIZE));
        button.setFocusPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(defaultColor));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(defaultColor);
            }
        });

        return button;
    }
}
