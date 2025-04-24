package model;

import java.util.*;
import java.util.List;

/**
 * The main model class for the Treasure Hunt game.
 * Contains all the game state and logic.
 */
public class GameModel {
    // constants
    public static final int GRID_SIZE = 20;
    public static final int NUM_TREASURES = 3;
    public static final int MIN_OBSTACLES = 10;
    public static final int MAX_OBSTACLES = 100;
    public static final int INITIAL_SCORE = 100;

    // game state
    private Cell[][] grid;
    private Cell[][] visibleGrid;
    private Point playerPosition;
    private int score;
    private int treasuresFound;
    private List<Point> treasureLocations;
    private List<Point> currentPath;
    private boolean hintUsedSinceLastMove;
    private List<Point> revealedObstacles;
    private List<Point> discoveredTreasures;

    // search algorithm statistics
    private int bfsCellsExplored;
    private int aStarCellsExplored;
    private int lastPathLength;

    // direction vectors for movement and pathfinding
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // down, right, up, left

    /**
     *  initializes the game state and generates the initial map.
     */
    public GameModel() {
        grid = new Cell[GRID_SIZE][GRID_SIZE];
        visibleGrid = new Cell[GRID_SIZE][GRID_SIZE];
        treasureLocations = new ArrayList<>();
        currentPath = new ArrayList<>();
        revealedObstacles = new ArrayList<>();
        discoveredTreasures = new ArrayList<>();
        hintUsedSinceLastMove = false;
        resetGame();
    }

    /**
     * resets the game to its initial state.
     */
    public void resetGame() {
        score = INITIAL_SCORE;
        treasuresFound = 0;
        hintUsedSinceLastMove = false;
        bfsCellsExplored = 0;
        aStarCellsExplored = 0;
        lastPathLength = 0;
        revealedObstacles.clear();
        discoveredTreasures.clear();
        generateMap();
    }

    /**
     * generates a new random map with obstacles and treasures.
     */
    public void generateMap() {
        // Initialize map with empty cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = Cell.EMPTY;
                visibleGrid[i][j] = Cell.EMPTY;
            }
        }

        // placing the player in the center
        playerPosition = new Point(GRID_SIZE / 2, GRID_SIZE / 2);
        grid[playerPosition.getY()][playerPosition.getX()] = Cell.PLAYER;
        visibleGrid[playerPosition.getY()][playerPosition.getX()] = Cell.PLAYER;

        // placing the random obstacles
        Random rand = new Random();
        int numObstacles = rand.nextInt(MAX_OBSTACLES - MIN_OBSTACLES + 1) + MIN_OBSTACLES;

        for (int i = 0; i < numObstacles; i++) {
            int x = rand.nextInt(GRID_SIZE);
            int y = rand.nextInt(GRID_SIZE);

            if ((x != playerPosition.getX() || y != playerPosition.getY()) && grid[y][x] == Cell.EMPTY) {
                grid[y][x] = Cell.OBSTACLE;
            }
        }

        // placing treasures
        placeTreasures();
    }

    /**
     * places treasures on the map, ensuring they are reachable from the player's position.
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

                if (grid[y][x] == Cell.EMPTY &&
                        (x != playerPosition.getX() || y != playerPosition.getY())) {

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
     * check if a position is valid (within grid bounds).
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

    /**
     * check if there is a path from start to end point.
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
     * attempting to move the player in the specified direction.
     * returns true if the move resulted in finding a treasure.
     */
    public boolean movePlayer(Direction direction) {
        int newX = playerPosition.getX();
        int newY = playerPosition.getY();

        clearPathHints();

        hintUsedSinceLastMove = false;

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

        if (isValidPosition(newX, newY)) {
            if (grid[newY][newX] == Cell.OBSTACLE) {
                score -= 10;
                hitObstacle = true;
                obstaclePoint = new Point(newX, newY);

                visibleGrid[newY][newX] = Cell.OBSTACLE;
                revealedObstacles.add(obstaclePoint);
            } else {
                Point oldPosition = new Point(playerPosition.getX(), playerPosition.getY());

                boolean isOnDiscoveredTreasure = false;
                for (Point p : discoveredTreasures) {
                    if (p.getX() == oldPosition.getX() && p.getY() == oldPosition.getY()) {
                        visibleGrid[oldPosition.getY()][oldPosition.getX()] = Cell.TREASURE;
                        isOnDiscoveredTreasure = true;
                        break;
                    }
                }

                if (!isOnDiscoveredTreasure) {
                    visibleGrid[oldPosition.getY()][oldPosition.getX()] = Cell.EMPTY;
                }

                grid[playerPosition.getY()][playerPosition.getX()] = Cell.EMPTY;

                if (grid[newY][newX] == Cell.TREASURE) {
                    treasuresFound++;
                    Point treasurePoint = new Point(newX, newY);
                    int finalNewX = newX;
                    int finalNewY = newY;
                    treasureLocations.removeIf(p -> p.getX() == finalNewX && p.getY() == finalNewY);
                    foundTreasure = true;

                    discoveredTreasures.add(treasurePoint);

                    visibleGrid[newY][newX] = Cell.TREASURE;
                }

                playerPosition = new Point(newX, newY);
                grid[newY][newX] = Cell.PLAYER;
                visibleGrid[newY][newX] = Cell.PLAYER;

                score -= 1;
            }
        }

        return foundTreasure;
    }

    /**
     * clears any path hints from the map.
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
     * showing only the next step towards the nearest treasure using BFS.
     * returns true if a path was found.
     */
    public boolean showHintBFS() {
        clearPathHints();

        if (treasureLocations.isEmpty()) {
            return false;
        }

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

        if (shortestPath != null && !shortestPath.isEmpty()) {
            Point nextStep = shortestPath.get(0);

            if (grid[nextStep.getY()][nextStep.getX()] != Cell.OBSTACLE ||
                    visibleGrid[nextStep.getY()][nextStep.getX()] == Cell.OBSTACLE) {

                visibleGrid[nextStep.getY()][nextStep.getX()] = Cell.PATH_HINT;
                currentPath.add(nextStep);
            }

            lastPathLength = shortestPath.size();

            if (!hintUsedSinceLastMove) {
                score -= 3;
                hintUsedSinceLastMove = true;
            }

            return true;
        }

        return false;
    }

    /**
     * shows only the next step towards the nearest treasure using A* search.
     * returns true if a path was found.
     */
    public boolean showHintAStar() {
        clearPathHints();

        if (treasureLocations.isEmpty()) {
            return false;
        }

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

        if (shortestPath != null && !shortestPath.isEmpty()) {
            Point nextStep = shortestPath.get(0);

            if (grid[nextStep.getY()][nextStep.getX()] != Cell.OBSTACLE ||
                    visibleGrid[nextStep.getY()][nextStep.getX()] == Cell.OBSTACLE) {

                visibleGrid[nextStep.getY()][nextStep.getX()] = Cell.PATH_HINT;
                currentPath.add(nextStep);
            }

            lastPathLength = shortestPath.size();

            if (!hintUsedSinceLastMove) {
                score -= 3;
                hintUsedSinceLastMove = true;
            }

            return true;
        }

        return false;
    }

    /**
     * finding the shortest path between two points using BFS.
     */
    private List<Point> findShortestPath(Point start, Point end) {
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>();

        bfsCellsExplored = 0;

        queue.add(start);
        visited[start.getY()][start.getX()] = true;
        bfsCellsExplored++;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.getX() == end.getX() && current.getY() == end.getY()) {
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
                    bfsCellsExplored++;
                }
            }
        }

        return null;
    }

    /**
     * calculates the Manhattan distance between two points.
     */
    private int calculateManhattanDistance(Point a, Point b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * find the path between two points using A* search algorithm.
     */
    private List<Point> findPathAStar(Point start, Point end) {
        aStarCellsExplored = 0;

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(
                Comparator.comparingInt(node -> node.fScore)
        );

        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];

        Map<Point, Integer> gScore = new HashMap<>();

        Map<Point, Point> parentMap = new HashMap<>();

        gScore.put(start, 0);
        int startFScore = calculateManhattanDistance(start, end);
        openSet.add(new AStarNode(start, startFScore));
        aStarCellsExplored++;

        while (!openSet.isEmpty()) {
            AStarNode currentNode = openSet.poll();
            Point current = currentNode.point;

            if (visited[current.getY()][current.getX()]) {
                continue;
            }

            visited[current.getY()][current.getX()] = true;

            if (current.getX() == end.getX() && current.getY() == end.getY()) {
                List<Point> path = reconstructPath(parentMap, start, end);
                lastPathLength = path.size();
                return path;
            }

            int currentGScore = gScore.getOrDefault(current, Integer.MAX_VALUE);

            for (int[] dir : DIRECTIONS) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                if (!isValidPosition(newX, newY) ||
                        grid[newY][newX] == Cell.OBSTACLE) {
                    continue;
                }

                Point neighbor = new Point(newX, newY);

                int tentativeGScore = currentGScore + 1;

                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    parentMap.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);

                    int fScore = tentativeGScore + calculateManhattanDistance(neighbor, end);

                    if (!visited[newY][newX]) {
                        openSet.add(new AStarNode(neighbor, fScore));
                        aStarCellsExplored++;
                    }
                }
            }
        }

        return null;
    }

    /**
     * helper class for A* algorithm to track nodes and their f-scores.
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
     * reconstructs a path from the parent map created during pathfinding.
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

    // getters and setters
    public Cell getCell(int x, int y) {
        return visibleGrid[y][x]; // Return the visible grid cell
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
     * returns the number of cells explored by the BFS algorithm
     * during the last hint calculation.
     */
    public int getBFSCellsExplored() {
        return bfsCellsExplored;
    }

    /**
     * return the number of cells explored by the A* algorithm
     * during the last hint calculation.
     */
    public int getAStarCellsExplored() {
        return aStarCellsExplored;
    }

    /**
     * returns the length of the last calculated path.
     */
    public int getLastPathLength() {
        return lastPathLength;
    }

}