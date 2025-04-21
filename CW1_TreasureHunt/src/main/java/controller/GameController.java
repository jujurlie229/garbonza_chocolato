package controller;

import model.Direction;
import model.GameModel;
import view.AnimationManager;
import view.GameView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController {
    private GameModel model;
    private GameView view;
    private AnimationManager animationManager;
    private boolean isGameInProgress = false;

    /**
     * Constructor initializes the game components and sets up event listeners.
     */
    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        // Get animation manager from view
        this.animationManager = view.getAnimationManager();

        // Set up welcome screen start button listener
        view.addStartButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // Set up key listener for player movement
        view.addKeyboardListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isGameInProgress) {
                    handleKeyPress(e.getKeyCode());
                }
            }
        });

        // Set up BFS hint button listener
        view.addHintBFSButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameInProgress) {
                    handleHintBFSRequest();
                }
            }
        });

        // Set up A* hint button listener
        view.addHintAStarButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameInProgress) {
                    handleHintAStarRequest();
                }
            }
        });

        // Set up reset button listener
        view.addResetButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
    }

    /**
     * Starts the game by showing the game screen and initializing the game state.
     */
    private void startGame() {
        // Show game screen
        view.showGameScreen();

        // Reset game state
        resetGame();

        // Set game in progress
        isGameInProgress = true;
    }

    /**
     * Handles keyboard input for player movement.
     */
    private void handleKeyPress(int keyCode) {
        boolean foundTreasure = false;
        int oldX = model.getPlayerPosition().getX();
        int oldY = model.getPlayerPosition().getY();

        // Clear path hints when moving
        animationManager.stopHintPathAnimation();

        // Translate key press to direction and move player
        switch (keyCode) {
            case KeyEvent.VK_UP:
                foundTreasure = model.movePlayer(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                foundTreasure = model.movePlayer(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                foundTreasure = model.movePlayer(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                foundTreasure = model.movePlayer(Direction.RIGHT);
                break;
            default:
                return; // Ignore other keys
        }

        // Get new position
        int newX = model.getPlayerPosition().getX();
        int newY = model.getPlayerPosition().getY();

        // Check if player hit an obstacle (position didn't change but score decreased)
        if (oldX == newX && oldY == newY && model.getScore() < 100) {
            // Calculate where the obstacle would be based on attempted direction
            int obstacleX = oldX;
            int obstacleY = oldY;

            switch (keyCode) {
                case KeyEvent.VK_UP:
                    obstacleY--;
                    break;
                case KeyEvent.VK_DOWN:
                    obstacleY++;
                    break;
                case KeyEvent.VK_LEFT:
                    obstacleX--;
                    break;
                case KeyEvent.VK_RIGHT:
                    obstacleX++;
                    break;
            }

            // Start obstacle hit animation if the position is valid
            if (model.isValidPosition(obstacleX, obstacleY)) {
                animationManager.startObstacleHitAnimation(obstacleX, obstacleY);
            }
        }

        // If treasure was found, start collection animation
        if (foundTreasure) {
            animationManager.startTreasureCollectAnimation(newX, newY);
        }

        // Update the view
        updateView();

        // Check game over conditions
        checkGameStatus();
    }

    /**
     * Handles the BFS hint button press.
     */
    private void handleHintBFSRequest() {
        // Only show hint if we have enough score and treasures remain
        if (model.getScore() >= 3 && !model.allTreasuresFound()) {
            // Clear previous hints first to ensure new path is shown
            model.clearPathHints();
            animationManager.stopHintPathAnimation();

            // Set the animation manager to display BFS path type
            animationManager.setAStarPathActive(false);

            boolean pathFound = model.showHintBFS();

            if (pathFound) {
                // Start the path animation with BFS style
                animationManager.startHintPathAnimation(false);

                // Update statistics display
                view.updateStatistics(true, model.getBFSCellsExplored(), model.getLastPathLength());
            }

            updateView();

            // Check if we've run out of points after using hint
            if (model.getScore() <= 0) {
                isGameInProgress = false;
                view.showGameOverMessage(false, model.getScore());
                resetGame();
            }
        }
    }

    /**
     * Handles the A* hint button press.
     */
    private void handleHintAStarRequest() {
        // Only show hint if we have enough score and treasures remain
        if (model.getScore() >= 3 && !model.allTreasuresFound()) {
            // Clear previous hints first to ensure new path is shown
            model.clearPathHints();
            animationManager.stopHintPathAnimation();

            // Set the animation manager to display A* path type
            animationManager.setAStarPathActive(true);

            boolean pathFound = model.showHintAStar();

            if (pathFound) {
                // Start the path animation with A* style
                animationManager.startHintPathAnimation(true);

                // Update statistics display
                view.updateStatistics(false, model.getAStarCellsExplored(), model.getLastPathLength());
            }

            updateView();

            // Check if we've run out of points after using hint
            if (model.getScore() <= 0) {
                isGameInProgress = false;
                view.showGameOverMessage(false, model.getScore());
                resetGame();
            }
        }
    }

    /**
     * Legacy method to maintain backward compatibility.
     */
    private void handleHintRequest() {
        handleHintBFSRequest();
    }

    /**
     * Checks if the game is over and shows appropriate message.
     */
    private void checkGameStatus() {
        if (model.isGameOver()) {
            isGameInProgress = false;
            boolean won = model.allTreasuresFound();
            view.showGameOverMessage(won, model.getScore());
            resetGame();
        }
    }

    /**
     * Resets the game to initial state.
     */
    private void resetGame() {
        // Stop any ongoing animations
        animationManager.stopHintPathAnimation();

        // Reset the model
        model.resetGame();

        // Update the view
        updateView();

        // Clear statistics
        view.updateStatistics(true, 0, 0);

        // Game is now in progress
        isGameInProgress = true;
    }

    /**
     * Updates the view to reflect the current model state.
     */
    private void updateView() {
        view.updateView(model);
    }
}