package k.barrier.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sensor implements IDraw {
	
	// index of sensor
	public int index;

	// Sensor location
	private Point circle;

	// r
	public double r;

	// r
	public double alpha;

	// r
	public double beta;

	public double xL;
	public double xR;

	// source sensor
	public static final Sensor s = new Sensor(0);
	
	// target sensor
	public static final Sensor t = new Sensor(1);
	
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

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the beta
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * @param beta the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	private Sensor(int index) {
		this.index = index;
		this.circle = new Point(0, 0);
	}

	public Sensor(int index, double x, double y, double r, double alpha, double beta) {
		this.index = index;
		this.circle = new Point(x, y);
		this.r = r;
		this.alpha = alpha;
		this.beta = beta;

		List<Double> list = new ArrayList<Double>();
		list.add(x);
		list.add(x+r*Math.cos(beta-alpha));
		list.add(x+r*Math.cos(beta));
		list.add(x+r*Math.cos(beta+alpha));

		this.xL = Collections.min(list);
		this.xR = Collections.max(list);
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.fillArc((int) (circle.x - r), (int) (circle.y - r), (int) (2*r), (int) (2*r), (int)Math.toDegrees(beta-alpha), (int)Math.toDegrees(alpha*2));
	}

}
