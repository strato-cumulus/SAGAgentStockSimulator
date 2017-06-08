package model.math;

/**
 * Created by Marcin on 09.06.2017.
 */
public class Line {
    private Double m;
    private Double b;

    public Line (Double m, Double b) {
        this.m = m;
        this.b = b;
    }

    public Double getB() {
        return b;
    }

    public Double getM() {
        return m;
    }

    public static Line getLineFromPoints(Point A, Point B) {
        Double m = (B.getY() - A.getY()) / (B.getX() - A.getX());
        Double b = A.getY() - m * A.getX();
        return new Line(m, b);
    }

    public Point getIntersection(Line B) {
        Double W = B.getM() - this.getM();
        Double Wx = this.getB() - B.getB();
        Double Wy = B.getM() * this.getB() - this.getM() * B.getB();
        Double x = Wx / W;
        Double y = Wy / W;
        return new Point(x, y);
    }
}
