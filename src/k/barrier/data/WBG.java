package k.barrier.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import k.barrier.model.Barrier;
import k.barrier.model.Sensor;
import k.barrier.util.Distance;

/**
 * Do thi wbg
 * 
 * @author ljnk975
 *
 */
public class WBG {

	/**
	 * Data
	 */
	private Data d;

	/**
	 * Size of sensors
	 */
	private int size;

	/**
	 * weighted barrier graph
	 */
	private int[][] wbg;

	/**
	 * List of node
	 */
	private Node[] nodes;

	// Node 
	public static class Node {

		public int index;
		public Sensor s;
		public boolean removed;

		Node(int index) {
			this.index = index;
		}

	}

	private WBG() {
	}

	/**
	 * Create WBG from input data
	 * @param d data input
	 */
	protected WBG(Data d) {
		this.d = d;

		this.buildWBG();

		// Print WBG
//		System.out.println("WBG: ");
//		for (int i = 0; i < wbg.length; i++) {
//			System.out.print("[");
//			for (int j = 0; j < wbg.length; j++)
//				System.out.format("%2d, ", wbg[i][j]);
//			System.out.println("]");
//		}
	}

	/**
	 * Create WBG from input data
	 * @param d data input
	 */
	private void buildWBG() {
		// Size of wbg
		size = d.getListSensor().size()+2;

		// List of node
		nodes = new Node[size];

		// Build node
		for (int i = 0; i < size; i++) {
			Node node = new Node(i);
			node.s = i == 0 ? Sensor.s : i == 1 ? Sensor.t : d.getListSensor().get(i-2);
			nodes[i] = node;
		}
		
		// The wbg
		this.wbg = new int[size][size];

		// Build wbg
		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++)
				this.wbg[j][i] = this.wbg[i][j] = Distance.w(nodes[i].s, nodes[j].s, d.getL(), d.getLr(), d.isWeak());
		}
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Get the i-th node
	 * @param index i
	 * @return
	 */
	public Node getNode(int index) {
		return nodes[index];
	}

	/**
	 * Get weight from node i and j
	 * @param ni
	 * @param nj
	 * @return
	 */
	public int getWeight(Node ni, Node nj) {
		if (ni.removed || nj.removed)
			return -1;
		return this.wbg[ni.index][nj.index];
	}

	/**
	 * Get weight from node i and j
	 * @param ni
	 * @param nj
	 * @return
	 */
	public int getWeight(int ni, int nj) {
		return getWeight(getNode(ni), getNode(nj));
	}

	/**
	 * Get the neighbour of the node
	 * @param s node s
	 * @return
	 */
	private List<Node> neighbours(Node s) {
		List<Node> list = new ArrayList<Node>();

		for (int i = 0; i < size; i++) {
			if (!nodes[i].removed)
				list.add(nodes[i]);
		}

		return list;
	}

	/**
	 * Remove node s from WBG
	 * @param s the node
	 */
	public void remove(Node s) {
		if (s.index == 0 || s.index == 1)
			return;
		s.removed = true;
	}

	/**
	 * Set all sensor unremoved
	 */
	public void reset() {
		for (Node s : nodes)
			s.removed = false;
	}

	/**
	 * Do dijktra algothirm find shortest path from s to t
	 * @return the dijktra path
	 */
	public List<Node> dijkstra() {
		boolean label[] = new boolean[size];
		double  dist[]  = new double[size];
		int     prev[]  = new int[size];

		int i;
		for (i = 0; i < prev.length; i++) {
			label[i] = true;
			dist[i]  = Double.MAX_VALUE;
			prev[i]  = -1;
		}

//		PriorityQueue<Node> q = new PriorityQueue<Node>((s1, s2) -> {
//			double s = dist[s1.index]-dist[s2.index];
//			return s > 0 ? 1 : s < 0 ? -1 : 0;
//		});

//		q.add(nodes[0]);
		dist[0] = 0;

		Node u; double alt;

		while (label[1]) {
//			// No path ? 
//			if (q.isEmpty())
//				return null;
//
//			// Min
//			u = q.remove();

			u = null;
			for (i = 0; i < size; i++) {
				if (label[i] && (u == null || dist[i] < dist[u.index]))
					u = nodes[i];
			}

			if (u == null || dist[u.index] == Double.MAX_VALUE)
				return null;

			// Mark u
			label[u.index] = false;

			List<Node> neighbours = neighbours(u);
			for (Node v : neighbours) {
				if (label[v.index] && (alt = dist[u.index]+getWeight(u, v)) < dist[v.index]) {
					dist[v.index] = alt;
					prev[v.index] = u.index;
//					if (!q.contains(v))
//						q.add(v);
				}
			}
		}

		List<Node> path = new LinkedList<Node>();
		u = nodes[1];
		
		do {
			path.add(0, u); u = nodes[prev[u.index]];
		} while (u.index != 0);
		path.add(0, nodes[0]);

		return path;
	}

	public Barrier directBarrier() {
		List<Sensor> list = new ArrayList<Sensor>();
		list.add(Sensor.s); list.add(Sensor.t);
		return new Barrier(list, getWeight(nodes[0], nodes[1]));
	}

	public Barrier barrierFromPath(List<Node> path) {
		List<Sensor> list = new ArrayList<Sensor>();
		Iterator<Node> it = path.iterator();
		Node s = it.next(); list.add(s.s); int length = 0;
		while (it.hasNext()) {
			Node t = it.next(); list.add(t.s); length += getWeight(s, t); s = t;
		}
		return new Barrier(list, length);
	}

	public WBG transform(List<List<Node>> list) {
		return new WBG();
	}

	public List<List<Node>> pathUpdate(List<List<Node>> list, List<Node> np) {
		return null;
	}

}


