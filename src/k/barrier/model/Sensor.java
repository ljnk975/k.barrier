package k.barrier.model;

import java.awt.Color;
import java.awt.Graphics2D;

public class Sensor implements IDraw {
	
	// index of sensor
	public int index;

	// Sensor location
	private Point circle;

	// r
	public double r;

	// source sensor
	public static final Sensor s = new Sensor(0, 0, 0, 0);
	
	// target sensor
	public static final Sensor t = new Sensor(1, 0, 0, 0);
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the circle
	 */
	public Point getCircle() {
		return circle;
	}

	/**
	 * @param circle the circle to set
	 */
	public void setCircle(Point circle) {
		this.circle = circle;
	}

	/**
	 * @return the r
	 */
	public double getR() {
		return r;
	}

	/**
	 * @param r the r to set
	 */
	public void setR(double r) {
		this.r = r;
	}

	public Sensor(int index, double x, double y, double l) {
		this.index = index;
		this.circle = new Point(x, y);
		this.r = l;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.fillArc((int) (circle.x - r), (int) (circle.y - r), (int) (2*r), (int) (2*r), 0, 360);
	}

}
