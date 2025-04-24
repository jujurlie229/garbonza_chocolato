package view;

import model.Cell;
import model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * manages various animations in the game.
 */
public class AnimationManager {
    // animation timers
    private Timer playerMoveTimer;
    private Timer treasureCollectTimer;
    private Timer hintPathTimer;
    private Timer obstacleHitTimer;

    // animation properties
    private final float PLAYER_PULSE_MIN = 0.7f;
    private final float PLAYER_PULSE_MAX = 1.0f;
    private final int TREASURE_SPARKLE_COUNT = 5;
    private final int HINT_PATH_FRAMES = 10;

    // animation state
    private float playerPulseValue = PLAYER_PULSE_MAX;
    private boolean playerPulseIncreasing = false;
    private Map<Point, Integer> treasureSparkles = new HashMap<>();
    private boolean hintPathVisible = true;
    private int obstacleHitFrame = 0;
    private Point lastHitObstacle = null;

    private boolean isAStarPathActive = false;

    private Random random = new Random();

    /**
     * constructor initializes animation timers.
     */
    public AnimationManager() {
        // player pulse animation (continuous)
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

        hintPathTimer = new Timer(300, e -> {
            hintPathVisible = !hintPathVisible;
        });

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
     * starts the treasure collection animation at the specified location.
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
     * starts the hint path animation with specified algorithm.
     */
    public void startHintPathAnimation() {
        if (!hintPathTimer.isRunning()) {
            hintPathVisible = true;
            hintPathTimer.start();
        }
    }

    /**
     * starts the hint path animation with specified algorithm.
     */
    public void startHintPathAnimation(boolean isAStar) {
        isAStarPathActive = isAStar;
        startHintPathAnimation();
    }

    /**
     * stops the hint path animation.
     */
    public void stopHintPathAnimation() {
        hintPathTimer.stop();
        hintPathVisible = true;
    }

    /**
     * starts the obstacle hit animation.
     */
    public void startObstacleHitAnimation(int x, int y) {
        lastHitObstacle = new Point(x, y);
        obstacleHitFrame = 0;
        obstacleHitTimer.start();
    }

    /**
     * draws animations on the provided graphics context.
     */
    public void drawAnimations(Graphics2D g2d, GameModel model, int cellSize) {
        if (model != null) {
            for (int y = 0; y < GameModel.GRID_SIZE; y++) {
                for (int x = 0; x < GameModel.GRID_SIZE; x++) {
                    int cellX = x * cellSize;
                    int cellY = y * cellSize;
                    int margin = 1;

                    if (model.getCell(x, y) == Cell.PLAYER) {
                        // calculate pulse color based on the base player color
                        Color baseColor = Theme.PLAYER_COLOR;
                        float[] hsb = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);
                        Color pulseColor = Color.getHSBColor(hsb[0], hsb[1], playerPulseValue);

                        g2d.setColor(pulseColor);
                        g2d.fillRoundRect(
                                cellX + margin,
                                cellY + margin,
                                cellSize - (2 * margin),
                                cellSize - (2 * margin),
                                6, 6
                        );

                        g2d.setColor(Theme.TEXT_LIGHT);
                        int iconMargin = cellSize / 3;
                        g2d.fillOval(
                                cellX + iconMargin,
                                cellY + iconMargin,
                                cellSize - (2 * iconMargin),
                                cellSize - (2 * iconMargin)
                        );
                    }

                    if (model.getCell(x, y) == Cell.PATH_HINT) {
                        if (hintPathVisible) {
                            if (isAStarPathActive) {
                                g2d.setColor(Theme.PATH_HINT_ASTAR_COLOR);
                            } else {
                                g2d.setColor(Theme.PATH_HINT_BFS_COLOR);
                            }

                            g2d.fillRoundRect(
                                    cellX + margin,
                                    cellY + margin,
                                    cellSize - (2 * margin),
                                    cellSize - (2 * margin),
                                    4, 4
                            );

                            if (isAStarPathActive) {
                                g2d.setColor(new Color(50, 100, 200));
                            } else {
                                g2d.setColor(new Color(50, 150, 50));
                            }

                            int[] xPoints = {
                                    cellX + cellSize/5,
                                    cellX + cellSize*4/5,
                                    cellX + cellSize/2
                            };
                            int[] yPoints = {
                                    cellY + cellSize/2,
                                    cellY + cellSize/2,
                                    cellY + cellSize*3/4
                            };
                            g2d.fillPolygon(xPoints, yPoints, 3);

                        } else {
                            if (isAStarPathActive) {
                                g2d.setColor(new Color(180, 200, 255));
                            } else {
                                g2d.setColor(new Color(180, 255, 180));
                            }

                            g2d.fillRoundRect(
                                    cellX + margin,
                                    cellY + margin,
                                    cellSize - (2 * margin),
                                    cellSize - (2 * margin),
                                    4, 4
                            );
                        }
                    }

                    if (model.getCell(x, y) == Cell.TREASURE) {
                        g2d.setColor(Theme.TREASURE_COLOR);
                        g2d.fillRoundRect(
                                cellX + margin,
                                cellY + margin,
                                cellSize - (2 * margin),
                                cellSize - (2 * margin),
                                4, 4
                        );

                        int iconSize = cellSize / 2;
                        int iconX = cellX + (cellSize - iconSize) / 2;
                        int iconY = cellY + (cellSize - iconSize) / 2;

                        int[] xPoints = {
                                iconX + iconSize/2,
                                iconX + iconSize,
                                iconX + iconSize/2,
                                iconX
                        };
                        int[] yPoints = {
                                iconY,
                                iconY + iconSize/2,
                                iconY + iconSize,
                                iconY + iconSize/2
                        };

                        g2d.setColor(Theme.SOFT_GOLD);
                        g2d.fillPolygon(xPoints, yPoints, 4);

                        g2d.setColor(Color.WHITE);
                        g2d.drawLine(
                                iconX + iconSize/4,
                                iconY + iconSize/4,
                                iconX + iconSize/2,
                                iconY + iconSize/2
                        );

                        Point treasurePos = new Point(x, y);
                        if (treasureSparkles.containsKey(treasurePos)) {
                            int sparklesLeft = treasureSparkles.get(treasurePos);
                            if (sparklesLeft > 0) {
                                drawTreasureSparkles(g2d, cellX, cellY, cellSize, sparklesLeft);
                            }
                        }
                    }

                    if (lastHitObstacle != null &&
                            lastHitObstacle.x == x && lastHitObstacle.y == y &&
                            model.getCell(x, y) == Cell.OBSTACLE) {

                        int alpha = 255 - (obstacleHitFrame * 25);
                        if (alpha < 0) alpha = 0;

                        g2d.setColor(new Color(255, 70, 70, alpha));
                        g2d.fillRoundRect(
                                cellX + margin,
                                cellY + margin,
                                cellSize - (2 * margin),
                                cellSize - (2 * margin),
                                4, 4
                        );
                    }
                }
            }
        }
    }

    /**
     * draws sparkle effects for treasure collection animation.
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
     * sets the active hint path algorithm type.
     */
    public void setAStarPathActive(boolean isAStar) {
        this.isAStarPathActive = isAStar;
    }

    /**
     * simple Point class for animation tracking.
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
     * cleans up resources when no longer needed.
     */
    public void dispose() {
        if (playerMoveTimer != null) playerMoveTimer.stop();
        if (treasureCollectTimer != null) treasureCollectTimer.stop();
        if (hintPathTimer != null) hintPathTimer.stop();
        if (obstacleHitTimer != null) obstacleHitTimer.stop();
    }
}