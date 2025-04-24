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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            applyThemeUISettings();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView();
            GameController controller = new GameController(model, view);

            view.setVisible(true);
        });
    }

    /**
     * applies global UI settings for consistent navy and gold theme
     */
    private static void applyThemeUISettings() {
        UIManager.put("Panel.background", Theme.NAVY);
        UIManager.put("OptionPane.background", Theme.NAVY);
        UIManager.put("OptionPane.messageForeground", Theme.TEXT_LIGHT);
        UIManager.put("Label.foreground", Theme.TEXT_LIGHT);
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put("ScrollPane.background", Theme.NAVY);
        UIManager.put("ScrollBar.thumb", Theme.LIGHT_NAVY);
        UIManager.put("ScrollBar.track", Theme.NAVY);

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