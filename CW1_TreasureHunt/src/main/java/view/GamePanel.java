package view;

import model.Cell;
import model.GameModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that renders the game grid with animations.
 */
public class GamePanel extends JPanel {
    private static final int CELL_SIZE = 30;
    private GameModel model;
    private AnimationManager animationManager;

    public GamePanel() {
        animationManager = new AnimationManager();

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

        // Draw the grid background
        for (int y = 0; y < GameModel.GRID_SIZE; y++) {
            for (int x = 0; x < GameModel.GRID_SIZE; x++) {
                int cellX = x * CELL_SIZE;
                int cellY = y * CELL_SIZE;

                // Set color based on cell type
                switch (model.getCell(x, y)) {
                    case EMPTY:
                        g2d.setColor(Color.WHITE);
                        break;
                    case OBSTACLE:
                        g2d.setColor(Color.BLACK);
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

                // Fill the cell
                g2d.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw animated elements
        animationManager.drawAnimations(g2d, model, CELL_SIZE);

        // Draw grid lines
        g2d.setColor(Color.GRAY);
        for (int y = 0; y < GameModel.GRID_SIZE; y++) {
            for (int x = 0; x < GameModel.GRID_SIZE; x++) {
                int cellX = x * CELL_SIZE;
                int cellY = y * CELL_SIZE;
                g2d.drawRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
            }
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