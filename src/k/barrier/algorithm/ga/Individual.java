package k.barrier.algorithm.ga;

import java.util.ArrayList;
import java.util.List;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.Data;
import k.barrier.data.WBG;
import k.barrier.data.WBG.Node;
import k.barrier.model.Barrier;
import k.barrier.util.RandomUtil;

public class Individual {

	/**
	 * Algorithm
	 */
	protected Algorithm alg;

	/**
	 * Size of individual gene
	 */
	public int size;

	/**
	 * Gene hoan vi
	 */
	public int[] genes;

	/**
	 * Current fitness
	 */
	public int fitness;

	/**
	 * k-barrier obtained
	 */
	public Barrier[] barriers;

	protected Individual(Algorithm alg) {
		this.alg  = alg;
		this.size = alg.getInput().getListSensor().size()+alg.getInput().getK();
		this.fitness = -1;
	}

	public Individual(Algorithm alg, int[] genes) {
		this(alg);
		this.genes = genes;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append(this.fitness+" genes=[");
		for (int i = 0; i < this.genes.length-1; i++)
			buff.append(this.genes[i]+", ");
		buff.append(this.genes[this.genes.length-1]+"]");
		return buff.toString();
	}

	public static Individual random(Algorithm alg) {
		int[] genes = new int[alg.getInput().getListSensor().size()+alg.getInput().getK()];

		int nk = 0;
		boolean[] selected = new boolean[alg.getInput().getListSensor().size()];
		for (int gi = 0; gi < genes.length; gi++) {
			int next = -1;
			while(true) {
				next = RandomUtil.nextInt(alg.getInput().getListSensor().size()+1)+1;
				if (next == 1) {
					if (nk < alg.getInput().getK()) {
						nk++;
						break;
					}
				} else if (!selected[next-2]) {
					selected[next-2] = true;
					break;
				}
			}
			genes[gi] = next;
		}

		return new Individual(alg, genes);
	}

	public static Individual greedy(Algorithm alg, boolean remove) {
		Data d = alg.getInput();

		List<List<Node>> list = new ArrayList<List<Node>>();

		if (remove) {
			int lent = RandomUtil.nextInt(d.getListSensor().size()/2);
			for (int i = 0; i < lent; i++)
				d.getWBG().remove(d.getWBG().getNode(2+RandomUtil.nextInt(d.getListSensor().size())));
		}

		for (int i = 0; i < d.getK(); i++) {
			List<Node> path = d.getWBG().dijkstra();
			if (path == null || d.getWBG().barrierFromPath(path).getLength() >= (int)(d.getL()/d.getLr()))
				break;
			path.forEach(s -> alg.getInput().getWBG().remove(s));
			list.add(path);
		}

		d.getWBG().reset();

		int[] genes = new int[alg.getInput().getListSensor().size()+alg.getInput().getK()];

		int gi = 0;

		int nk = 0;
		boolean[] selected = new boolean[alg.getInput().getListSensor().size()];

		for (List<Node> path : list) {
			for (Node s : path) {
				if (s.index == 0)
					continue;
				if (s.index == 1)
					nk++;
				else
					selected[s.index-2] = true;
				genes[gi++] = s.index;
			}
		}

		for (; gi < genes.length; gi++) {
			int next = -1;
			while(true) {
				next = RandomUtil.nextInt(alg.getInput().getListSensor().size()+1)+1;
				if (next == 1) {
					if (nk < alg.getInput().getK()) {
						nk++;
						break;
					}
				} else if (!selected[next-2]) {
					selected[next-2] = true;
					break;
				}
			}
			genes[gi] = next;
		}

		return new Individual(alg, genes);
	}

	protected void obtainKBarrier() {
		Data d = alg.getInput();
		WBG wbg = d.getWBG();

		this.barriers = new Barrier[d.getK()];

		for (int i = 0, j = 0; i < d.getK(); i++) {
			List<Node> path = new ArrayList<>(); path.add(wbg.getNode(0));

			while(genes[j] != 1) {
				path.add(wbg.getNode(genes[j])); j++;
			}

			path.add(wbg.getNode(1)); j++;

			barriers[i] = wbg.barrierFromPath(path);

//			Barrier barrier = wbg.barrierFromPath(path);
//
//			System.out.print("Barrier "+i+": [");
//			for (int j = 0; j < barrier.size()-1; j++)
//				System.out.print(barrier.get(j).getIndex()+", ");
//			System.out.println("1] Nm="+barrier.getLength());
//
//			barriers[i] = barrier;
		}
	}

	public Individual callFitness() {
		if (fitness < 0) {
			obtainKBarrier();

			fitness = 0;
			for (Barrier b : barriers)
				fitness += b.getLength();
		}
		return this;
	}

}
