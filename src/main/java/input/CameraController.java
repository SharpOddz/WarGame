package input;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import util.Clamp;

public class CameraController {
    private double offsetX = 50;
    private double offsetY = 50;
    private double zoom = 1.0;
    private double lastX;
    private double lastY;
    private boolean dragging;

    public void attach(Canvas canvas) {
        canvas.setOnMousePressed(this::onPress);
        canvas.setOnMouseDragged(this::onDrag);
        canvas.setOnScroll(e -> {
            double delta = e.getDeltaY() > 0 ? 0.1 : -0.1;
            zoom = Clamp.clamp(zoom + delta, 0.4, 2.5);
        });
    }

    private void onPress(MouseEvent e) {
        dragging = true;
        lastX = e.getX();
        lastY = e.getY();
    }

    private void onDrag(MouseEvent e) {
        if (!dragging) return;
        double dx = e.getX() - lastX;
        double dy = e.getY() - lastY;
        offsetX += dx;
        offsetY += dy;
        lastX = e.getX();
        lastY = e.getY();
    }

    public void onRelease(MouseEvent e) {
        dragging = false;
    }

    public void tick(boolean up, boolean down, boolean left, boolean right) {
        double speed = 5;
        if (up) offsetY -= speed;
        if (down) offsetY += speed;
        if (left) offsetX -= speed;
        if (right) offsetX += speed;
    }

    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getZoom() { return zoom; }

    public void setCamera(double offsetX, double offsetY, double zoom) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.zoom = zoom;
    }
}
