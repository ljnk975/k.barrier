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
	 * Gene
	 */
	protected int[] genes;

	/**
	 * K
	 */
	protected int[] k;

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
		this.size = alg.getInput().getListSensor().size();
		this.fitness = -1;
	}

	public Individual(Algorithm alg, int[] genes, int[] k) {
		this(alg);
		this.genes = genes;
		this.k = k;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append(this.fitness+" k=[");
		for (int i = 0; i < this.k.length-1; i++)
			buff.append(this.k[i]+", ");
		buff.append(this.k[this.k.length-1]+"] genes=[");
		for (int i = 0; i < this.genes.length-1; i++)
			buff.append(this.genes[i]+", ");
		buff.append(this.genes[this.genes.length-1]+"]");
		return buff.toString();
	}

	public static Individual random(Algorithm alg) {
		int[] genes = new int[alg.getInput().getListSensor().size()];
		int[] ks = new int[alg.getInput().getK()+1];

		for (int gi = 0; gi < genes.length; gi++) {
			int next = -1;
			loop1:
			while(true) {
				next = RandomUtil.nextInt(genes.length);
				for (int j = 0; j < gi; j++)
					if (genes[j] == next)
						continue loop1;
				break;
			}
			genes[gi] = next;
		}

		WBG wbg = alg.getInput().getWBG();
		int ki = 1, g = 0;
		for (int gi = 0; gi < genes.length; gi++) {
			if (ki >= ks.length)
				break;
			if (wbg.getWeight(g, 1) < wbg.getWeight(g, genes[gi]))
				ks[ki++] = gi;
			g = genes[gi];
		}

		for (; ki < ks.length; ki++)
			ks[ki] = ks[ki-1]+RandomUtil.nextInt(genes.length+1-ks[ki-1]);
		
		return new Individual(alg, genes, ks);
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

		int[] genes = new int[alg.getInput().getListSensor().size()];
		int[] ks = new int[alg.getInput().getK()+1];

		int gi = 0, ki = 1;

		for (List<Node> path : list) {
			for (Node s : path) {
				if (s.index == 0 || s.index == 1)
					continue; 
				genes[gi++] = s.index;
			}
			ks[ki] = ks[ki-1]+path.size()-2; ki++;
		}

		for (; gi < genes.length; gi++) {
			int next = -1;
			loop1:
			while(true) {
				next = RandomUtil.nextInt(genes.length);
				for (int j = 0; j < gi; j++)
					if (genes[j] == next)
						continue loop1;
				break;
			}
			genes[gi] = next;
		}

		for (; ki < ks.length; ki++)
			ks[ki] = ks[ki-1];
		
		return new Individual(alg, genes, ks);
	}

	protected void obtainKBarrier() {
		Data d = alg.getInput();
		WBG wbg = d.getWBG();

		this.barriers = new Barrier[d.getK()];

		for (int i = 0; i < d.getK(); i++) {
			List<Node> path = new ArrayList<>(); path.add(wbg.getNode(0));

			for (int j = k[i]; j < k[i+1]; j++)
				path.add(wbg.getNode(genes[j]));

			path.add(wbg.getNode(1));

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

	protected Individual callFitness() {
		if (fitness < 0) {
			obtainKBarrier();

			fitness = 0;
			for (Barrier b : barriers)
				fitness += b.getLength();
		}
		return this;
	}

}
