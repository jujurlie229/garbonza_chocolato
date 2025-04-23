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
    private Cell[][] visibleGrid; // What the player can see
    private Point playerPosition;
    private int score;
    private int treasuresFound;
    private List<Point> treasureLocations;
    private List<Point> currentPath;
    private boolean hintUsedSinceLastMove; // Tracks if hint was already used before moving
    private List<Point> revealedObstacles; // Track obstacles that have been revealed

    // Search algorithm statistics
    private int bfsCellsExplored;
    private int aStarCellsExplored;
    private int lastPathLength;

    // Direction vectors for movement and pathfinding
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // down, right, up, left

    /**
     * Constructor initializes the game state and generates the initial map.
     */
    public GameModel() {
        grid = new Cell[GRID_SIZE][GRID_SIZE];
        visibleGrid = new Cell[GRID_SIZE][GRID_SIZE];
        treasureLocations = new ArrayList<>();
        currentPath = new ArrayList<>();
        revealedObstacles = new ArrayList<>();
        hintUsedSinceLastMove = false;
        resetGame();
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        score = INITIAL_SCORE;
        treasuresFound = 0;
        hintUsedSinceLastMove = false;
        bfsCellsExplored = 0;
        aStarCellsExplored = 0;
        lastPathLength = 0;
        revealedObstacles.clear();
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
                visibleGrid[i][j] = Cell.EMPTY; // All cells start as empty in visible grid
            }
        }

        // Place player in the center
        playerPosition = new Point(GRID_SIZE / 2, GRID_SIZE / 2);
        grid[playerPosition.getY()][playerPosition.getX()] = Cell.PLAYER;
        visibleGrid[playerPosition.getY()][playerPosition.getX()] = Cell.PLAYER;

        // Place random obstacles
        Random rand = new Random();
        int numObstacles = rand.nextInt(MAX_OBSTACLES - MIN_OBSTACLES + 1) + MIN_OBSTACLES;

        for (int i = 0; i < numObstacles; i++) {
            int x = rand.nextInt(GRID_SIZE);
            int y = rand.nextInt(GRID_SIZE);

            // Don't place obstacle on player or existing obstacle
            if ((x != playerPosition.getX() || y != playerPosition.getY()) && grid[y][x] == Cell.EMPTY) {
                grid[y][x] = Cell.OBSTACLE;
                // Don't add to visible grid - obstacles are hidden initially
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
            // Don't add to visible grid - treasures are hidden initially
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

        // Reset hint used flag when player moves
        hintUsedSinceLastMove = false;

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
        boolean hitObstacle = false;
        Point obstaclePoint = null;

        // Check if the move is valid
        if (isValidPosition(newX, newY)) {
            if (grid[newY][newX] == Cell.OBSTACLE) {
                // Player hit a wall
                score -= 10;
                hitObstacle = true;
                obstaclePoint = new Point(newX, newY);

                // Reveal the obstacle in the visible grid
                visibleGrid[newY][newX] = Cell.OBSTACLE;
                revealedObstacles.add(obstaclePoint);
            } else {
                // Update player position
                grid[playerPosition.getY()][playerPosition.getX()] = Cell.EMPTY;
                visibleGrid[playerPosition.getY()][playerPosition.getX()] = Cell.EMPTY;

                if (grid[newY][newX] == Cell.TREASURE) {
                    // Player found a treasure
                    treasuresFound++;
                    int finalNewX = newX;
                    int finalNewY = newY;
                    treasureLocations.removeIf(p -> p.getX() == finalNewX && p.getY() == finalNewY);
                    foundTreasure = true;

                    // Make the treasure visible in the visible grid before changing to player
                    visibleGrid[newY][newX] = Cell.TREASURE;
                }

                playerPosition = new Point(newX, newY);
                grid[newY][newX] = Cell.PLAYER;
                visibleGrid[newY][newX] = Cell.PLAYER;

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
            if (visibleGrid[p.getY()][p.getX()] == Cell.PATH_HINT) {
                visibleGrid[p.getY()][p.getX()] = Cell.EMPTY;
            }
        }
        currentPath.clear();
    }

    /**
     * Shows only the next step towards the nearest treasure using BFS.
     * Returns true if a path was found.
     */
    public boolean showHintBFS() {
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

        // Mark only the first step of the path on the map
        if (shortestPath != null && !shortestPath.isEmpty()) {
            Point nextStep = shortestPath.get(0);

            // Only show the hint if the cell is not an obstacle or already revealed
            if (grid[nextStep.getY()][nextStep.getX()] != Cell.OBSTACLE ||
                    visibleGrid[nextStep.getY()][nextStep.getX()] == Cell.OBSTACLE) {

                visibleGrid[nextStep.getY()][nextStep.getX()] = Cell.PATH_HINT;
                currentPath.add(nextStep);
            }

            // Store the full path length for statistics
            lastPathLength = shortestPath.size();

            // Using hint costs 3 points - but only charge once if player is comparing algorithms
            if (!hintUsedSinceLastMove) {
                score -= 3;
                hintUsedSinceLastMove = true;
            }

            return true;
        }

        return false;
    }

    /**
     * Shows only the next step towards the nearest treasure using A* search.
     * Returns true if a path was found.
     */
    public boolean showHintAStar() {
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
            List<Point> path = findPathAStar(playerPosition, treasure);
            if (path != null && path.size() < shortestDistance) {
                shortestDistance = path.size();
                shortestPath = path;
                closestTreasure = treasure;
            }
        }

        // Mark only the first step of the path on the map
        if (shortestPath != null && !shortestPath.isEmpty()) {
            Point nextStep = shortestPath.get(0);

            // Only show the hint if the cell is not an obstacle or already revealed
            if (grid[nextStep.getY()][nextStep.getX()] != Cell.OBSTACLE ||
                    visibleGrid[nextStep.getY()][nextStep.getX()] == Cell.OBSTACLE) {

                visibleGrid[nextStep.getY()][nextStep.getX()] = Cell.PATH_HINT;
                currentPath.add(nextStep);
            }

            // Store the full path length for statistics
            lastPathLength = shortestPath.size();

            // Using hint costs 3 points - but only charge once if player is comparing algorithms
            if (!hintUsedSinceLastMove) {
                score -= 3;
                hintUsedSinceLastMove = true;
            }

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

        // Reset cells explored counter
        bfsCellsExplored = 0;

        queue.add(start);
        visited[start.getY()][start.getX()] = true;
        bfsCellsExplored++; // Count starting cell

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.getX() == end.getX() && current.getY() == end.getY()) {
                // Path found, reconstruct it
                List<Point> path = reconstructPath(parentMap, start, end);
                lastPathLength = path.size();
                return path;
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
                    bfsCellsExplored++; // Count each explored cell
                }
            }
        }

        return null; // No path found
    }

    /**
     * Calculates the Manhattan distance between two points.
     */
    private int calculateManhattanDistance(Point a, Point b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * Finds the path between two points using A* search algorithm.
     */
    private List<Point> findPathAStar(Point start, Point end) {
        // Reset cells explored counter
        aStarCellsExplored = 0;

        // Priority queue with custom comparator for f-score
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(
                Comparator.comparingInt(node -> node.fScore)
        );

        // Track visited nodes
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];

        // For node n, gScore[n] is the cost of the cheapest path from start to n
        Map<Point, Integer> gScore = new HashMap<>();

        // For node n, parent[n] is the node immediately preceding it on the cheapest path
        Map<Point, Point> parentMap = new HashMap<>();

        // Add start node to open set
        gScore.put(start, 0);
        int startFScore = calculateManhattanDistance(start, end);
        openSet.add(new AStarNode(start, startFScore));
        aStarCellsExplored++; // Count starting node

        while (!openSet.isEmpty()) {
            // Get node with lowest f-score
            AStarNode currentNode = openSet.poll();
            Point current = currentNode.point;

            // Skip if already visited
            if (visited[current.getY()][current.getX()]) {
                continue;
            }

            // Mark as visited
            visited[current.getY()][current.getX()] = true;

            // Check if we reached the goal
            if (current.getX() == end.getX() && current.getY() == end.getY()) {
                List<Point> path = reconstructPath(parentMap, start, end);
                lastPathLength = path.size();
                return path;
            }

            // Get current g-score (cost from start)
            int currentGScore = gScore.getOrDefault(current, Integer.MAX_VALUE);

            // Check all neighbors
            for (int[] dir : DIRECTIONS) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                // Skip invalid or obstacle cells
                if (!isValidPosition(newX, newY) ||
                        grid[newY][newX] == Cell.OBSTACLE) {
                    continue;
                }

                Point neighbor = new Point(newX, newY);

                // Calculate tentative g-score (1 step from current)
                int tentativeGScore = currentGScore + 1;

                // If this path is better than any previous one
                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    // Record this path
                    parentMap.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);

                    // Calculate f-score = g-score + heuristic
                    int fScore = tentativeGScore + calculateManhattanDistance(neighbor, end);

                    // Add to open set if not visited
                    if (!visited[newY][newX]) {
                        openSet.add(new AStarNode(neighbor, fScore));
                        aStarCellsExplored++; // Count each explored cell
                    }
                }
            }
        }

        return null; // No path found
    }

    /**
     * Helper class for A* algorithm to track nodes and their f-scores.
     */
    private class AStarNode {
        Point point;
        int fScore;

        AStarNode(Point point, int fScore) {
            this.point = point;
            this.fScore = fScore;
        }
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
        return visibleGrid[y][x]; // Return the visible grid cell
    }

    public Cell getRealCell(int x, int y) {
        return grid[y][x]; // Return the actual grid cell (for internal use)
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

    /**
     * Reveals a treasure at the specified location.
     * Used when player discovers a treasure.
     */
    public void revealTreasure(int x, int y) {
        if (isValidPosition(x, y) && grid[y][x] == Cell.TREASURE) {
            visibleGrid[y][x] = Cell.TREASURE;
        }
    }

    /**
     * Check if the position contains a revealed obstacle
     */
    public boolean isRevealedObstacle(int x, int y) {
        for (Point p : revealedObstacles) {
            if (p.getX() == x && p.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of cells explored by the BFS algorithm
     * during the last hint calculation.
     */
    public int getBFSCellsExplored() {
        return bfsCellsExplored;
    }

    /**
     * Returns the number of cells explored by the A* algorithm
     * during the last hint calculation.
     */
    public int getAStarCellsExplored() {
        return aStarCellsExplored;
    }

    /**
     * Returns the length of the last calculated path.
     */
    public int getLastPathLength() {
        return lastPathLength;
    }

    /**
     * Legacy method to maintain backward compatibility.
     * Uses BFS as the default algorithm.
     */
    public boolean showHint() {
        return showHintBFS();
    }
}