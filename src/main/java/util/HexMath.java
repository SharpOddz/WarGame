package util;

import javafx.geometry.Point2D;

public class HexMath {
    // Pointy top odd-r offset coordinate system
    public static final double HEX_SIZE = 16.0;
    private static final double WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double HEIGHT = 2 * HEX_SIZE;

    public static Point2D axialToPixel(int q, int r, double zoom, double offsetX, double offsetY) {
        double x = HEX_SIZE * Math.sqrt(3) * (q + 0.5 * (r & 1));
        double y = HEX_SIZE * 1.5 * r;
        return new Point2D(x * zoom + offsetX, y * zoom + offsetY);
    }

    public static int[] pixelToAxial(double x, double y, double zoom, double offsetX, double offsetY) {
        double px = (x - offsetX) / zoom;
        double py = (y - offsetY) / zoom;
        double q = (Math.sqrt(3) / 3 * px - 1.0 / 3 * py);
        double r = (2.0 / 3 * py);
        return cubeToAxial(cubeRound(q, -q - r, r));
    }

    public static int[] cubeRound(double x, double y, double z) {
        int rx = (int) Math.round(x);
        int ry = (int) Math.round(y);
        int rz = (int) Math.round(z);

        double xDiff = Math.abs(rx - x);
        double yDiff = Math.abs(ry - y);
        double zDiff = Math.abs(rz - z);

        if (xDiff > yDiff && xDiff > zDiff) {
            rx = -ry - rz;
        } else if (yDiff > zDiff) {
            ry = -rx - rz;
        } else {
            rz = -rx - ry;
        }
        return new int[]{rx, ry, rz};
    }

    public static int[] cubeToAxial(int[] cube) {
        return new int[]{cube[0], cube[2]};
    }

    public static int[] axialToOffset(int q, int r) {
        int col = q + (r - (r & 1)) / 2;
        int row = r;
        return new int[]{col, row};
    }

    public static int[] offsetToAxial(int col, int row) {
        int q = col - (row - (row & 1)) / 2;
        int r = row;
        return new int[]{q, r};
    }

    public static Point2D[] polygonCorners(int col, int row, double zoom, double offsetX, double offsetY) {
        int[] axial = offsetToAxial(col, row);
        Point2D center = axialToPixel(axial[0], axial[1], zoom, offsetX, offsetY);
        Point2D[] pts = new Point2D[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 180 * (60 * i - 30);
            double x = center.getX() + HEX_SIZE * zoom * Math.cos(angle);
            double y = center.getY() + HEX_SIZE * zoom * Math.sin(angle);
            pts[i] = new Point2D(x, y);
        }
        return pts;
    }

    public static Point2D hexCenter(int col, int row, double zoom, double offsetX, double offsetY) {
        int[] axial = offsetToAxial(col, row);
        return axialToPixel(axial[0], axial[1], zoom, offsetX, offsetY);
    }

    public static int[][] neighborCoords(int col, int row) {
        int parity = row & 1;
        int[][] dirs = new int[][]{
                {+1, 0}, {0, +1}, {-1, +1}, {-1, 0}, {-1 + parity, -1}, {parity, -1}
        };
        int[][] result = new int[6][2];
        for (int i = 0; i < 6; i++) {
            result[i][0] = col + dirs[i][0];
            result[i][1] = row + dirs[i][1];
        }
        return result;
    }
}
