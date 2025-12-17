package app;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.SceneRouter;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        SceneRouter router = new SceneRouter(primaryStage);
        router.showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
