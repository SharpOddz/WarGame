package ui;

import game.GameState;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ui.dialogs.Dialogs;

public class MainMenuView {
    private final VBox root;

    public MainMenuView(SceneRouter router) {
        root = new VBox(16);
        root.setAlignment(Pos.CENTER);

        Button newScenario = new Button("New Scenario");
        Button loadScenario = new Button("Load Scenario");
        Button multiplayer = new Button("Multiplayer");
        Button settings = new Button("Settings");
        Button exit = new Button("Exit Game");

        newScenario.setOnAction(e -> router.showNewScenario());
        loadScenario.setOnAction(e -> Dialogs.handleLoad(router.getStage()).ifPresent(router::showGame));
        multiplayer.setOnAction(e -> router.showPlaceholder("Multiplayer", "Multiplayer mode coming soon."));
        settings.setOnAction(e -> router.showPlaceholder("Settings", "Settings dialog coming soon."));
        exit.setOnAction(e -> router.exitGame());

        root.getChildren().addAll(newScenario, loadScenario, multiplayer, settings, exit);
    }

    public VBox getRoot() {
        return root;
    }
}
