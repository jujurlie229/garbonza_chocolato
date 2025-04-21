package view;

import model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main view class for the Treasure Hunt game.
 * Responsible for rendering the game state and providing UI components.
 */
public class GameView extends JFrame {
    // UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private WelcomeScreen welcomeScreen;
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private JLabel treasureLabel;
    private JButton hintButton;
    private JButton resetButton;

    // Constants
    private static final String WELCOME_CARD = "welcome";
    private static final String GAME_CARD = "game";

    /**
     * Constructor initializes the game UI.
     */
    public GameView() {
        setTitle("Treasure Hunt Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add window listener to clean up resources when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (gamePanel != null) {
                    gamePanel.dispose();
                }
            }
        });

        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create welcome screen
        welcomeScreen = new WelcomeScreen();

        // Create game screen
        JPanel gameScreen = createGameScreen();

        // Add screens to card layout
        mainPanel.add(welcomeScreen, WELCOME_CARD);
        mainPanel.add(gameScreen, GAME_CARD);

        // Add main panel to frame
        add(mainPanel);

        // Start with welcome screen
        cardLayout.show(mainPanel, WELCOME_CARD);

        // Set window properties
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Creates the game screen with game panel and controls.
     */
    private JPanel createGameScreen() {
        JPanel gameScreen = new JPanel(new BorderLayout());

        // Create game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
                GameModel.GRID_SIZE * GamePanel.getCellSize(),
                GameModel.GRID_SIZE * GamePanel.getCellSize()
        ));

        // Create control panel with game info and buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        scoreLabel = new JLabel("Score: 0");
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        scoreLabel.setFont(labelFont);

        treasureLabel = new JLabel("Treasures: 0/0");
        treasureLabel.setFont(labelFont);

        hintButton = new JButton("Hint (Cost: 3)");
        hintButton.setFont(labelFont);

        resetButton = new JButton("New Game");
        resetButton.setFont(labelFont);

        // Add tooltip to buttons
        hintButton.setToolTipText("Shows the path to the nearest treasure");
        resetButton.setToolTipText("Restart the game with a new map");

        controlPanel.add(scoreLabel);
        controlPanel.add(treasureLabel);
        controlPanel.add(hintButton);
        controlPanel.add(resetButton);

        // Add panels to frame
        gameScreen.add(gamePanel, BorderLayout.CENTER);
        gameScreen.add(controlPanel, BorderLayout.SOUTH);

        // Make the game panel focusable for keyboard input
        gamePanel.setFocusable(true);

        return gameScreen;
    }

    /**
     * Shows the game screen.
     */
    public void showGameScreen() {
        cardLayout.show(mainPanel, GAME_CARD);
        gamePanel.requestFocusInWindow();
    }

    /**
     * Updates the UI to reflect the current game state.
     */
    public void updateView(GameModel model) {
        // Update labels
        scoreLabel.setText("Score: " + model.getScore());
        treasureLabel.setText("Treasures: " + model.getTreasuresFound() +
                "/" + model.getTreasuresTotal());

        // Repaint the game panel
        gamePanel.setModel(model);
        gamePanel.repaint();

        // Request focus for keyboard input
        gamePanel.requestFocusInWindow();
    }

    /**
     * Shows a game over message with animation.
     */
    public void showGameOverMessage(boolean won, int finalScore) {
        // Create a modal dialog for game over
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setLayout(new BorderLayout());

        // Create content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create game over text
        JLabel titleLabel = new JLabel(won ? "Congratulations!" : "Game Over!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String message = won ?
                "You found all treasures!" :
                "You ran out of points.";
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("Final Score: " + finalScore);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add message to content panel
        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(messageLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(scoreLabel);
        content.add(Box.createVerticalStrut(20));

        // Create button to close dialog
        JButton closeButton = new JButton("Play Again");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());
        content.add(closeButton);
        content.add(Box.createVerticalGlue());

        // Add content to dialog
        dialog.add(content, BorderLayout.CENTER);

        // Set dialog properties
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Show the dialog
        dialog.setVisible(true);
    }

    /**
     * Gets the animation manager from the game panel.
     */
    public AnimationManager getAnimationManager() {
        return gamePanel.getAnimationManager();
    }

    /**
     * Adds key listener to the game panel.
     */
    public void addKeyboardListener(KeyListener listener) {
        gamePanel.addKeyListener(listener);
    }

    /**
     * Adds action listener to the hint button.
     */
    public void addHintButtonListener(ActionListener listener) {
        hintButton.addActionListener(listener);
    }

    /**
     * Adds action listener to the reset button.
     */
    public void addResetButtonListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    /**
     * Adds action listener to the start button in welcome screen.
     */
    public void addStartButtonListener(ActionListener listener) {
        welcomeScreen.addStartButtonListener(listener);
    }
}