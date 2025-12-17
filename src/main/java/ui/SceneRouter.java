package ui;

import game.GameState;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.dialogs.Dialogs;

public class SceneRouter {
    private final Stage stage;
    private GameState currentGameState;

    public SceneRouter(Stage stage) {
        this.stage = stage;
        stage.setTitle("Modern Hex Wargame Prototype");
    }

    public void showMainMenu() {
        MainMenuView view = new MainMenuView(this);
        Scene scene = new Scene(view.getRoot(), 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

    public void showNewScenario() {
        NewScenarioView view = new NewScenarioView(this);
        Scene scene = new Scene(view.getRoot(), 1024, 768);
        stage.setScene(scene);
    }

    public void showGame(GameState state) {
        this.currentGameState = state;
        GameView view = new GameView(this, state);
        Scene scene = new Scene(view.getRoot(), 1280, 900);
        view.attachInputs(scene);
        stage.setScene(scene);
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public void exitGame() {
        stage.close();
    }

    public void showPlaceholder(String title, String message) {
        Dialogs.showInfo(title, message);
    }
}
