package k.barrier.algorithm.ga;

import static k.barrier.algorithm.ga.Config.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.WBG;
import k.barrier.model.Barrier;
import k.barrier.util.RandomUtil;

public class GA extends Algorithm {

	public GA() {
		super("GA");
	}

	protected List<Individual> init() {
		List<Individual> population = new ArrayList<Individual>();
		population.add(Individual.greedy(this, false));
		int i = 1;
		for (; i < POPNUM/20; i++)
			population.add(Individual.greedy(this, true));
		for (; i < POPNUM; i++)
			population.add(Individual.random(this));
		return population;
	}

	protected Individual crossoverG(Individual ind, Individual ind2) {
		// Lai ghep
		int[] genes = new int[d.getListSensor().size()];
		int[] ks = new int[d.getK()+1];

		int p1 = RandomUtil.nextInt(genes.length-1), p2 = p1+1+RandomUtil.nextInt(genes.length-p1);
		int j, k;
		for (j = p1; j < p2; j++)
			genes[j] = ind.genes[j];
		for (j = 0, k = 0; j < p1; j++) {
			int next = -1;
			loop1:
			while(true) {
				next = ind2.genes[k]; k = (k+1) % ind.size;
				for (int l = 0; l < j; l++)
					if (genes[l] == next)
						continue loop1;
				for (int l = p1; l < p2; l++)
					if (genes[l] == next)
						continue loop1;
				break;
			}
			genes[j] = next;
		}

		for (j = p2; j < ind.size; j++) {
			int next = -1;
			loop1:
			while(true) {
				next = ind2.genes[k]; k = (k+1) % ind.size;
				for (int l = 0; l < j; l++)
					if (genes[l] == next)
						continue loop1;
				break;
			}
			genes[j] = next;
		}

		WBG wbg = getInput().getWBG();
		int ki = 1, g = 0;
		for (int gi = 0; gi < genes.length; gi++) {
			if (ki >= ks.length)
				break;
			if (wbg.getWeight(g, 1) < wbg.getWeight(g, genes[gi]))
				ks[ki++] = gi;
			g = genes[gi];
		}

		for (; ki < ks.length; ki++)
			ks[ki] = ks[ki-1];

		return new Individual(this, genes, ks);
	}

	protected Individual crossoverK(Individual ind, Individual ind2) {
		// Lai ghep
		int[] genes = new int[d.getListSensor().size()];
		int[] ks = new int[d.getK()+1];

		int p1 = RandomUtil.nextInt(ks.length-1), p2 = p1+1+RandomUtil.nextInt(ks.length-1-p1);
		int gi = 0, ki = 1, j, k;
		for (j = p1; j < p2; j++) {
			for (k = ind.k[j]; k < ind.k[j+1]; k++)
				genes[gi++] = ind.genes[k];
			ks[ki] = ks[ki-1]+ind.k[j+1]-ind.k[j]; ki++;
		}
		for (j = 0; gi < ind.size; gi++) {
			int next = -1;
			loop1:
			while(true) {
				next = ind2.genes[j]; j = (j+1) % ind.size;
				for (k = 0; k < gi; k++)
					if (genes[k] == next)
						continue loop1;
				break;
			}
			genes[gi] = next;
		}

		for (; ki < ks.length; ki++)
			ks[ki] = ks[ki-1]+RandomUtil.nextInt(genes.length+1-ks[ki-1]);

		return new Individual(this, genes, ks);
	}

	protected Individual mutation(Individual ind) {
		// Dot bien
		int[] genes = new int[d.getListSensor().size()];
		int[] ks = new int[d.getK()+1];

		int j;

		// Copy of ind
		for (j = 0; j < ind.size; j++)
			genes[j] = ind.genes[j];
		for (j = 0; j < ks.length; j++)
			ks[j] = ind.k[j];

		// Make mutation
		int p1 = RandomUtil.nextInt(genes.length-1), p2 = p1+1+RandomUtil.nextInt(genes.length-p1-1);
		int temp = genes[p1];
		genes[p1] = genes[p2];
		genes[p2] = temp;

		return new Individual(this, genes, ks);
	}

	protected Individual mutation2(Individual ind) {
		// Dot bien
		int[] genes = new int[d.getListSensor().size()];
		int[] ks = new int[d.getK()+1];

		int j;

		// Copy of ind
		for (j = 0; j < ind.size; j++)
			genes[j] = ind.genes[j];
		for (j = 0; j < ks.length; j++)
			ks[j] = ind.k[j];

		// Make mutation
		int directLent = d.getWBG().directBarrier().getLength();
		ind.callFitness();
		List<Barrier> list = new ArrayList<>();
		for (Barrier b : ind.barriers) {
			if (b.getLength() < directLent)
				list.add(b);
		}
		int k = 0, m = 1; j = 0;
		for (Barrier b : list) {
			for (int l = 1; l < b.size()-1; l++)
				genes[j++] = b.get(l).index;
			k += b.size()-2;
			ks[m++] = k;
		}

		for (k = 0; j < genes.length; j++) {
			int next = -1;
			loop1:
			while(true) {
				next = ind.genes[k]; k = (k+1) % ind.size;
				for (int l = 0; l < j; l++)
					if (genes[l] == next)
						continue loop1;
				break;
			}
			genes[j] = next;
		}

		for (; m < ks.length; m++)
			ks[m] = ks[m-1];

		return new Individual(this, genes, ks);
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

					if (RandomUtil.nextDouble() < 0.5) {
						childs.add(crossoverG(ind, ind2));
						childs.add(crossoverG(ind2, ind));
					} else {
						childs.add(crossoverK(ind, ind2));
						childs.add(crossoverK(ind2, ind));
					}
				}
			});

			// Mutation
			List<Individual> mutas = new ArrayList<Individual>();
			childs.forEach(ind -> {
				// Check dot bien
				if (RandomUtil.nextDouble() < MUTAT_RATE) {
//					if (RandomUtil.nextDouble() < 0.5)
						mutas.add(mutation(ind));
//					else 
//						mutas.add(mutation2(ind));
				}
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
