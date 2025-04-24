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
 */
public class GameView extends JFrame {
    // UI components
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

    // constants
    private static final String WELCOME_CARD = "welcome";
    private static final String GAME_CARD = "game";

    /**
     * initializeing the game UI.
     */
    public GameView() {
        setTitle("Treasure Hunt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (gamePanel != null) {
                    gamePanel.dispose();
                }
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Theme.NAVY);

        welcomeScreen = new WelcomeScreen();

        JPanel gameScreen = createGameScreen();

        mainPanel.add(welcomeScreen, WELCOME_CARD);
        mainPanel.add(gameScreen, GAME_CARD);

        add(mainPanel);

        cardLayout.show(mainPanel, WELCOME_CARD);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * creates the game screen with game panel and controls.
     */
    private JPanel createGameScreen() {
        JPanel gameScreen = new JPanel(new BorderLayout(0, 0));
        gameScreen.setBackground(Theme.NAVY);

        // create game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
                GameModel.GRID_SIZE * GamePanel.getCellSize(),
                GameModel.GRID_SIZE * GamePanel.getCellSize()
        ));

        gamePanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Theme.GOLD));

        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Theme.NAVY);
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        statsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        statsLabel = new JLabel("No path calculated yet");
        statsLabel.setFont(Theme.STATS_FONT);
        statsLabel.setForeground(Theme.TEXT_LIGHT);
        statsPanel.add(statsLabel);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setBackground(Theme.NAVY);
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Theme.NAVY);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(Theme.HEADER_FONT);
        scoreLabel.setForeground(Theme.GOLD);

        treasureLabel = new JLabel("Treasures: 0/0");
        treasureLabel.setFont(Theme.HEADER_FONT);
        treasureLabel.setForeground(Theme.GOLD);

        hintBFSButton = createStyledButton("BFS Hint");
        hintAStarButton = createStyledButton("A* Hint");
        resetButton = createStyledButton("New Game");

        hintBFSButton.setToolTipText("Shows the path to the nearest treasure using BFS (Cost: 3)");
        hintAStarButton.setToolTipText("Shows the path to the nearest treasure using A* search (Cost: 3)");
        resetButton.setToolTipText("Restart the game with a new map");

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Theme.NAVY);
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        infoPanel.add(scoreLabel);
        infoPanel.add(treasureLabel);

        buttonPanel.add(hintBFSButton);
        buttonPanel.add(hintAStarButton);
        buttonPanel.add(resetButton);

        controlPanel.add(infoPanel, BorderLayout.NORTH);
        controlPanel.add(statsPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        gameScreen.add(gamePanel, BorderLayout.CENTER);
        gameScreen.add(controlPanel, BorderLayout.SOUTH);

        gamePanel.setFocusable(true);

        return gameScreen;
    }

    /**
     * creates a styled button with navy and gold theme
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
     * shows the game screen.
     */
    public void showGameScreen() {
        cardLayout.show(mainPanel, GAME_CARD);
        gamePanel.requestFocusInWindow();
    }

    /**
     * updates the view to reflect the current game state.
     */
    public void updateView(GameModel model) {
        scoreLabel.setText("Score: " + model.getScore());
        treasureLabel.setText("Treasures: " + model.getTreasuresFound() +
                "/" + model.getTreasuresTotal());

        gamePanel.setModel(model);
        gamePanel.repaint();

        gamePanel.requestFocusInWindow();
    }

    /**
     * Updates the algorithm statistics display.
     */
    public void updateStatistics(boolean isBFS, int cellsExplored, int pathLength) {
        String algorithm = isBFS ? "BFS" : "A*";
        statsLabel.setText(String.format("%s: Explored %d cells, Path length: %d",
                algorithm, cellsExplored, pathLength));

        if (isBFS) {
            statsLabel.setForeground(Theme.PATH_HINT_BFS_COLOR);
        } else {
            statsLabel.setForeground(Theme.PATH_HINT_ASTAR_COLOR);
        }
    }

    /**
     * shows a game over message with animation.
     */
    public void showGameOverMessage(boolean won, int finalScore) {
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Theme.NAVY);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        content.setBackground(Theme.NAVY);

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

        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(messageLabel);
        content.add(Box.createVerticalStrut(25));
        content.add(scoreLabel);
        content.add(Box.createVerticalStrut(25));

        JButton closeButton = createStyledButton("Play Again");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());
        content.add(closeButton);
        content.add(Box.createVerticalGlue());

        dialog.add(content, BorderLayout.CENTER);

        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.setVisible(true);
    }

    /**
     * shows a treasure found message with a continue button.
     */
    public void showTreasureFoundMessage(int treasuresFound, int treasuresTotal) {
        JDialog dialog = new JDialog(this, "Treasure Found!", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Theme.NAVY);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        content.setBackground(Theme.NAVY);

        JLabel titleLabel = new JLabel("Treasure Found!");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.GOLD);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel progressLabel = new JLabel(
                treasuresFound + "/" + treasuresTotal + " treasure" +
                        (treasuresTotal > 1 ? "s" : "") + " found");
        progressLabel.setFont(Theme.HEADER_FONT);
        progressLabel.setForeground(Theme.TEXT_LIGHT);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(25));
        content.add(progressLabel);
        content.add(Box.createVerticalStrut(30));

        JButton continueButton = createStyledButton("Continue");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        continueButton.addActionListener(e -> {
            dialog.dispose();

            gamePanel.requestFocusInWindow();

            if (controller != null) {
                controller.resumeGame();
            }
        });

        content.add(continueButton);
        content.add(Box.createVerticalGlue());

        dialog.add(content, BorderLayout.CENTER);

        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.setVisible(true);
    }

    /**
     * gets the animation manager from the game panel.
     */
    public AnimationManager getAnimationManager() {
        return gamePanel.getAnimationManager();
    }

    /**
     * adds key listener to the game panel.
     */
    public void addKeyboardListener(KeyListener listener) {
        gamePanel.addKeyListener(listener);
    }

    /**
     * adds action listener to the BFS hint button.
     */
    public void addHintBFSButtonListener(ActionListener listener) {
        hintBFSButton.addActionListener(listener);
    }

    /**
     * adds action listener to the A* hint button.
     */
    public void addHintAStarButtonListener(ActionListener listener) {
        hintAStarButton.addActionListener(listener);
    }

    /**
     * adds action listener to the reset button.
     */
    public void addResetButtonListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    /**
     * adds action listener to the start button in welcome screen.
     */
    public void addStartButtonListener(ActionListener listener) {
        welcomeScreen.addStartButtonListener(listener);
    }

    /**
     * Legacy method to maintain backward compatibility.
     * adds action listener to the BFS hint button.
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