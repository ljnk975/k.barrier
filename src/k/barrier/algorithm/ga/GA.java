package k.barrier.algorithm.ga;

import static k.barrier.algorithm.ga.Config.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import k.barrier.algorithm.Algorithm;
import k.barrier.util.RandomUtil;

public class GA extends Algorithm {

	public GA() {
		super("GA3");
	}

	protected List<Individual> init() {
		List<Individual> population = new ArrayList<Individual>();
//		int i = 0;
		int i = 1;
		population.add(Individual.greedy(this, false));
		for (; i < POPNUM/20; i++)
			population.add(Individual.greedy(this, true));
		for (; i < POPNUM; i++)
			population.add(Individual.random(this));
		return population;
	}

	protected Individual crossover(Individual ind, Individual ind2) {
		// Lai ghep
		int[] genes = new int[ind.size];

		int p1 = RandomUtil.nextInt(genes.length-1), p2 = p1+1+RandomUtil.nextInt(genes.length-p1);

		int nk = 0;
		boolean[] selected = new boolean[d.getListSensor().size()];

		int j, k;
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

	// TODO: dot bien chua thuc su tot
	protected Individual mutation(Individual ind) {
		// Dot bien
		int[] genes = new int[ind.size];

		int j;

		// Copy of ind
		for (j = 0; j < ind.size; j++)
			genes[j] = ind.genes[j];

		// Make mutation
		int p1 = RandomUtil.nextInt(genes.length-1), p2 = p1+1+RandomUtil.nextInt(genes.length-p1-1);
		int temp = genes[p1];
		genes[p1] = genes[p2];
		genes[p2] = temp;

		return new Individual(this, genes);
	}

	@Override
	protected void doAlgorithm() {
		// Comparator
		Comparator<Individual> compareFitness = (ind1, ind2) -> ind1.fitness-ind2.fitness;

		// Encoding
		List<Individual> population = init();

		// Print
		System.out.println("Inited");

		// Main loop
		for (int i = 0; i < ITER_MAX; i++) {
			// Old population
			List<Individual> oldPopulation = population;

			// Crossover
			List<Individual> childs = new ArrayList<Individual>();
			oldPopulation.forEach(ind -> {
				// Check lai ghep
				if (RandomUtil.nextDouble() < CROSS_RATE) {
					// Chon me
					Individual ind2 = null;
					do {
						ind2 = oldPopulation.get(RandomUtil.nextInt(oldPopulation.size()));
					} while (ind2 == ind);

					childs.add(crossover(ind, ind2));
					childs.add(crossover(ind2, ind));
				}
			});

			// Mutation
			List<Individual> mutas = new ArrayList<Individual>();
			childs.forEach(ind -> {
				// Check dot bien
				if (RandomUtil.nextDouble() < MUTAT_RATE)
					mutas.add(mutation(ind));
			});

			// Selection
			population = Stream.of(population, childs, mutas).flatMap(Collection::stream)
					.map(ind -> ind.callFitness())
					.sorted(compareFitness)
					.limit(POPNUM)
					.collect(Collectors.toList());

			// Print
			System.out.println("Iter "+i+": best="+population.get(0).fitness);
		}

		Individual best = population.get(0);
		this.Nm = best.fitness;
		this.barriers = best.barriers;

		System.out.print("Solution "+best);
		for (int i = 0; i < d.getK(); i++)
			System.out.println(best.barriers[i]);
	}

	public static void main(String args[]) {
		run(new GA(), 1);
	}

}
