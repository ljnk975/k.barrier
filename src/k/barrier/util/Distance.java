package k.barrier.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import k.barrier.model.Point;
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
    		return (int) Math.ceil(dw(vi, vj, l)/lr);
        return (int) Math.ceil(ds(vi, vj, l)/lr);
    }

    /**
	 * weak distance
	 * @param vi
	 * @param vj
	 * @return
	 */
    public static double dw(Sensor vi, Sensor vj, double l) {
    	// trung
    	if (vi == vj)
    		return 0;
    	if (vi == Sensor.s && vj == Sensor.t || vi == Sensor.t && vj == Sensor.s)
    		return l;
    	if (vi == Sensor.s)
    		return vj.xL;
    	if (vi == Sensor.t)
    		return l - vj.xR;
    	if (vj == Sensor.s)
    		return vi.xL;
    	if (vj == Sensor.t)
    		return l - vi.xR;
        if ((vi.xL <= vj.xL && vj.xL <= vi.xR) || (vj.xL <= vi.xL && vi.xL <= vj.xR))
            return 0;
        if (vj.xL-vi.xR > 0)
            return vj.xL-vi.xR;
        return vi.xL-vj.xR;
    }

    /**
     * strong distance
     * @param vi
     * @param vj
     * @return
     */
    public static double ds(Sensor vi, Sensor vj, double l) {
    	// trung
    	if (vi == vj)
    		return 0;
    	// Khoang cach tu dinh ao s, t theo cong thuc weak barrier
    	if (vi == Sensor.s || vi == Sensor.t || vj == Sensor.s || vj == Sensor.t)
    		return dw(vi, vj, l);
    	// Neu 2 sensor la sensor thuong
    	return minimum__sectors_distance(vi, vj);
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

	public static double angle(double value) {
	    // chuan hoa gia tri goc tu -pi den pi
	    return (value) % (2*Math.PI);
	    // return (value+np.pi) % (2*np.pi) - np.pi
	}

	public static double arctan(double y, double x) {
	    // if x >= 0:
	    //     return np.arctan(y/x)
	    if (x == 0) {
	        if (y > 0)
	            return Math.PI / 2;
	        return Math.PI * 3 / 2;
	    }
	    if (x > 0)
	        return Math.atan(y/x);
	    return Math.PI+Math.atan(y/x);
	}

	public static boolean is_between_angles(double value, double angle1, double angle2) {
		double angle_min = Math.min(angle1, angle2);
		double angle_max = Math.max(angle1, angle2);
	    
		// angle_min = min(angle(angle1), angle(angle2))
	    // angle_max = max(angle(angle1), angle(angle2))
	    // return angle_min <= angle(value) <= angle_max
	    
		if (angle_max-angle_min < Math.PI)
	        return angle_min <= value && value <= angle_max;
	    if (value < 0)
	        return angle_max-2*Math.PI <= value && value <= angle_min;
	    return angle_max-2*Math.PI <= value-2*Math.PI && value-2*Math.PI <= angle_min;
	}

	public static boolean is_between_anglesHai(double value, double beta, double alpha) {
	    return Math.abs(angle(beta - value)) <= alpha;
	}

	private static final double SMALL_NUM = 0.00000001D;

	public static double closestDistanceBetweenLinesHai(Point a0, Point a1, Point b0, Point b1) {
		Point u = a1.sub(a0);
		Point v = b1.sub(b0);
		Point w = a0.sub(b0);

		double a = u.dot(u);
		double b = u.dot(v);
		double c = v.dot(v);
		double d = u.dot(w);
		double e = v.dot(w);
		double D = a * c - b * b;
		double sc = D, sN = D, sD = D;
		double tc = D, tN = D, tD = D;

		// compute the line parameters of the two closest points
		if (D < SMALL_NUM) { //  the lines are almost parallel
			sN = 0.0; // force using point P0 on segment S1
			sD = 1.0; // to prevent possible division by 0.0 later
			tN = e;
			tD = c;
		} else {// get the closest points on the infinite lines
			sN = (b * e - c * d);
			tN = (a * e - b * d);
			if (sN < 0.0) { // sc < 0 = > the s=0 edge is visible
				sN = 0.0;
				tN = e;
				tD = c;
			} else if (sN > sD) { // sc > 1  = > the s=1 edge is visible
				sN = sD;
				tN = e + b;
				tD = c;
			}
		}

		if (tN < 0.0) { // tc < 0 = > the t=0 edge is visible
			tN = 0.0;
			// recompute sc for this edge
			if (-d < 0.0)
				sN = 0.0;
			else if (-d > a)
				sN = sD;
			else {
				sN = -d;
				sD = a;
			}
		} else if (tN > tD) {// tc > 1  = > the t=1 edge is visible
			tN = tD;
			// recompute sc for this edge
			if ((-d + b) < 0.0)
				sN = 0;
			else if ((-d + b) > a)
				sN = sD;
			else {
				sN = (-d + b);
				sD = a;
			}
		}

		// finally do the division to get sc and tc
		sc = Math.abs(sN) < SMALL_NUM ? 0 : sN / sD;
		tc = Math.abs(tN) < SMALL_NUM ? 0 : tN / tD;

		return w.add(u.mul(sc)).sub(v.mul(tc)).r(); // return the closest distance
	}

	private static final double SMALL_ANGLE = 0.0001D;

	public static boolean is_point_in_fan(Point point, Point center, double beta, double r, double alpha) {
		if (point.sub(center).r() > r)
			return false;
		double phi = Math.atan((point.y-center.y)/(point.x-center.x));
		if (point.y-center.y < 0)
			phi += Math.PI;
		if (alpha >= Math.PI - SMALL_ANGLE) // de phong sai so truong hop gan voi pi (Tuc ca duong tron)
			return true;
		double arg_min = Math.min((beta - alpha)%(2*Math.PI), (beta + alpha)%(2*Math.PI));
		double arg_max = Math.max((beta - alpha)%(2*Math.PI), (beta + alpha)%(2*Math.PI));

		return arg_min <= phi%(2*Math.PI) && phi%(2*Math.PI) <= arg_max;
	}

	public static double closest_distance_between_point2arc(Point point, Point center, double beta, double r, double alpha) {
		if (is_point_in_fan(point, center, beta, r, alpha))
	        return 0;
	        // return [None, None, 0]

	    // intersection of radius and arc
	    double t = r / point.sub(center).r();
	    Point u = point.sub(center);
	    double phi = arctan(u.y, u.x);

	    if (is_between_anglesHai(phi, beta, alpha))
	        return Math.abs(t-1)*point.sub(center).r();
	    if (is_between_anglesHai(phi + Math.PI, beta, alpha))
	        return Math.abs(t+1)*point.sub(center).r();

		Point end_point1 = center.add(Point.rp(r, beta-alpha));
	    Point end_point2 = center.add(Point.rp(r, beta+alpha));
	    return Math.min(point.sub(end_point1).r(), point.sub(end_point2).r());
	}

	public static double closesDistanceBetweenArcsHai(Point center1, Point center2, double r1, double r2, double beta1, double beta2, double alpha1, double alpha2) {
		// arc1
		Point endpoint1 = center1.add(Point.rp(r1, beta1-alpha1));
		Point endpoint2 = center1.add(Point.rp(r1, beta1+alpha1));

		// arc2
		Point endpoint3 = center2.add(Point.rp(r2, beta2-alpha2));
		Point endpoint4 = center2.add(Point.rp(r2, beta2+alpha2));

		List<Double> res = new ArrayList<>();
		res.add(closest_distance_between_point2arc(endpoint1, center2, beta2, r2, alpha2));
		res.add(closest_distance_between_point2arc(endpoint2, center2, beta2, r2, alpha2));
		res.add(closest_distance_between_point2arc(endpoint3, center1, beta1, r1, alpha1));
		res.add(closest_distance_between_point2arc(endpoint4, center1, beta1, r1, alpha1));

		Point u = center2.sub(center1);
		double phi = arctan(u.y, u.x);

		if (is_between_anglesHai(angle(phi+Math.PI), beta2, alpha2) && is_between_anglesHai(phi, beta1, alpha1)) {
			res.add(Math.max(closest_distance_between_point2arc(center2, center1, beta1, r1, alpha1) - r1, 0));
			res.add(Math.max(closest_distance_between_point2arc(center1, center2, beta2, r2, alpha2) - r2, 0));
		}

		return Collections.min(res);
	}

	public static double closestDistanceBetweenArcAndLine(Point a0, Point a1, Point center, double beta, double r, double alpha) {
		// a0 a1 la 2 dau mut cua doan thang
		// center beta r alpha la thong so cua day cung

		List<Double> res = new ArrayList<>();

		// ends
	    Point endpoint1 = center.add(Point.rp(r, beta-alpha));
	    Point endpoint2 = center.add(Point.rp(r, beta+alpha));

	    res.add(a0.sub(endpoint1).r());
	    res.add(a0.sub(endpoint2).r());
	    res.add(a1.sub(endpoint1).r());
	    res.add(a1.sub(endpoint2).r());

	    // interiors
	    // x = (a0*(a1-b1)**2+c0*(b0-a0)**2+(a1-b1)*(b0-a0)*(c1-a1)) / ((a1-b1)**2+(a0-b0)**2)
	    // y = c1-(c0-x)(b0-a0)/(a1-b1)

	    double x = (a0.x * (a0.y - a1.y) * (a0.y - a1.y) + center.x * (a1.x - a0.x) * (a1.x - a0.x) + (a0.y - a1.y) * (a1.x - a0.x) * (center.y - a0.y)) / ((a0.y - a1.y) * (a0.y - a1.y) + (a0.x - a1.x) * (a0.x - a1.x));
	    double y = center.y - (center.x - x)*(a1.x - a0.x) / (a0.y - a1.y);
	    double d = new Point(x, y).sub(center).r() - r;
	    double phi = arctan(y - center.y, x - center.x);

	    if (x - center.x == 0)
	        phi = Math.signum(y - center.y) * Math.PI/2;
	    if ((a0.x <= x && x <= a1.x || a1.x <= x && x <= a0.x) && (a0.y <= y && y <= a1.y || a1.y <= y && y <= a0.y) && is_between_angles(phi, angle(beta -alpha), angle(beta + alpha))) {
	        if (d > 0)
	            res.add(d);
	        else
	            res.add(0D); // intersection
	    }

	    // end of arc to interior of line
	    x = (a0.x * (a0.y - a1.y) * (a0.y - a1.y) + endpoint1.x * (a1.x - a0.x) * (a1.x - a0.x) + (a0.y - a1.y) * (a1.x - a0.x) * (endpoint1.y - a0.y)) / ((a0.y - a1.y) * (a0.y - a1.y) + (a0.x - a1.x) * (a0.x - a1.x));
	    y = endpoint1.y - (endpoint1.x - x)*(a1.x - a0.x) / (a0.y - a1.y);
	    if ((a0.x <= x && x <= a1.x || a1.x <= x && x <= a0.x) && (a0.y <= y && y <= a1.y || a1.y <= y && y <= a0.y))
	        res.add(new Point(x, y).sub(endpoint1).r());

	    x = (a0.x * (a0.y - a1.y) * (a0.y - a1.y) + endpoint2.x * (a1.x - a0.x) * (a1.x - a0.x) + (a0.y - a1.y) * (a1.x - a0.x) * (endpoint2.y - a0.y)) / ((a0.y - a1.y) * (a0.y - a1.y) + (a0.x - a1.x) * (a0.x - a1.x));
	    y = endpoint2.y - (endpoint2.x - x)*(a1.x - a0.x) / (a0.y - a1.y);
	    if ((a0.x <= x && x <= a1.x || a1.x <= x && x <= a0.x) && (a0.y <= y && y <= a1.y || a1.y <= y && y <= a0.y))
	        res.add(new Point(x, y).sub(endpoint2).r());

	    // end of line to interior of arc
	    double phi0 = arctan(a0.y - center.y, a0.x - center.x);
	    if (a0.x - center.x == 0)
	        phi0 = Math.signum(a0.y - center.y)*Math.PI/2;
	    double phi1 = arctan(a1.y - center.y , a1.x - center.x);
	    if (a1.x - center.x == 0)
	        phi1 = Math.signum(a1.y - center.y)*Math.PI/2;
	    d = a0.sub(center).r() - r;
	    if (is_between_angles(phi0, angle(beta -alpha), angle(beta + alpha))) {
	        if (d > 0)
	            res.add(d);
	        else
	            res.add(0D);
	    }
	    d = a1.sub(center).r() - r;
	    if (is_between_angles(phi1, angle(beta -alpha), angle(beta + alpha))) {
	        if (d > 0)
	            res.add(d);
	        else
	            res.add(0D);
	    }

	    // intersection
	    // A = 1+(a1.y-a0.y)**2/(a1.x-a0.x)**2
	    // B = -2*center.x-(2*a0.x*(a1.y-a0.y)**2+2*(a1.y-a0.y)*(a0.y-center.y)*(a1.x-a0.x))/(a1.x-a0.x)**2
	    // C = center.x*2+(a0.x**2*(a1.y-a0.y)**2+(a0.y-center.y)**2*(a1.x-a0.x)**2-2*a0.x*(a1.y-a0.y)*(a0.y-center.y)*(a1.x-a0.x))/(a1.x-a0.x)**2
	    // delta = B**2-4*A*C
	    // if delta >= 0:
	    //     res.append(0)

	    return Collections.min(res);
	}

	public static double minimum__sectors_distance(Sensor sensor1, Sensor sensor2) {
		// k/c 2 sensor = min(khoang cach canh-canh, canh-cung, cung-cung)
	    List<Double> resultarray = new ArrayList<>();

	    Point a0 = sensor1.getCircle();
	    Point a1 = sensor1.getCircle().add(Point.rp(sensor1.r, sensor1.beta - sensor1.alpha));
	    Point a2 = sensor1.getCircle().add(Point.rp(sensor1.r, sensor1.beta + sensor1.alpha));
	    Point b0 = sensor2.getCircle();
	    Point b1 = sensor2.getCircle().add(Point.rp(sensor2.r, sensor2.beta - sensor2.alpha));
	    Point b2 = sensor2.getCircle().add(Point.rp(sensor2.r, sensor2.beta + sensor2.alpha));

	    resultarray.add(closestDistanceBetweenLinesHai(a0, a1, b0, b1));
	    resultarray.add(closestDistanceBetweenLinesHai(a0, a1, b0, b2));
	    resultarray.add(closestDistanceBetweenLinesHai(a0, a2, b0, b1));
	    resultarray.add(closestDistanceBetweenLinesHai(a0, a2, b0, b2));
	    resultarray.add(closesDistanceBetweenArcsHai(a0, b0, sensor1.r, sensor2.r, sensor1.beta, sensor2.beta, sensor1.alpha, sensor2.alpha));
	    resultarray.add(closestDistanceBetweenArcAndLine(a0, a1, b0, sensor2.beta, sensor2.r, sensor2.alpha));
	    resultarray.add(closestDistanceBetweenArcAndLine(a0, a2, b0, sensor2.beta, sensor2.r, sensor2.alpha));
	    resultarray.add(closestDistanceBetweenArcAndLine(b0, b1, a0, sensor1.beta, sensor1.r, sensor1.alpha));
	    resultarray.add(closestDistanceBetweenArcAndLine(b0, b2, a0, sensor1.beta, sensor1.r, sensor1.alpha));

	    return Collections.min(resultarray);
	}

}
