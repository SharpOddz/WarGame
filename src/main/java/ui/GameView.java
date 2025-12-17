package ui;

import game.GameState;
import input.CameraController;
import input.InputBindings;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import map.HexMap;
import render.MapRenderer;
import render.OverlayRenderer;
import ui.dialogs.Dialogs;
import util.HexMath;

public class GameView {
    private final BorderPane root;
    private final Canvas canvas;
    private final MapRenderer mapRenderer;
    private final OverlayRenderer overlayRenderer;
    private final CameraController camera;
    private final InputBindings keys;
    private int hoverCol = -1;
    private int hoverRow = -1;
    private boolean showUnits = true;

    public GameView(SceneRouter router, GameState state) {
        this.root = new BorderPane();
        this.canvas = new Canvas(1280, 880);
        this.mapRenderer = new MapRenderer();
        this.overlayRenderer = new OverlayRenderer();
        this.camera = new CameraController();
        this.keys = new InputBindings();
        camera.setCamera(state.getCameraX(), state.getCameraY(), state.getZoom());

        StackPane stack = new StackPane(canvas);
        stack.setStyle("-fx-background-color: #000000;");
        root.setCenter(stack);

        Button menuButton = new Button("☰");
        menuButton.setOnAction(e -> toggleMenu(router, state));
        HBox top = new HBox(menuButton);
        top.setPadding(new Insets(8));
        root.setTop(top);

        ToggleButton unitsToggle = new ToggleButton("Show Units");
        unitsToggle.setSelected(true);
        unitsToggle.setOnAction(e -> showUnits = unitsToggle.isSelected());
        HBox bottom = new HBox(unitsToggle);
        bottom.setPadding(new Insets(8));
        root.setBottom(bottom);

        camera.attach(canvas);
        canvas.setOnMouseMoved(e -> updateHover(e.getX(), e.getY())) ;
        canvas.setOnMouseReleased(camera::onRelease);

        SceneRouter r = router;
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                camera.tick(keys.isUp(), keys.isDown(), keys.isLeft(), keys.isRight());
                state.setCamera(camera.getOffsetX(), camera.getOffsetY(), camera.getZoom());
                draw(state);
            }
        };
        timer.start();

    }

    public void attachInputs(javafx.scene.Scene scene) {
        keys.attach(scene);
    }

    private void toggleMenu(SceneRouter router, GameState state) {
        javafx.scene.control.ContextMenu menu = new javafx.scene.control.ContextMenu();
        javafx.scene.control.MenuItem save = new javafx.scene.control.MenuItem("Save Scenario");
        javafx.scene.control.MenuItem load = new javafx.scene.control.MenuItem("Load Scenario");
        javafx.scene.control.MenuItem settings = new javafx.scene.control.MenuItem("Settings");
        javafx.scene.control.MenuItem exitToMenu = new javafx.scene.control.MenuItem("Exit to Main Menu");
        javafx.scene.control.MenuItem exitGame = new javafx.scene.control.MenuItem("Exit Game");
        save.setOnAction(e -> Dialogs.handleSave(router.getStage(), state));
        load.setOnAction(e -> Dialogs.handleLoad(router.getStage()).ifPresent(router::showGame));
        settings.setOnAction(e -> Dialogs.showInfo("Settings", "Settings coming soon."));
        exitToMenu.setOnAction(e -> {
            if (state.isDirty()) {
                if (!Dialogs.confirm("Unsaved", "You have unsaved changes. Exit?")) return;
            }
            router.showMainMenu();
        });
        exitGame.setOnAction(e -> router.exitGame());
        menu.getItems().addAll(save, load, settings, exitToMenu, exitGame);
        menu.show(root, javafx.geometry.Side.LEFT, 0, 0);
    }

    private void updateHover(double x, double y) {
        int[] axial = HexMath.pixelToAxial(x, y, camera.getZoom(), camera.getOffsetX(), camera.getOffsetY());
        int[] offset = HexMath.axialToOffset(axial[0], axial[1]);
        hoverCol = offset[0];
        hoverRow = offset[1];
    }

    private void draw(GameState state) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        HexMap map = state.getMap();
        mapRenderer.render(gc, map, camera.getZoom(), camera.getOffsetX(), camera.getOffsetY(), hoverCol, hoverRow, showUnits ? state.getUnits() : java.util.Collections.emptyList());
        overlayRenderer.drawHoverInfo(gc, map, hoverCol, hoverRow, state.getUnits());
        gc.setFill(Color.WHITE);
        gc.fillText("WASD to pan, wheel to zoom, drag to pan", 10, canvas.getHeight() - 10);
    }

    public BorderPane getRoot() {
        return root;
    }
}
