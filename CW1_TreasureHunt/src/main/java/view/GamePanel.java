package view;

import model.GameModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that renders the game grid with animations.
 */
public class GamePanel extends JPanel {
    private static final int CELL_SIZE = 30;
    private static final int CELL_MARGIN = 1;
    private GameModel model;
    private AnimationManager animationManager;

    public GamePanel() {
        animationManager = new AnimationManager();

        setBackground(Theme.NAVY);

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

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int y = 0; y < GameModel.GRID_SIZE; y++) {
            for (int x = 0; x < GameModel.GRID_SIZE; x++) {
                int cellX = x * CELL_SIZE;
                int cellY = y * CELL_SIZE;

                switch (model.getCell(x, y)) {
                    case EMPTY:
                        g2d.setColor(Theme.EMPTY_CELL_COLOR);
                        break;
                    case OBSTACLE:
                        g2d.setColor(Theme.OBSTACLE_COLOR);
                        break;
                    case PLAYER:
                        continue;
                    case TREASURE:
                        continue;
                    case PATH_HINT:
                        continue;
                }

                g2d.fillRoundRect(
                        cellX + CELL_MARGIN,
                        cellY + CELL_MARGIN,
                        CELL_SIZE - (2 * CELL_MARGIN),
                        CELL_SIZE - (2 * CELL_MARGIN),
                        4, 4
                );
            }
        }

        animationManager.drawAnimations(g2d, model, CELL_SIZE);

        g2d.setColor(Theme.DARK_NAVY);
        g2d.setStroke(new BasicStroke(0.5f));
        for (int y = 0; y <= GameModel.GRID_SIZE; y++) {
            g2d.drawLine(0, y * CELL_SIZE, GameModel.GRID_SIZE * CELL_SIZE, y * CELL_SIZE);
        }
        for (int x = 0; x <= GameModel.GRID_SIZE; x++) {
            g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, GameModel.GRID_SIZE * CELL_SIZE);
        }

        g2d.dispose();
    }

    /**
     * cleans up resources when the panel is no longer needed.
     */
    public void dispose() {
        if (animationManager != null) {
            animationManager.dispose();
        }
    }

    /**
     * returns the cell size used for rendering.
     */
    public static int getCellSize() {
        return CELL_SIZE;
    }
}