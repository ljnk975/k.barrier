package k.barrier.util;

import k.barrier.model.Sensor;

/**
 * Class cong cu, ho tro tinh khoang cach giua 2 sensor
 * @author ljnk975
 *
 */
public class Distance {

    /**
     * distance between two sensor
     * @param vi
     * @param vj
     * @return
     */
    public static int w(Sensor vi, Sensor vj, double l, double lr, boolean weak) {
    	if (weak)
    		return dw(vi, vj, l, lr);
        return ds(vi, vj, l, lr);
    }

    /**
	 * weak distance
	 * @param vi
	 * @param vj
	 * @return
	 */
    public static int dw(Sensor vi, Sensor vj, double l, double lr) {
    	// trung
    	if (vi == vj)
    		return 0;
    	if (vi == Sensor.s && vj == Sensor.t || vi == Sensor.t && vj == Sensor.s)
    		return (int) Math.ceil (l/lr);
    	if (vi == Sensor.s)
    		return (int) Math.ceil (distance(0, 0, vj.getCircle().x, vj.getR())/lr);
    	if (vi == Sensor.t)
    		return (int) Math.ceil (distance(l, 0, vj.getCircle().x, vj.getR())/lr);
    	if (vj == Sensor.s)
    		return (int) Math.ceil (distance(vi.getCircle().x, vi.getR(), 0, 0)/lr);
    	if (vj == Sensor.t)
    		return (int) Math.ceil (distance(vi.getCircle().x, vi.getR(), l, 0)/lr);
    	return (int) Math.ceil (distance(vi.getCircle().x, vi.getR(), vj.getCircle().x, vj.getR())/lr);
    }

    /**
     * strong distance
     * @param vi
     * @param vj
     * @return
     */
    public static int ds(Sensor vi, Sensor vj, double l, double lr) {
    	// trung
    	if (vi == vj)
    		return 0;
    	// Khoang cach tu dinh ao s, t theo cong thuc weak barrier
    	if (vi == Sensor.s || vi == Sensor.t || vj == Sensor.s || vj == Sensor.t)
    		return dw(vi, vj, l, lr);
    	// Neu 2 sensor la sensor thuong
    	return (int) Math.ceil(distance(vi.getCircle().x, vi.getCircle().y, vi.getR(), vj.getCircle().x, vj.getCircle().y, vj.getR())/lr);
    }

    /**
	 * Khoang cach euclid giua 2 diem (x1, y1) (x2, y2) <br/>
	 * r = sqrt(sqr(x1-x2)+sqr(y1-y2))
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return r
	 */
	public static double distancePoint(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}

	/**
	 * Khoang cach giua 2 duong tron tam (x1, y1) ban kinh r1 va (x2, y2) ban kinh r2 <br/>
	 * @param x1
	 * @param y1
	 * @param r1
	 * @param x2
	 * @param y2
	 * @param r2
	 * @return r
	 */
	public static double distance(double x1, double y1, double r1, double x2, double y2, double r2) {
		double r = distancePoint(x1, y1, x2, y2);
		if (r <= r1 + r2)
			return 0;
		return r - r1 - r2;
	}

	/**
	 * Khoang cach giua 2 duong tron x1 ban kinh r1 va x2 ban kinh r2 
	 * @param x1
	 * @param r1
	 * @param x2
	 * @param r2
	 * @return r
	 */
	public static double distance(double x1, double r1, double x2, double r2) {
		double r = Math.abs(x1-x2);
		if (r <= r1 + r2)
			return 0;
		return r - r1 - r2;
	}

}
