package view;

import model.Cell;
import model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages various animations in the game.
 */
public class AnimationManager {
    // Animation timers
    private Timer playerMoveTimer;
    private Timer treasureCollectTimer;
    private Timer hintPathTimer;
    private Timer obstacleHitTimer;

    // Animation properties
    private final float PLAYER_PULSE_MIN = 0.7f;
    private final float PLAYER_PULSE_MAX = 1.0f;
    private final int TREASURE_SPARKLE_COUNT = 5;
    private final int HINT_PATH_FRAMES = 10;

    // Animation state
    private float playerPulseValue = PLAYER_PULSE_MAX;
    private boolean playerPulseIncreasing = false;
    private Map<Point, Integer> treasureSparkles = new HashMap<>();
    private boolean hintPathVisible = true;
    private int obstacleHitFrame = 0;
    private Point lastHitObstacle = null;

    // Random generator for effects
    private Random random = new Random();

    /**
     * Constructor initializes animation timers.
     */
    public AnimationManager() {
        // Player pulse animation (continuous)
        playerMoveTimer = new Timer(100, e -> {
            if (playerPulseIncreasing) {
                playerPulseValue += 0.05f;
                if (playerPulseValue >= PLAYER_PULSE_MAX) {
                    playerPulseValue = PLAYER_PULSE_MAX;
                    playerPulseIncreasing = false;
                }
            } else {
                playerPulseValue -= 0.05f;
                if (playerPulseValue <= PLAYER_PULSE_MIN) {
                    playerPulseValue = PLAYER_PULSE_MIN;
                    playerPulseIncreasing = true;
                }
            }
        });
        playerMoveTimer.start();

        // Hint path animation
        hintPathTimer = new Timer(300, e -> {
            hintPathVisible = !hintPathVisible;
        });

        // Obstacle hit animation
        obstacleHitTimer = new Timer(50, e -> {
            obstacleHitFrame++;
            if (obstacleHitFrame > 10) {
                obstacleHitTimer.stop();
                obstacleHitFrame = 0;
                lastHitObstacle = null;
            }
        });
    }

    /**
     * Starts the treasure collection animation at the specified location.
     */
    public void startTreasureCollectAnimation(int x, int y) {
        Point treasurePos = new Point(x, y);
        treasureSparkles.put(treasurePos, TREASURE_SPARKLE_COUNT);

        if (treasureCollectTimer != null && treasureCollectTimer.isRunning()) {
            treasureCollectTimer.stop();
        }

        treasureCollectTimer = new Timer(150, e -> {
            boolean allAnimationsComplete = true;

            for (Point p : treasureSparkles.keySet()) {
                int framesLeft = treasureSparkles.get(p);
                if (framesLeft > 0) {
                    treasureSparkles.put(p, framesLeft - 1);
                    allAnimationsComplete = false;
                }
            }

            if (allAnimationsComplete) {
                treasureCollectTimer.stop();
                treasureSparkles.clear();
            }
        });

        treasureCollectTimer.start();
    }

    /**
     * Starts the hint path animation.
     */
    public void startHintPathAnimation() {
        if (!hintPathTimer.isRunning()) {
            hintPathVisible = true;
            hintPathTimer.start();
        }
    }

    /**
     * Stops the hint path animation.
     */
    public void stopHintPathAnimation() {
        hintPathTimer.stop();
        hintPathVisible = true;
    }

    /**
     * Starts the obstacle hit animation.
     */
    public void startObstacleHitAnimation(int x, int y) {
        lastHitObstacle = new Point(x, y);
        obstacleHitFrame = 0;
        obstacleHitTimer.start();
    }

    /**
     * Draws animations on the provided graphics context.
     */
    public void drawAnimations(Graphics2D g2d, GameModel model, int cellSize) {
        // Draw player pulse effect
        if (model != null) {
            for (int y = 0; y < GameModel.GRID_SIZE; y++) {
                for (int x = 0; x < GameModel.GRID_SIZE; x++) {
                    int cellX = x * cellSize;
                    int cellY = y * cellSize;

                    // Draw player with pulse effect
                    if (model.getCell(x, y) == Cell.PLAYER) {
                        float[] hsb = Color.RGBtoHSB(0, 0, 255, null);
                        Color pulseColor = Color.getHSBColor(hsb[0], hsb[1], playerPulseValue);
                        g2d.setColor(pulseColor);
                        g2d.fillRect(cellX, cellY, cellSize, cellSize);

                        // Draw player icon
                        g2d.setColor(Color.WHITE);
                        int margin = cellSize / 4;
                        g2d.fillOval(cellX + margin, cellY + margin, cellSize - 2*margin, cellSize - 2*margin);
                    }

                    // Draw path hint animation (if active)
                    if (model.getCell(x, y) == Cell.PATH_HINT) {
                        if (hintPathVisible) {
                            g2d.setColor(Color.GREEN);
                            g2d.fillRect(cellX, cellY, cellSize, cellSize);

                            // Draw arrow pattern
                            g2d.setColor(new Color(0, 100, 0));
                            g2d.drawLine(cellX + cellSize/4, cellY + cellSize/2,
                                    cellX + 3*cellSize/4, cellY + cellSize/2);
                            g2d.drawLine(cellX + 3*cellSize/4, cellY + cellSize/2,
                                    cellX + 2*cellSize/3, cellY + cellSize/3);
                            g2d.drawLine(cellX + 3*cellSize/4, cellY + cellSize/2,
                                    cellX + 2*cellSize/3, cellY + 2*cellSize/3);
                        } else {
                            g2d.setColor(new Color(200, 255, 200));
                            g2d.fillRect(cellX, cellY, cellSize, cellSize);
                        }
                    }

                    // Draw treasure with sparkle effect
                    if (model.getCell(x, y) == Cell.TREASURE) {
                        // Base treasure
                        g2d.setColor(Color.YELLOW);
                        g2d.fillRect(cellX, cellY, cellSize, cellSize);

                        // Treasure chest icon
                        g2d.setColor(new Color(139, 69, 19)); // Brown
                        g2d.fillRect(cellX + cellSize/4, cellY + cellSize/3,
                                cellSize/2, cellSize/2);
                        g2d.setColor(new Color(255, 215, 0)); // Gold
                        g2d.fillRect(cellX + cellSize/4, cellY + cellSize/3,
                                cellSize/2, cellSize/4);

                        // Add sparkles
                        Point treasurePos = new Point(x, y);
                        if (treasureSparkles.containsKey(treasurePos)) {
                            int sparklesLeft = treasureSparkles.get(treasurePos);
                            if (sparklesLeft > 0) {
                                drawTreasureSparkles(g2d, cellX, cellY, cellSize, sparklesLeft);
                            }
                        }
                    }

                    // Draw obstacle hit animation
                    if (lastHitObstacle != null &&
                            lastHitObstacle.x == x && lastHitObstacle.y == y &&
                            model.getCell(x, y) == Cell.OBSTACLE) {

                        int alpha = 255 - (obstacleHitFrame * 25);
                        if (alpha < 0) alpha = 0;

                        g2d.setColor(new Color(255, 0, 0, alpha));
                        g2d.fillRect(cellX, cellY, cellSize, cellSize);
                    }
                }
            }
        }
    }

    /**
     * Draws sparkle effects for treasure collection animation.
     */
    private void drawTreasureSparkles(Graphics2D g2d, int cellX, int cellY, int cellSize, int frame) {
        g2d.setColor(Color.WHITE);

        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4.0;
            int sparkleRadius = (TREASURE_SPARKLE_COUNT - frame + 1) * cellSize / TREASURE_SPARKLE_COUNT;

            int sparkleX = cellX + cellSize/2 + (int)(Math.cos(angle) * sparkleRadius);
            int sparkleY = cellY + cellSize/2 + (int)(Math.sin(angle) * sparkleRadius);

            int sparkleSize = Math.max(2, cellSize / 10);
            g2d.fillOval(sparkleX - sparkleSize/2, sparkleY - sparkleSize/2, sparkleSize, sparkleSize);
        }
    }

    /**
     * Simple Point class for animation tracking.
     */
    private class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point) {
                Point other = (Point) obj;
                return this.x == other.x && this.y == other.y;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    /**
     * Clean up resources when no longer needed.
     */
    public void dispose() {
        if (playerMoveTimer != null) playerMoveTimer.stop();
        if (treasureCollectTimer != null) treasureCollectTimer.stop();
        if (hintPathTimer != null) hintPathTimer.stop();
        if (obstacleHitTimer != null) obstacleHitTimer.stop();
    }
}