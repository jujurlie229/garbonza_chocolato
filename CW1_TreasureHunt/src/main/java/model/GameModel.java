package model;

import java.util.*;
import java.util.List;

/**
 * The main model class for the Treasure Hunt game.
 * Contains all the game state and logic.
 */
public class GameModel {
    // Constants
    public static final int GRID_SIZE = 20;
    public static final int NUM_TREASURES = 3;
    public static final int MIN_OBSTACLES = 10;
    public static final int MAX_OBSTACLES = 100;
    public static final int INITIAL_SCORE = 100;

    // Game state
    private Cell[][] grid;
    private Point playerPosition;
    private int score;
    private int treasuresFound;
    private List<Point> treasureLocations;
    private List<Point> currentPath;

    // Direction vectors for movement and pathfinding
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // down, right, up, left

    /**
     * Constructor initializes the game state and generates the initial map.
     */
    public GameModel() {
        grid = new Cell[GRID_SIZE][GRID_SIZE];
        treasureLocations = new ArrayList<>();
        currentPath = new ArrayList<>();
        resetGame();
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        score = INITIAL_SCORE;
        treasuresFound = 0;
        generateMap();
    }

    /**
     * Generates a new random map with obstacles and treasures.
     */
    public void generateMap() {
        // Initialize map with empty cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = Cell.EMPTY;
            }
        }

        // Place player in the center
        playerPosition = new Point(GRID_SIZE / 2, GRID_SIZE / 2);
        grid[playerPosition.getY()][playerPosition.getX()] = Cell.PLAYER;

        // Place random obstacles
        Random rand = new Random();
        int numObstacles = rand.nextInt(MAX_OBSTACLES - MIN_OBSTACLES + 1) + MIN_OBSTACLES;

        for (int i = 0; i < numObstacles; i++) {
            int x = rand.nextInt(GRID_SIZE);
            int y = rand.nextInt(GRID_SIZE);

            // Don't place obstacle on player or existing obstacle
            if ((x != playerPosition.getX() || y != playerPosition.getY()) && grid[y][x] == Cell.EMPTY) {
                grid[y][x] = Cell.OBSTACLE;
            }
        }

        // Place treasures
        placeTreasures();
    }

    /**
     * Places treasures on the map, ensuring they are reachable from the player's position.
     */
    private void placeTreasures() {
        treasureLocations.clear();
        Random rand = new Random();

        for (int i = 0; i < NUM_TREASURES; i++) {
            boolean validPosition = false;
            int x = 0, y = 0;

            while (!validPosition) {
                x = rand.nextInt(GRID_SIZE);
                y = rand.nextInt(GRID_SIZE);

                // Check if position is empty and not player position
                if (grid[y][x] == Cell.EMPTY &&
                        (x != playerPosition.getX() || y != playerPosition.getY())) {

                    // Check if treasure is reachable from player's position
                    if (isReachable(playerPosition, new Point(x, y))) {
                        validPosition = true;
                    }
                }
            }

            grid[y][x] = Cell.TREASURE;
            treasureLocations.add(new Point(x, y));
        }
    }

    /**
     * Checks if a position is valid (within grid bounds).
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

    /**
     * Checks if there is a path from start to end point.
     */
    public boolean isReachable(Point start, Point end) {
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
        Queue<Point> queue = new LinkedList<>();

        queue.add(start);
        visited[start.getY()][start.getX()] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.getX() == end.getX() && current.getY() == end.getY()) {
                return true;
            }

            for (int[] dir : DIRECTIONS) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                if (isValidPosition(newX, newY) && !visited[newY][newX] &&
                        grid[newY][newX] != Cell.OBSTACLE) {
                    queue.add(new Point(newX, newY));
                    visited[newY][newX] = true;
                }
            }
        }

        return false;
    }

    /**
     * Attempts to move the player in the specified direction.
     * Returns true if the move resulted in finding a treasure.
     */
    public boolean movePlayer(Direction direction) {
        int newX = playerPosition.getX();
        int newY = playerPosition.getY();

        // Clear path hints
        clearPathHints();

        // Calculate new position based on direction
        switch (direction) {
            case UP:
                newY--;
                break;
            case DOWN:
                newY++;
                break;
            case LEFT:
                newX--;
                break;
            case RIGHT:
                newX++;
                break;
        }

        boolean foundTreasure = false;

        // Check if the move is valid
        if (isValidPosition(newX, newY)) {
            if (grid[newY][newX] == Cell.OBSTACLE) {
                // Player hit a wall
                score -= 10;
            } else {
                // Update player position
                grid[playerPosition.getY()][playerPosition.getX()] = Cell.EMPTY;

                if (grid[newY][newX] == Cell.TREASURE) {
                    // Player found a treasure
                    treasuresFound++;
                    int finalNewX = newX;
                    int finalNewY = newY;
                    treasureLocations.removeIf(p -> p.getX() == finalNewX && p.getY() == finalNewY);
                    foundTreasure = true;
                }

                playerPosition = new Point(newX, newY);
                grid[newY][newX] = Cell.PLAYER;

                // Moving costs 1 score point
                score -= 1;
            }
        }

        return foundTreasure;
    }

    /**
     * Clears any path hints from the map.
     */
    public void clearPathHints() {
        for (Point p : currentPath) {
            if (grid[p.getY()][p.getX()] == Cell.PATH_HINT) {
                grid[p.getY()][p.getX()] = Cell.EMPTY;
            }
        }
        currentPath.clear();
    }

    /**
     * Finds and displays the shortest path to the nearest treasure.
     * Returns true if a path was found.
     */
    public boolean showHint() {
        // Clear previous hints
        clearPathHints();

        if (treasureLocations.isEmpty()) {
            return false;
        }

        // Find closest treasure and shortest path
        Point closestTreasure = null;
        List<Point> shortestPath = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (Point treasure : treasureLocations) {
            List<Point> path = findShortestPath(playerPosition, treasure);
            if (path != null && path.size() < shortestDistance) {
                shortestDistance = path.size();
                shortestPath = path;
                closestTreasure = treasure;
            }
        }

        // Mark the path on the map
        if (shortestPath != null) {
            for (Point p : shortestPath) {
                if (grid[p.getY()][p.getX()] == Cell.EMPTY) {
                    grid[p.getY()][p.getX()] = Cell.PATH_HINT;
                    currentPath.add(p);
                }
            }

            // Using hint costs 3 points
            score -= 3;
            return true;
        }

        return false;
    }

    /**
     * Finds the shortest path between two points using BFS.
     */
    private List<Point> findShortestPath(Point start, Point end) {
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>();

        queue.add(start);
        visited[start.getY()][start.getX()] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.getX() == end.getX() && current.getY() == end.getY()) {
                // Path found, reconstruct it
                return reconstructPath(parentMap, start, end);
            }

            for (int[] dir : DIRECTIONS) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                if (isValidPosition(newX, newY) && !visited[newY][newX] &&
                        (grid[newY][newX] == Cell.EMPTY || grid[newY][newX] == Cell.TREASURE)) {
                    Point next = new Point(newX, newY);
                    queue.add(next);
                    visited[newY][newX] = true;
                    parentMap.put(next, current);
                }
            }
        }

        return null; // No path found
    }

    /**
     * Reconstructs a path from the parent map created during pathfinding.
     */
    private List<Point> reconstructPath(Map<Point, Point> parentMap, Point start, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = end;

        while (!current.equals(start)) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    // Getters and setters

    public Cell getCell(int x, int y) {
        return grid[y][x];
    }

    public int getScore() {
        return score;
    }

    public int getTreasuresFound() {
        return treasuresFound;
    }

    public int getTreasuresTotal() {
        return NUM_TREASURES;
    }

    public boolean isGameOver() {
        return treasuresFound == NUM_TREASURES || score <= 0;
    }

    public boolean allTreasuresFound() {
        return treasuresFound == NUM_TREASURES;
    }

    public Point getPlayerPosition() {
        return playerPosition;
    }
}
