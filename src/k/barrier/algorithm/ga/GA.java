package k.barrier.algorithm.ga;

import static k.barrier.algorithm.ga.Config.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.WBG.Node;
import k.barrier.model.Barrier;
import k.barrier.util.RandomUtil;

public class GA extends Algorithm {

	public GA() {
		super("GA");
	}

	protected List<Individual> init() {
		int i = 0;

		List<Individual> population = new ArrayList<Individual>();
//		for (; i < POPNUM/5; i++)
//			population.add(Individual.greedy(this, true));
		for (; i < POPNUM; i++)
			population.add(Individual.random(this));

		return population;
	}

	protected Individual oxCrossover(Individual ind, Individual ind2) {
		// Lai ghep
		int[] genes = new int[ind.size];

		int p1 = RandomUtil.nextInt(genes.length-1), p2 = p1+1+RandomUtil.nextInt(genes.length-p1);

		int j = 0, k = 0;

		int nk = 0;
		boolean[] selected = new boolean[d.getListSensor().size()];

		for (j = p1; j < p2; j++) {
			genes[j] = ind.genes[j];
			if (ind.genes[j] == 1)
				nk++;
			else
				selected[ind.genes[j]-2] = true;
		}

		for (j = 0, k = 0; j < p1; j++) {
			int next = -1;
			while(true) {
				next = ind2.genes[k]; k = (k+1) % ind.size;
				if (next == 1) {
					if (nk < d.getK()) {
						nk++;
						break;
					}
				} else if (!selected[next-2]) {
					selected[next-2] = true;
					break;
				}
			}
			genes[j] = next;
		}

		for (j = p2; j < ind.size; j++) {
			int next = -1;
			while(true) {
				next = ind2.genes[k]; k = (k+1) % ind.size;
				if (next == 1) {
					if (nk < d.getK()) {
						nk++;
						break;
					}
				} else if (!selected[next-2]) {
					selected[next-2] = true;
					break;
				}
			}
			genes[j] = next;
		}

		return new Individual(this, genes);
	}

	protected Individual oxkCrossover(Individual ind, Individual ind2) {
		ind.callFitness();

		// Lai ghep
		int[] genes = new int[ind.size];

		int k1 = RandomUtil.nextInt(d.getK()-1), k2 = k1+1+RandomUtil.nextInt(d.getK()-k1);

		int j = 0, k = 0;

		int nk = 0;
		boolean[] selected = new boolean[d.getListSensor().size()];

		for (int i = k1; i < k2; i++) {
			Barrier b = ind.barriers[i];
			for (int l = 1; l < b.size(); l++) {
				int s = b.get(l).index;
				genes[j++] = s;
				if (s != 1)
					selected[s-2] = true;
			}
			nk++;
		}

		for (; j < ind.size; j++) {
			int next = -1;
			while(true) {
				next = ind2.genes[k]; k = (k+1) % ind.size;
				if (next == 1) {
					if (nk < d.getK()) {
						nk++;
						break;
					}
				} else if (!selected[next-2]) {
					selected[next-2] = true;
					break;
				}
			}
			genes[j] = next;
		}

		return new Individual(this, genes);
	}

	/**
	 * Twors Mutation
	 * @param ind
	 * @return
	 */
	protected Individual tworsMutation(Individual ind) {
		int[] genes = new int[ind.size];

		for (int i = 0; i < ind.size; i++)
			genes[i] = ind.genes[i];

		// Diem dot bien
		int k = RandomUtil.nextInt(ind.size-1), l = k+1+RandomUtil.nextInt(ind.size-k-1);

		genes[k] = ind.genes[l];
		genes[l] = ind.genes[k];

		return new Individual(this, genes).callFitness();
	}

	/**
	 * Centre inverse mutation
	 * @param ind
	 * @return
	 */
	protected Individual cimMutation(Individual ind) {
		int[] genes = new int[ind.size];

		// Diem dot bien
		int k = RandomUtil.nextInt(ind.size-1);

		for (int i = 0; i < k; i++)
			genes[i] = ind.genes[k-i-1];

		for (int i = k; i < ind.size; i++)
			genes[i] = ind.genes[ind.size+k-i-1];

		return new Individual(this, genes);
	}

	/**
	 * Reverse sequence mutation
	 * @param ind
	 * @return
	 */
	protected Individual rsmMutation(Individual ind) {
		int[] genes = new int[ind.size];

		for (int i = 0; i < ind.size; i++)
			genes[i] = ind.genes[i];

		// Diem dot bien
		int k = RandomUtil.nextInt(ind.size-1), l = k+1+RandomUtil.nextInt(ind.size-k-1);

		for (int i = k; i <= l; i++)
			genes[i] = ind.genes[l+k-i];

		return new Individual(this, genes);
	}

	protected Individual mutation(Individual ind) {
		int[] genes = new int[ind.size];

		Barrier[] barriers = new Barrier[d.getK()];
		List<Node> list = new ArrayList<Node>();

		int i, j, k, fitness = 0;
		for (i = 0, j = 0, k = 0; i < d.getK(); i++) {
			for (; j < ind.size; j++) {
				if (ind.genes[j] == 1)
					break;
				list.add(d.getWBG().getNode(ind.genes[j]));
			}

			//
			List<Node> path = d.getWBG().dijkstra(list);

			// Copy first
			for (Node s : path) {
				if (s.index == 0)
					continue;
				genes[k++] = s.index;
				list.remove(s);
			}
			
			// barrier
			barriers[i] = d.getWBG().barrierFromPath(path);
			fitness += barriers[i].getLength();
		}

		Individual ind2 = new Individual(this, genes, k);
		ind2.barriers = barriers;
		ind2.fitness = fitness;
		return ind2;

/*
		int k = RandomUtil.nextInt(ind.size), i, nk = 0;

		for (i = 0; i < k; i++) {
			genes[i] = ind.genes[i];
			if (ind.genes[i] == 1)
				nk++;
			else
				d.getWBG().remove(d.getWBG().getNode(ind.genes[i]));
		}

		if (k > 0 && ind.genes[k-1] != 1 && nk < d.getK()) {
			genes[i++] = 1;
			nk++;
		}

		if (nk < d.getK()) {
			// 
			List<Node> list = d.getWBG().dijkstra();

			// Copy first
			for (Node s : list) {
				if (s.index == 0)
					continue;
				genes[i++] = s.index;
			}
		}

		d.getWBG().reset();

		return new Individual(this, genes, i);
*/
	}

	// Comparator
	private Comparator<Individual> compareFitness = (ind1, ind2) -> ind1.fitness-ind2.fitness;

	protected List<Individual> selection(List<Individual> oldPopulation) {
		return oldPopulation.stream()
				.sorted(compareFitness)
				.limit(POPNUM)
				.collect(Collectors.toList());
	}

	protected List<Individual> rolletSelection(List<Individual> oldPopulation) {
		double sum = oldPopulation.stream().mapToDouble(p -> 1F/p.fitness).sum();
		boolean[] selected = new boolean[oldPopulation.size()];

		List<Individual> population = new ArrayList<Individual>();

		Individual gbest = Collections.min(oldPopulation, compareFitness);
		population.add(gbest);
		for (int j = 0; j < oldPopulation.size(); j++) {
			if (oldPopulation.get(j) == gbest) {
				selected[j] = true;
				sum -= 1F/gbest.fitness;
				break;
			}
		}

		for (int j = 0; j < POPNUM-1; j++) {
			double rand = RandomUtil.nextDouble()*sum; double p = 0;
			for (int k = 0; k < oldPopulation.size(); k++) {
				if (selected[k])
					continue;
				Individual ind = oldPopulation.get(k);
				if (p < rand && rand < p+1F/ind.fitness) {
					population.add(ind); selected[k] = true; sum -= 1F/ind.fitness;
					break;
				}
				p += 1F/ind.fitness;
			}
		}
		
		return population;
	}

	@Override
	protected void doAlgorithm() {
		// Encoding
		List<Individual> population = init();

		// Print
		System.out.println("Inited");

		int nloop = 0;
		int currFitness = -1;

		// Main loop
		for (int i = 0; i < ITER_MAX && nloop < L_MAX && currFitness != 0; i++) {
			List<Individual> ppopulation = population;

			System.out.println("Crossover");

			// Crossover
			List<Individual> childs = new ArrayList<Individual>();
			population.forEach(ind -> {
				// Check lai ghep
				if (RandomUtil.nextDouble() < CROSS_RATE) {
					// Chon me
					Individual ind2 = null;
					do {
						ind2 = ppopulation.get(RandomUtil.nextInt(ppopulation.size()));
					} while (ind2 == ind);

					childs.add(oxkCrossover(ind, ind2));
					childs.add(oxkCrossover(ind2, ind));
				}
			});

			System.out.println("Mutation");

			// Mutation
			List<Individual> mutas = new ArrayList<Individual>();
			childs.forEach(ind -> {
				// Check dot bien
				if (RandomUtil.nextDouble() < MUTAT_RATE)
					mutas.add(rsmMutation(ind));
				if (RandomUtil.nextDouble() < MUTAT_RATE)
					mutas.add(mutation(ind));
			});

			System.out.println("Selection");

			// Selection
			List<Individual> list = Stream.of(population, childs, mutas).flatMap(Collection::stream)
					.map(ind -> ind.callFitness())
					.collect(Collectors.toList());

			population = selection(list);

			// Fitness
			if (currFitness < 0 || population.get(0).fitness < currFitness) {
				currFitness = population.get(0).fitness;
				nloop = 0;
			} else
				nloop++;

			// Print
			System.out.println("Iter "+i+": best="+currFitness);
			metaPs.println("Iter "+i+": best="+currFitness);
		}

		Individual best = population.get(0);
		this.Nm = best.fitness;
		this.barriers = best.barriers;

		System.out.print("Solution "+best);
		for (int i = 0; i < d.getK(); i++)
			System.out.println(best.barriers[i]);
	}

	public static void main(String args[]) {
		run(new GA(), 10);
	}

}
