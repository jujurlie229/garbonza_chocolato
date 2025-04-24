package view;

import model.GameModel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionListener;

public class WelcomeScreen extends JPanel {
    private JButton startButton;

    public WelcomeScreen() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(
                GameModel.GRID_SIZE * GamePanel.getCellSize(),
                GameModel.GRID_SIZE * GamePanel.getCellSize()
        ));
        setBackground(Theme.NAVY);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Theme.NAVY);
        titlePanel.setBorder(new EmptyBorder(15, 0, 5, 0));

        JLabel titleLabel = new JLabel("Treasure Hunt");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26)); // Slightly smaller title
        titleLabel.setForeground(Theme.GOLD);
        titlePanel.add(titleLabel);

        JPanel rulesPanel = new JPanel();
        rulesPanel.setBackground(Theme.NAVY);
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.GOLD, 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                )
        ));

        // rules with styling - more compact format
        String[] rules = {
                "<html><h3 style='color:#DAA520;margin-bottom:5px;font-size:18px'>Game Rules</h3></html>",
                "<html><b style='color:#DAA520;'>Objective:</b> <span style='color:white;'>Find all treasures before running out of points</span></html>",
                "<html><b style='color:#DAA520;'>Controls:</b> <span style='color:white;'>Arrow keys to move</span> | <b style='color:#DAA520;'>Scoring:</b> <span style='color:white;'>Start: 100 pts</span></html>",
                "<html><span style='color:white;'>• Move: -1 pt • Hit obstacle: -10 pts • Hint: -3 pts</span></html>",

                "<html><b style='color:#DAA520;margin-top:5px;'>Map Elements:</b></html>",
                "<html><span style='color:#4682B4;'>■</span> <span style='color:white;'>Player</span></html>",
                "<html><span style='color:#192841;'>■</span> <span style='color:white;'>Obstacle</span></html>",
                "<html><span style='color:#DAA520;'>■</span> <span style='color:white;'>Treasure</span></html>",
                "<html><span style='color:#78C878;'>■</span> <span style='color:white;'>BFS hint</span></html>",
                "<html><span style='color:#6496FA;'>■</span> <span style='color:white;'>A* hint</span></html>",

                "<html><b style='color:#DAA520;margin-top:5px;'>Hint Types:</b></html>",
                "<html><span style='color:white;'>• BFS: Breadth-First Search - explores all directions equally</span></html>",
                "<html><span style='color:white;'>• A*: Uses Manhattan distance heuristic - more directed</span></html>",

                "<html><b style='color:#DAA520;margin-top:5px;'>Game End:</b> <span style='color:white;'>All treasures found or out of points</span></html>"
        };

        for (String rule : rules) {
            JLabel ruleLabel = new JLabel(rule);
            ruleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            ruleLabel.setForeground(Theme.TEXT_LIGHT);
            ruleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ruleLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            rulesPanel.add(ruleLabel);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Theme.NAVY);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        startButton.setBackground(Theme.LIGHT_NAVY);
        startButton.setForeground(Theme.GOLD);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setFocusPainted(false);
        startButton.setBorder(new CompoundBorder(
                new LineBorder(Theme.GOLD, 2),
                new EmptyBorder(6, 20, 6, 20)
        ));

        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(Theme.DARK_NAVY);
                startButton.setBorder(new CompoundBorder(
                        new LineBorder(Theme.SOFT_GOLD, 2),
                        new EmptyBorder(8, 25, 8, 25)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(Theme.LIGHT_NAVY);
                startButton.setBorder(new CompoundBorder(
                        new LineBorder(Theme.GOLD, 2),
                        new EmptyBorder(8, 25, 8, 25)
                ));
            }
        });

        buttonPanel.add(startButton);

        add(titlePanel, BorderLayout.NORTH);
        add(new JScrollPane(rulesPanel) {
            {
                setBorder(null);
                getViewport().setBackground(Theme.NAVY);
                getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                    @Override
                    protected void configureScrollBarColors() {
                        this.thumbColor = Theme.LIGHT_NAVY;
                        this.trackColor = Theme.NAVY;
                    }

                    @Override
                    protected JButton createDecreaseButton(int orientation) {
                        JButton button = super.createDecreaseButton(orientation);
                        button.setBackground(Theme.LIGHT_NAVY);
                        return button;
                    }

                    @Override
                    protected JButton createIncreaseButton(int orientation) {
                        JButton button = super.createIncreaseButton(orientation);
                        button.setBackground(Theme.LIGHT_NAVY);
                        return button;
                    }
                });
            }
        }, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }
}