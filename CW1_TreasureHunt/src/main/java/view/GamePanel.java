package view;

import model.Cell;
import model.GameModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that renders the game grid with animations.
 * Updated with navy and gold minimalist theme.
 */
public class GamePanel extends JPanel {
    private static final int CELL_SIZE = 30;
    private static final int CELL_MARGIN = 1; // Smaller margin for a more minimalist look
    private GameModel model;
    private AnimationManager animationManager;

    public GamePanel() {
        animationManager = new AnimationManager();

        // Set background color to main navy
        setBackground(Theme.NAVY);

        // Set up animation timer for constant repainting
        Timer repaintTimer = new Timer(50, e -> repaint());
        repaintTimer.start();
    }

    public void setModel(GameModel model) {
        this.model = model;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (model == null) return;

        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw the grid background
        for (int y = 0; y < GameModel.GRID_SIZE; y++) {
            for (int x = 0; x < GameModel.GRID_SIZE; x++) {
                int cellX = x * CELL_SIZE;
                int cellY = y * CELL_SIZE;

                // Set color based on cell type
                switch (model.getCell(x, y)) {
                    case EMPTY:
                        g2d.setColor(Theme.EMPTY_CELL_COLOR);
                        break;
                    case OBSTACLE:
                        g2d.setColor(Theme.OBSTACLE_COLOR);
                        break;
                    case PLAYER:
                        // Player is drawn in animation manager
                        continue;
                    case TREASURE:
                        // Treasure is drawn in animation manager
                        continue;
                    case PATH_HINT:
                        // Path hint is drawn in animation manager
                        continue;
                }

                // Fill the cell with margin for minimalist grid look
                g2d.fillRoundRect(
                        cellX + CELL_MARGIN,
                        cellY + CELL_MARGIN,
                        CELL_SIZE - (2 * CELL_MARGIN),
                        CELL_SIZE - (2 * CELL_MARGIN),
                        4, 4  // Slightly rounded corners for modern look
                );
            }
        }

        // Draw animated elements
        animationManager.drawAnimations(g2d, model, CELL_SIZE);

        // Draw subtle grid lines with a darker navy
        g2d.setColor(Theme.DARK_NAVY);
        g2d.setStroke(new BasicStroke(0.5f));  // Thinner lines for minimalist look
        for (int y = 0; y <= GameModel.GRID_SIZE; y++) {
            g2d.drawLine(0, y * CELL_SIZE, GameModel.GRID_SIZE * CELL_SIZE, y * CELL_SIZE);
        }
        for (int x = 0; x <= GameModel.GRID_SIZE; x++) {
            g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, GameModel.GRID_SIZE * CELL_SIZE);
        }

        g2d.dispose();
    }

    /**
     * Cleans up resources when the panel is no longer needed.
     */
    public void dispose() {
        if (animationManager != null) {
            animationManager.dispose();
        }
    }

    /**
     * Returns the cell size used for rendering.
     */
    public static int getCellSize() {
        return CELL_SIZE;
    }
}