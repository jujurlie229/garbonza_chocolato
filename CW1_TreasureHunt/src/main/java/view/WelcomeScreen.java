package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Welcome screen class that displays the game rules at startup.
 */
public class WelcomeScreen extends JPanel {
    private JButton startButton;

    /**
     * Constructor initializes the welcome screen with game rules.
     */
    public WelcomeScreen() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 500));

        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Treasure Hunt");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titlePanel.add(titleLabel);

        // Rules panel
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String[] rules = {
                "<html><h2>Game Rules:</h2></html>",
                "<html><b>Objective:</b> Find all 3 treasures before running out of points.</html>",
                "<html><b>Controls:</b> Use arrow keys to move your character (blue square).</html>",
                "<html><b>Scoring:</b></html>",
                "<html>• You start with 100 points.</html>",
                "<html>• Each move costs 1 point.</html>",
                "<html>• Hitting an obstacle costs 10 points.</html>",
                "<html>• Using a hint costs 3 points.</html>",
                "<html><b>Map Elements:</b></html>",
                "<html>• <span style='color:blue'>Blue</span>: Player</html>",
                "<html>• <span style='color:black'>Black</span>: Obstacle</html>",
                "<html>• <span style='color:#CCCC00'>Yellow</span>: Treasure</html>",
                "<html>• <span style='color:#64C864'>Green</span>: BFS path hint</html>",
                "<html>• <span style='color:#6496FA'>Blue</span>: A* path hint</html>",
                "<html><b>Hint Options:</b></html>",
                "<html>• <b>BFS Hint:</b> Uses Breadth-First Search algorithm to find the shortest path.</html>",
                "<html>• <b>A* Hint:</b> Uses A* Search algorithm with Manhattan distance heuristic.</html>",
                "<html><b>Game Over:</b> The game ends when you collect all treasures or run out of points.</html>"
        };

        for (String rule : rules) {
            JLabel ruleLabel = new JLabel(rule);
            ruleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ruleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            rulesPanel.add(ruleLabel);
        }

        // Start button panel
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(startButton);

        // Add all panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(new JScrollPane(rulesPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Add action listener to the start button.
     */
    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }
}