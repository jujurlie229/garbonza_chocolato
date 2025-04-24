package view;

import controller.GameController;
import model.GameModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main view class for the Treasure Hunt game.
 * Responsible for rendering the game state and providing UI components.
 * Updated with a minimalistic navy and gold theme.
 */
public class GameView extends JFrame {
    // UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private WelcomeScreen welcomeScreen;
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private JLabel treasureLabel;
    private JLabel statsLabel;
    private JButton hintBFSButton;
    private JButton hintAStarButton;
    private JButton resetButton;
    private GameController controller;

    // Constants
    private static final String WELCOME_CARD = "welcome";
    private static final String GAME_CARD = "game";

    /**
     * Constructor initializes the game UI.
     */
    public GameView() {
        setTitle("Treasure Hunt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set window icon if available
        // ImageIcon icon = new ImageIcon(getClass().getResource("/treasure_icon.png"));
        // setIconImage(icon.getImage());

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
        mainPanel.setBackground(Theme.NAVY);

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
        JPanel gameScreen = new JPanel(new BorderLayout(0, 0));
        gameScreen.setBackground(Theme.NAVY);

        // Create game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
                GameModel.GRID_SIZE * GamePanel.getCellSize(),
                GameModel.GRID_SIZE * GamePanel.getCellSize()
        ));

        // Add a small border around the game panel
        gamePanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Theme.GOLD));

        // Create statistics panel with navy background and gold text
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Theme.NAVY);
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        statsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        statsLabel = new JLabel("No path calculated yet");
        statsLabel.setFont(Theme.STATS_FONT);
        statsLabel.setForeground(Theme.TEXT_LIGHT);
        statsPanel.add(statsLabel);

        // Create control panel with game info and buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setBackground(Theme.NAVY);
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create button panel with navy background
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Theme.NAVY);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        // Create labels with gold text
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(Theme.HEADER_FONT);
        scoreLabel.setForeground(Theme.GOLD);

        treasureLabel = new JLabel("Treasures: 0/0");
        treasureLabel.setFont(Theme.HEADER_FONT);
        treasureLabel.setForeground(Theme.GOLD);

        // Create stylish buttons with navy and gold theme
        hintBFSButton = createStyledButton("BFS Hint");
        hintAStarButton = createStyledButton("A* Hint");
        resetButton = createStyledButton("New Game");

        // Add tooltip to buttons
        hintBFSButton.setToolTipText("Shows the path to the nearest treasure using BFS (Cost: 3)");
        hintAStarButton.setToolTipText("Shows the path to the nearest treasure using A* search (Cost: 3)");
        resetButton.setToolTipText("Restart the game with a new map");

        // Add game info to a separate panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Theme.NAVY);
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        infoPanel.add(scoreLabel);
        infoPanel.add(treasureLabel);

        // Add buttons to the button panel
        buttonPanel.add(hintBFSButton);
        buttonPanel.add(hintAStarButton);
        buttonPanel.add(resetButton);

        // Add panels to the control panel
        controlPanel.add(infoPanel, BorderLayout.NORTH);
        controlPanel.add(statsPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panels to frame
        gameScreen.add(gamePanel, BorderLayout.CENTER);
        gameScreen.add(controlPanel, BorderLayout.SOUTH);

        // Make the game panel focusable for keyboard input
        gamePanel.setFocusable(true);

        return gameScreen;
    }

    /**
     * Creates a styled button with navy and gold theme
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.BUTTON_FONT);
        button.setBackground(Theme.LIGHT_NAVY);
        button.setForeground(Theme.GOLD);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.DARK_NAVY);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.SOFT_GOLD, 1),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.LIGHT_NAVY);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.GOLD, 1),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });

        return button;
    }

    /**
     * Shows the game screen.
     */
    public void showGameScreen() {
        cardLayout.show(mainPanel, GAME_CARD);
        gamePanel.requestFocusInWindow();
    }

    /**
     * Updates the view to reflect the current game state.
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
     * Updates the algorithm statistics display.
     *
     * @param isBFS         true if BFS algorithm was used, false for A*
     * @param cellsExplored number of cells explored by the algorithm
     * @param pathLength    length of the found path
     */
    public void updateStatistics(boolean isBFS, int cellsExplored, int pathLength) {
        String algorithm = isBFS ? "BFS" : "A*";
        statsLabel.setText(String.format("%s: Explored %d cells, Path length: %d",
                algorithm, cellsExplored, pathLength));

        // Highlight the label based on algorithm type
        if (isBFS) {
            statsLabel.setForeground(Theme.PATH_HINT_BFS_COLOR); // Green for BFS
        } else {
            statsLabel.setForeground(Theme.PATH_HINT_ASTAR_COLOR); // Blue for A*
        }
    }

    /**
     * Shows a game over message with animation.
     */
    public void showGameOverMessage(boolean won, int finalScore) {
        // Create a modal dialog for game over
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Theme.NAVY);

        // Create content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        content.setBackground(Theme.NAVY);

        // Create game over text
        JLabel titleLabel = new JLabel(won ? "Congratulations!" : "Game Over!");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(won ? Theme.GOLD : Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String message = won ?
                "You found all treasures!" :
                "You ran out of points.";
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(Theme.NORMAL_FONT);
        messageLabel.setForeground(Theme.TEXT_LIGHT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("Final Score: " + finalScore);
        scoreLabel.setFont(Theme.HEADER_FONT);
        scoreLabel.setForeground(Theme.GOLD);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add message to content panel
        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(messageLabel);
        content.add(Box.createVerticalStrut(25));
        content.add(scoreLabel);
        content.add(Box.createVerticalStrut(25));

        // Create button to close dialog
        JButton closeButton = createStyledButton("Play Again");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());
        content.add(closeButton);
        content.add(Box.createVerticalGlue());

        // Add content to dialog
        dialog.add(content, BorderLayout.CENTER);

        // Set dialog properties
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Show the dialog
        dialog.setVisible(true);
    }

    /**
     * Shows a treasure found message with a continue button.
     */
    public void showTreasureFoundMessage(int treasuresFound, int treasuresTotal) {
        // Create a modal dialog for treasure found
        JDialog dialog = new JDialog(this, "Treasure Found!", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Theme.NAVY);

        // Create content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        content.setBackground(Theme.NAVY);

        // Create treasure found text with golden color
        JLabel titleLabel = new JLabel("Treasure Found!");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.GOLD);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create progress message
        JLabel progressLabel = new JLabel(
                treasuresFound + "/" + treasuresTotal + " treasure" +
                        (treasuresTotal > 1 ? "s" : "") + " found");
        progressLabel.setFont(Theme.HEADER_FONT);
        progressLabel.setForeground(Theme.TEXT_LIGHT);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add message to content panel
        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(25));
        content.add(progressLabel);
        content.add(Box.createVerticalStrut(30));

        // Create continue button
        JButton continueButton = createStyledButton("Continue");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // When continue button is pressed, resume the game
        continueButton.addActionListener(e -> {
            dialog.dispose();

            // Request focus back to game panel to continue gameplay
            gamePanel.requestFocusInWindow();

            // Resume the game in GameController
            if (controller != null) {
                controller.resumeGame();
            }
        });

        content.add(continueButton);
        content.add(Box.createVerticalGlue());

        // Add content to dialog
        dialog.add(content, BorderLayout.CENTER);

        // Set dialog properties
        dialog.setSize(350, 250);
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
     * Adds action listener to the BFS hint button.
     */
    public void addHintBFSButtonListener(ActionListener listener) {
        hintBFSButton.addActionListener(listener);
    }

    /**
     * Adds action listener to the A* hint button.
     */
    public void addHintAStarButtonListener(ActionListener listener) {
        hintAStarButton.addActionListener(listener);
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

    /**
     * Legacy method to maintain backward compatibility.
     * Adds action listener to the BFS hint button.
     */
    public void addHintButtonListener(ActionListener listener) {
        addHintBFSButtonListener(listener);
    }

    /**
     * Sets the controller reference for popup dialogs
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }
}