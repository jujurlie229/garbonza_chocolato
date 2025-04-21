import controller.GameController;
import model.GameModel;
import view.GameView;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for the Treasure Hunt game.
 * Creates the model, view, and controller components.
 */
public class TreasureHunt {
    public static void main(String[] args) {
        // Set the look and feel to the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
}