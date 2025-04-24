package view;

import java.awt.Color;
import java.awt.Font;

/**
 * Defines the color theme and styles for the game UI.
 */
public class Theme {
    // Main colors
    public static final Color NAVY = new Color(25, 40, 65);
    public static final Color GOLD = new Color(218, 165, 32);
    public static final Color SOFT_GOLD = new Color(240, 220, 130);
    public static final Color LIGHT_NAVY = new Color(45, 60, 90);
    public static final Color DARK_NAVY = new Color(15, 25, 40);

    // Text colors
    public static final Color TEXT_LIGHT = new Color(240, 240, 240);
    public static final Color TEXT_DARK = new Color(20, 20, 30);

    // Game element colors
    public static final Color PLAYER_COLOR = new Color(70, 130, 180);  // Steel blue
    public static final Color PLAYER_HIGHLIGHT = new Color(100, 160, 220);
    public static final Color TREASURE_COLOR = GOLD;
    public static final Color OBSTACLE_COLOR = DARK_NAVY;
    public static final Color EMPTY_CELL_COLOR = LIGHT_NAVY;
    public static final Color PATH_HINT_BFS_COLOR = new Color(120, 200, 120);  // Light green
    public static final Color PATH_HINT_ASTAR_COLOR = new Color(100, 150, 250);  // Light blue

    // Fonts
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font STATS_FONT = new Font("SansSerif", Font.ITALIC, 12);
}