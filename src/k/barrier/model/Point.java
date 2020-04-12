package k.barrier.model;

import java.awt.Graphics2D;

/**
 * 2D Point include x, y coordinate
 * @author ljnk975
 *
 */
public class Point implements Cloneable, IDraw {

	/**
	 * x-coordinate
	 */
	public double x;

	/**
	 * y-coordinate
	 */
	public double y;

	/**
	 * Create (0, 0) point
	 */
	public Point() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Create a point with x, y coordinate
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a point with coordinate copy from another point
	 * @param other the point to copy coordinate
	 */
	private Point(Point other) {
		this.x = other.x;
		this.y = other.y;
	}

	//
	public static int rDraw = 2;

	@Override
	public void draw(Graphics2D g) {
		g.fillOval((int) (x - rDraw), (int) (y - rDraw), 2*rDraw, 2*rDraw);
	}

	/**
	 * Tinh do lon cua vector co 2 chieu x, y dinh nghia boi point
	 * @return do lon r
	 */
	public double r() {
		return Math.sqrt(x*x+y*y);
	}

	/**
	 * Khoang cach tu diem nay den diem khac
	 * @param other diem den
	 * @return khoang cach
	 */
	public double distance(Point other) {
		return Math.sqrt((other.x-x)*(other.x-x)+(other.y-y)*(other.y-y));
	}

	/**
	 * Lay vector huong tu diem nay den p
	 * @param p diem den
	 * @return 1 diem the hien vector huong den p
	 */
	public Point v(Point p) {
		double dx = p.x-x;
        double dy = p.y-y;
        double dd = Math.sqrt(dx * dx + dy * dy);
		return new Point(dx/dd, dy/dd);
	}

	@Override
	public String toString() {
		return "Point: (" + x + ", " + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		Point p = (Point) o;
		return ((this.x == p.x) && (this.y == p.y));
	}

	@Override
	public int hashCode() {
		return 100 * (int) (x + y);
	}

	@Override
	public Object clone() {
		return new Point(this);
	}
	
}
