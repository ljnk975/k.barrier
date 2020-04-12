package k.barrier.util;

import java.util.Random;

public class RandomUtil {

	private static Random r = new Random();

	public static double randX() {
		double rand;
		do {
			rand = r.nextDouble();
		} while (rand == 0 || rand == 0.25 || rand == 0.5 || rand == 0.75);
		return rand;
	}

	public static double nextDouble() {
		return r.nextDouble();
	}

	public static int nextInt(int bound) {
		return r.nextInt(bound);
	}

}
