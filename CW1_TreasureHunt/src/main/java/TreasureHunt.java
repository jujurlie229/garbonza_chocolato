import controller.GameController;
import model.GameModel;
import view.GameView;
import view.Theme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.Enumeration;

/**
 * Main class for the Treasure Hunt game.
 * Creates the model, view, and controller components.
 * Updated with navy and gold theme styling.
 */
public class TreasureHunt {
    public static void main(String[] args) {
        // Set the look and feel to the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Apply global UI settings for navy and gold theme
            applyThemeUISettings();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Enable anti-aliased text and graphics
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            // Create the MVC components
            GameModel model = new GameModel();
            GameView view = new GameView();
            GameController controller = new GameController(model, view);

            // Display the view
            view.setVisible(true);
        });
    }

    /**
     * Applies global UI settings for consistent navy and gold theme
     */
    private static void applyThemeUISettings() {
        // Set global UI colors for components
        UIManager.put("Panel.background", Theme.NAVY);
        UIManager.put("OptionPane.background", Theme.NAVY);
        UIManager.put("OptionPane.messageForeground", Theme.TEXT_LIGHT);
        UIManager.put("Label.foreground", Theme.TEXT_LIGHT);
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put("ScrollPane.background", Theme.NAVY);
        UIManager.put("ScrollBar.thumb", Theme.LIGHT_NAVY);
        UIManager.put("ScrollBar.track", Theme.NAVY);

        // Update default font for all UI components
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, Theme.NORMAL_FONT);
            }
        }
    }
}