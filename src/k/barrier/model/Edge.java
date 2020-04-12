package k.barrier.model;

import java.awt.Graphics2D;

/**
 * 2D Edge
 * @author ljnk975
 *
 */
public class Edge implements IDraw {

	/**
	 * Start point
	 */
	private Point A;

	/**
	 * End point
	 */
	private Point B;

	/**
	 * Constructor
	 * @param A diem dau
	 * @param B diem cuoi
	 */
	public Edge(Point A, Point B) {
		this.A = A;
		this.B = B;
	}

	/**
	 * Get the start point of this edge
	 * @return the start point
	 */
	public Point getFirstPoint() {
		return A;
	}

	/**
	 * Get the end point of this edge
	 * @return the end point
	 */
	public Point getSecondPoint() {
		return A;
	}

	/**
	 * Get the mid point<br/>
	 * midpoint = [(startX+endX)/2, (startY+endY)/2]
	 * @return midPoint of edge A-B
	 */
	public Point getMidPoint() {
		double mid_x = (A.x + B.x) / 2;
		double mid_y = (A.y + B.y) / 2;
		return new Point(mid_x, mid_y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(Graphics2D g) {
		g.drawLine((int) A.x, (int) A.y, (int) B.x, (int) B.y);
	}

	@Override
	public String toString() {
		return "Edge: (" + A.x + ";" + A.y + ") -- (" + B.x + ";" + B.y + ")";
	}

	@Override
	public boolean equals(Object obj) {
		Edge edge = (Edge) obj;
		return (this.A.equals(edge.A)) && (this.B.equals(edge.B))
				|| (this.A.equals(edge.B)) && (this.B.equals(edge.A));
	}

	@Override
	public int hashCode() {
		return 100 * (int) (this.A.x + this.A.y + this.B.x + this.B.y);
	}

}
