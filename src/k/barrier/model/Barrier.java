package k.barrier.model;

import java.util.List;

/**
 * Define a barrier from source s to des t
 * 
 * @author ljnk975
 *
 */
public class Barrier {

	/**
	 * Sensor path
	 */
	private List<Sensor> path;

	/**
	 * Length of barrier
	 */
	private int length;

	/**
	 * 
	 * @param path
	 */
	public Barrier(List<Sensor> path, int length) {
		this.path = path;
		this.length = length;
	}

	/**
	 * Number of mobile sensor to obtain barrier
	 * @return the number of mobile sensor needed
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * Size of this path
	 * @return the size
	 */
	public int size() {
		return this.path.size();
	}

	/**
	 * @return the path
	 */
	public List<Sensor> getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(List<Sensor> path) {
		this.path = path;
	}

	/**
	 * Get the index-th Sensor
	 * @param index index of sensor
	 * @return the sensor
	 */
	public Sensor get(int index) {
		return this.path.get(index);
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder("Barrier[");

		for (int i = 0; i < path.size()-1; i++)
			buff.append(path.get(i).index).append(", ");

		return buff.append("1]").toString();
	}

}
