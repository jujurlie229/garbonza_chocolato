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
     * constructor initializes the game components and sets up event listeners.
     */
    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        view.setController(this);
        this.animationManager = view.getAnimationManager();

        // welcome screen set up
        view.addStartButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // player movement set up
        view.addKeyboardListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isGameInProgress) {
                    handleKeyPress(e.getKeyCode());
                }
            }
        });

        // BFS hint set up
        view.addHintBFSButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameInProgress) {
                    handleHintBFSRequest();
                }
            }
        });

        // A* hint button set up
        view.addHintAStarButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameInProgress) {
                    handleHintAStarRequest();
                }
            }
        });

        // reset button set up
        view.addResetButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
    }

    /**
     * start the game by showing the game screen and initializing the game state.
     */
    private void startGame() {
        // Show game screen
        view.showGameScreen();
        resetGame();
        isGameInProgress = true;
    }

    /**
     * handles keyboard input for player movement.
     */
    private void handleKeyPress(int keyCode) {
        // ignore keypresses if game is not in progress
        if (!isGameInProgress) {
            return;
        }

        boolean foundTreasure = false;
        int oldX = model.getPlayerPosition().getX();
        int oldY = model.getPlayerPosition().getY();

        animationManager.stopHintPathAnimation();

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
                return;
        }

        int newX = model.getPlayerPosition().getX();
        int newY = model.getPlayerPosition().getY();

        if (oldX == newX && oldY == newY && model.getScore() < 100) {
            // calculating where the obstacle would be based on attempted direction
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

            if (model.isValidPosition(obstacleX, obstacleY)) {
                animationManager.startObstacleHitAnimation(obstacleX, obstacleY);
            }
        }

        // If treasure was found, start animation and show popup
        if (foundTreasure) {
            animationManager.startTreasureCollectAnimation(newX, newY);
            updateView();
            isGameInProgress = false;

            view.showTreasureFoundMessage(model.getTreasuresFound(), model.getTreasuresTotal());

            return;
        }

        updateView();
        checkGameStatus();
    }

    /**
     * handles the BFS hint button press.
     */
    private void handleHintBFSRequest() {
        // only showing the hint if we have enough score and treasures remain
        if (model.getScore() >= 3 && !model.allTreasuresFound()) {
            model.clearPathHints();
            animationManager.stopHintPathAnimation();

            animationManager.setAStarPathActive(false);

            boolean pathFound = model.showHintBFS();

            if (pathFound) {
                animationManager.startHintPathAnimation(false);
                view.updateStatistics(true, model.getBFSCellsExplored(), model.getLastPathLength());
            }

            updateView();

            if (model.getScore() <= 0) {
                isGameInProgress = false;
                view.showGameOverMessage(false, model.getScore());
                resetGame();
            }
        }
    }

    /**
     * handles the A* hint button press.
     */
    private void handleHintAStarRequest() {
        // only showing the hint if we have enough score and treasures remain
        if (model.getScore() >= 3 && !model.allTreasuresFound()) {
            model.clearPathHints();
            animationManager.stopHintPathAnimation();

            animationManager.setAStarPathActive(true);

            boolean pathFound = model.showHintAStar();

            if (pathFound) {
                animationManager.startHintPathAnimation(true);
                view.updateStatistics(false, model.getAStarCellsExplored(), model.getLastPathLength());
            }

            updateView();

            // check if player has run out of points
            if (model.getScore() <= 0) {
                isGameInProgress = false;
                view.showGameOverMessage(false, model.getScore());
                resetGame();
            }
        }
    }

    /**
     * check if the game is over and shows appropriate message.
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
     * reset to the initial state.
     */
    private void resetGame() {
        animationManager.stopHintPathAnimation();
        model.resetGame();
        updateView();
        view.updateStatistics(true, 0, 0);
        isGameInProgress = true;
    }

    /**
     * updates the view to reflect the current model state.
     */
    private void updateView() {
        view.updateView(model);
    }

    /**
     * resumes the game after it was paused (e.g., after a popup dialog)
     */
    public void resumeGame() {
        isGameInProgress = true;

        updateView();
        checkGameStatus();
    }
}