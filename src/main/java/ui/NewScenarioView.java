package ui;

import game.GameState;
import game.ScenarioConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import map.MapGenerator;
import util.RandomUtil;
import ui.dialogs.Dialogs;

public class NewScenarioView {
    private final BorderPane root;

    public NewScenarioView(SceneRouter router) {
        root = new BorderPane();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(12);
        grid.setVgap(12);

        ComboBox<String> biomeBox = new ComboBox<>();
        biomeBox.getItems().addAll("Temperate", "Desert", "Arctic", "Tropical", "Islands", "Steppe");
        biomeBox.getSelectionModel().selectFirst();

        ComboBox<String> mapSize = new ComboBox<>();
        mapSize.getItems().addAll("100x100", "150x150", "200x200");
        mapSize.getSelectionModel().selectFirst();

        ComboBox<String> userNation = new ComboBox<>();
        userNation.getItems().addAll("NATO", "PACT", "Non-Aligned", "Regional Power");
        userNation.getSelectionModel().selectFirst();

        ComboBox<String> aiNation = new ComboBox<>();
        aiNation.getItems().addAll(userNation.getItems());
        aiNation.getSelectionModel().select(1);

        ComboBox<String> aiDifficulty = new ComboBox<>();
        aiDifficulty.getItems().addAll("Easy", "Normal", "Hard", "Expert");
        aiDifficulty.getSelectionModel().select(1);

        TextField seedField = new TextField();
        seedField.setPromptText("Random seed");

        grid.add(new Label("Scenario/Biome"), 0, 0);
        grid.add(biomeBox, 1, 0);
        grid.add(new Label("Map size"), 0, 1);
        grid.add(mapSize, 1, 1);
        grid.add(new Label("User nation"), 0, 2);
        grid.add(userNation, 1, 2);
        grid.add(new Label("AI nation"), 0, 3);
        grid.add(aiNation, 1, 3);
        grid.add(new Label("AI difficulty"), 0, 4);
        grid.add(aiDifficulty, 1, 4);
        grid.add(new Label("Seed"), 0, 5);
        grid.add(seedField, 1, 5);

        Button start = new Button("Start Scenario");
        Button back = new Button("Back");

        start.setOnAction(e -> {
            long seed;
            try {
                seed = seedField.getText().isBlank() ? RandomUtil.randomSeed() : Long.parseLong(seedField.getText());
            } catch (NumberFormatException ex) {
                Dialogs.showInfo("Invalid seed", "Seed must be a number.");
                return;
            }
            String[] sizeParts = mapSize.getValue().split("x");
            int width = Integer.parseInt(sizeParts[0]);
            int height = Integer.parseInt(sizeParts[1]);
            ScenarioConfig config = new ScenarioConfig(biomeBox.getValue(), width, height,
                    userNation.getValue(), aiNation.getValue(), aiDifficulty.getValue(), seed);
            GameState state = MapGenerator.generate(config);
            router.showGame(state);
        });

        back.setOnAction(e -> router.showMainMenu());

        GridPane buttons = new GridPane();
        buttons.setHgap(10);
        buttons.add(start, 0, 0);
        buttons.add(back, 1, 0);

        BorderPane.setAlignment(buttons, Pos.CENTER);
        BorderPane.setMargin(grid, new Insets(10));
        root.setCenter(grid);
        root.setBottom(buttons);
    }

    public BorderPane getRoot() {
        return root;
    }
}
