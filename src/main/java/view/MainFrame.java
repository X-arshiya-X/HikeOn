package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import backend.service.ChatbotService;
import entity.InMemoryUserStore;
import frontend.controller.ChatbotController;
import frontend.view.panels.ChatbotPanel;
import interface_adapter.autentication.login.LoginController;
import interface_adapter.autentication.logout.LogoutController;
import interface_adapter.locationSearch.LocationController;
import interface_adapter.locationSearch.LocationPresenter;
import interface_adapter.register.RegisterController;
import use_case.locationSearch.LocationInteractor;
import use_case.login.LoginInteractor;
import use_case.logout.LogoutInteractor;
import use_case.register.RegisterInteractor;

/**
 * Main frame for the HikeOn application.
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

    /**
     * The main method to launch the HikeOn application.
     *
     * */
    public static void launchMainFrame() {
        final LocationPresenter presenter = new LocationPresenter();
        final LocationInteractor interactor = new LocationInteractor(presenter);
        final LocationController controller = new LocationController(interactor);

        final LogoutInteractor logoutInteractor = new LogoutInteractor();
        final LogoutController logoutController = new LogoutController(logoutInteractor);

        final JFrame frame = createMainFrame(controller);
        frame.add(createTitlePanel(), BorderLayout.NORTH);
        frame.add(createCenterPanel(controller, logoutController, frame), BorderLayout.CENTER); // Pass frame here
        frame.add(createFooterPanel(), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static JFrame createMainFrame(LocationController controller) {
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

    private static JPanel createCenterPanel(LocationController locationController, LogoutController logoutController, JFrame frame) {
        final JPanel centerPanel = new JPanel(new GridBagLayout());
        final int borderSize = 20;
        centerPanel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
        final GridBagConstraints gbc = createGridBagConstraints();

        addLocationComponents(centerPanel, gbc, locationController);
        addWeatherComponents(centerPanel, gbc);
        addButtons(centerPanel, gbc, locationController, logoutController, frame); // Pass frame here

        return centerPanel;
    }


    private static GridBagConstraints createGridBagConstraints() {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET_SIZE, INSET_SIZE, INSET_SIZE, INSET_SIZE);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private static void addLocationComponents(JPanel panel, GridBagConstraints gbc,
                                              LocationController locationController) {
        final JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(new Font(FONT_NAME, Font.PLAIN, LOCATION_FONT_SIZE));

        final AutocompleteTextField locationField = new AutocompleteTextField(locationController);
        locationField.setColumns(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(locationLabel, gbc);
        gbc.gridx = 1;
        panel.add(locationField, gbc);

        panel.putClientProperty(LOCATIONTEXTFIELD, locationField);
    }

    private static void addWeatherComponents(JPanel panel, GridBagConstraints gbc) {
        final JLabel weatherLabel = new JLabel("Weather: ");
        final int weatherFontSize = 16;
        weatherLabel.setFont(new Font(FONT_NAME, Font.BOLD, weatherFontSize));
        weatherLabel.setForeground(Color.BLUE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(weatherLabel, gbc);

        panel.putClientProperty("WeatherLabel", weatherLabel);
    }

    private static void addButtons(JPanel panel, GridBagConstraints gbc, LocationController locationController, LogoutController logoutController, JFrame frame) {
        addWeatherButton(panel, gbc);
        addGoogleMapsButton(panel, gbc, locationController);
        addHikeOnAiButton(panel, gbc);
        addLogoutButton(panel, gbc, logoutController, frame); // Pass frame here
    }

    private static void addWeatherButton(JPanel panel, GridBagConstraints gbc) {
        final JButton fetchWeatherButton = createStyledButton("Get Weather", new Color(70, 130, 180),
                new Color(100, 149, 237));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(fetchWeatherButton, gbc);
    }

    private static void addGoogleMapsButton(JPanel panel, GridBagConstraints gbc,
                                            LocationController locationController) {
        final JButton googleMapsButton = createStyledButton("Find Nearby Hiking Trails",
                new Color(34, 139, 34), new Color(60, 179, 113));
        googleMapsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JTextField locationField = (JTextField) panel.getClientProperty(LOCATIONTEXTFIELD);
                final String location = locationField.getText();
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

    private static void addLogoutButton(JPanel panel, GridBagConstraints gbc, LogoutController logoutController, JFrame frame) {
        final JButton logoutButton = createStyledButton("Logout", new Color(255, 0, 0),
                new Color(255, 102, 102));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logoutController.logout(); // Perform logout
                frame.dispose(); // Close the MainFrame
                new LoginPage(new LoginController(new LoginInteractor()),
                        new RegisterController(new RegisterInteractor()))
                        .setVisible(true); // Relaunch the LoginPage
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(logoutButton, gbc);
    }

    private static void openChatbotWindow() {
        final JFrame chatbotFrame = new JFrame("HikeOn AI Chatbot");
        chatbotFrame.setSize(600, 400);
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