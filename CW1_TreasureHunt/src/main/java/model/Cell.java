package model;

/**
 * Represents a cell in the game grid with different possible types.
 */
public enum Cell {
    EMPTY,
    OBSTACLE,
    PLAYER,
    TREASURE,
    PATH_HINT;

    /**
     * Converts a cell type to its integer representation.
     * Used for backward compatibility.
     */
    public int toInt() {
        return this.ordinal();
    }

    /**
     * Converts an integer to its corresponding Cell type.
     * Used for backward compatibility.
     */
    public static Cell fromInt(int value) {
        return Cell.values()[value];
    }
}
