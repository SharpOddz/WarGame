package ui.dialogs;

import game.GameSerializer;
import game.GameState;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Dialogs {
    public static void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<File> showSaveDialog(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Scenario");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scenario JSON", "*.scenario.json"));
        return Optional.ofNullable(chooser.showSaveDialog(stage));
    }

    public static Optional<File> showOpenDialog(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load Scenario");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scenario JSON", "*.scenario.json", "*.json"));
        return Optional.ofNullable(chooser.showOpenDialog(stage));
    }

    public static boolean confirm(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(button -> button == javafx.scene.control.ButtonType.OK).isPresent();
    }

    public static void handleSave(Stage stage, GameState state) {
        showSaveDialog(stage).ifPresent(file -> {
            try {
                GameSerializer.saveToFile(state, file);
                state.setDirty(false);
            } catch (IOException e) {
                showInfo("Save Failed", e.getMessage());
            }
        });
    }

    public static Optional<GameState> handleLoad(Stage stage) {
        return showOpenDialog(stage).flatMap(file -> {
            try {
                GameState state = GameSerializer.loadFromFile(file);
                state.setDirty(false);
                return Optional.of(state);
            } catch (IOException e) {
                showInfo("Load Failed", e.getMessage());
                return Optional.empty();
            }
        });
    }
}
