package k.barrier.algorithm.gsa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.Data;
import k.barrier.data.WBG.Node;
import k.barrier.model.Barrier;
import k.barrier.util.ChoiceUtil;

public class Solution {

	/**
	 * Size of individual gene
	 */
	public int size;

	/**
	 * Sensor probadlity
	 */
	public double[] genes;

	/**
	 * k-barrier obtained
	 */
	public Barrier[] barriers;

	/**
	 * Current fitness
	 */
	public int fitness;

	/**
	 * Algorithm
	 */
	protected Algorithm alg;

	protected Solution(Algorithm alg) {
		this.alg   = alg;
		this.size  = alg.getInput().getListSensor().size()+1+alg.getInput().getK();
		this.genes = new double[size];
		this.fitness  = -1;
	}

	public Solution(Algorithm alg, double[] genes) {
		this(alg);
		for (int i = 0; i < size; i++)
			this.genes[i] = genes[i];
		this.callFitness();
	}

	public Solution(Solution s) {
		this(s.alg);
		this.copy(s);
	}

	public void copy(Solution s) {
		for (int i = 0; i < size; i++)
			this.genes[i] = s.genes[i];
		this.fitness  = s.fitness;
		this.barriers = s.barriers;
	}

	public void setBarrier(List<Barrier> barriers) {
		this.barriers = new Barrier[alg.getInput().getK()];
		for (int i = 0; i < alg.getInput().getK(); i++)
			this.barriers[i] = barriers.get(i);
	}

	public void obtainKBarrier() {
		Data d = alg.getInput();

		this.barriers = new Barrier[alg.getInput().getK()];

		List<Integer> selects = IntStream.range(1, genes.length).boxed().sorted((a1, a2) -> genes[a1]-genes[a2] < 0 ? 1 : genes[a1]==genes[a2] ? 0 : -1).collect(Collectors.toList());
		int k = 0;
		for (int i = 0; i < d.getK(); i++) {
			List<Node> path = new ArrayList<>(); path.add(d.getWBG().getNode(0));

			while(true) {
				int s = selects.get(k++);
				if (s > d.getListSensor().size())
					break;
				path.add(d.getWBG().getNode(s));
			}
			path.add(d.getWBG().getNode(1));
			
			barriers[i] = d.getWBG().barrierFromPath(path);
		}
	}

	public int callFitness() {
		obtainKBarrier();

		fitness = 0;
		for (Barrier b : barriers)
			fitness += b.getLength();
		
		return fitness;
	}

	public void obtainKBarrier2() {
		Data d = alg.getInput();

		this.barriers = new Barrier[alg.getInput().getK()];

		List<Integer> selects = new ArrayList<>();

		for (int i = 0; i < d.getK(); i++) {
			List<Node> path = new ArrayList<>(); path.add(d.getWBG().getNode(0));

			while(true) {
				int s = ChoiceUtil.rouletteChoice(genes, selects);
				if (s > d.getListSensor().size())
					break;
				path.add(d.getWBG().getNode(s));
			}
			path.add(d.getWBG().getNode(1));
			
			barriers[i] = d.getWBG().barrierFromPath(path);
		}
	}

	public int callFitness2() {
		obtainKBarrier2();

		fitness = 0;
		for (Barrier b : barriers)
			fitness += b.getLength();
		
		return fitness;
	}

}
