package k.barrier.algorithm.ipso;

import static k.barrier.algorithm.ipso.Config.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import k.barrier.algorithm.Algorithm;
import k.barrier.algorithm.gsa.Solution;
import k.barrier.util.RandomUtil;

public class IPSO extends Algorithm {

	public IPSO() {
		super("IPSO");
	}

	private static List<Individual> rouletteChoice(List<Individual> input, List<Double> probadly, int number) {
		double sum = probadly.stream().mapToDouble(d -> d).sum();
		List<Individual> result = new ArrayList<Individual>();
		for (int i = 0; i < number; i++) {
			double roll = sum*RandomUtil.nextDouble();
			double s = 0; int j;
			for (j = 0; j < probadly.size(); j++) {
				if (j == probadly.size()-1 || s < roll && roll < (s=s+probadly.get(j)))
					break;
			}
			result.add(new Individual(input.get(j)));
		}
		return result;
	}

	@Override
	protected void doAlgorithm() {
		// Comparator
		Comparator<Individual> compareFitness = (ind1, ind2) -> ind1.fitness-ind2.fitness;

		// Step 2: Encoding
		List<Individual> population = new ArrayList<Individual>();
		for (int i = 0; i < POPNUM; i++)
			population.add(Individual.random(this));

		// Gbest
		Solution gbest = new Solution(Collections.min(population, compareFitness));

		// Main loop
		for (int i = 0; i < ITER_MAX; i++) {
			// New population
			List<Individual> newPopulation;

			double min_aff = Double.MAX_VALUE;
			double max_aff = Double.MIN_VALUE;

			// Step 3, 4: Calc affinity
			for (int j = 0; j < population.size(); j++) {
				Individual ind = population.get(j);
				if (ind.affinity < min_aff)
					min_aff = ind.affinity;
				if (ind.affinity > max_aff)
					max_aff = ind.affinity;
			}

			// Sort affinity
			Collections.sort(population, (ind1, ind2) -> {
				return ind2.affinity-ind1.affinity > 0 ? 1 : ind1.affinity==ind2.affinity ? 0 : -1;
			});

			// Step 5: Antibody library
			List<Individual> antibody = population.stream()
				.limit(population.size()/5)
//				.map(ind -> new Individual(ind))
				.collect(Collectors.toList());

			// Step 6: Clone
			// Add to population
			List<Individual> antibodyGroup = new LinkedList<>();
			antibodyGroup.addAll(population);

			for (int j = 0, num = antibody.size()/2; j < num; j++)
				antibodyGroup.add(antibody.get(RandomUtil.nextInt(antibody.size())));

			// Calc density
			double part = (max_aff-min_aff) / M; double min = min_aff;
			antibodyGroup.stream()
				.collect(Collectors.groupingBy(ind -> (int)((ind.affinity-min)/part)))
				.entrySet()
				.forEach(e -> {
					e.getValue().forEach(ind -> ind.density = e.getValue().size()/M);
				});

			// Calc pd and pf
			double sumD = antibodyGroup.stream().mapToDouble(ind -> ind.density).sum();
			double sumF = antibodyGroup.stream().mapToDouble(ind -> ind.fitness).sum();

			List<Double> probadly = antibodyGroup.stream().map(ind -> {
				double pd = 1-ind.density/sumD;
				double pf = 1-ind.fitness/sumF;
				return alpha*pf+(1-alpha)*pd;
			}).collect(Collectors.toList());

			newPopulation = rouletteChoice(antibodyGroup, probadly, POPNUM);
			
			// Step 6: Crossover
			// Select 30% lowest
			List<Individual> selected = population.stream()
				.skip(population.size()*7/10)
				.collect(Collectors.toList());

			// Do crossover
			for (int j = 0; j < selected.size()/2; j++) {
				Individual father = selected.get(j);
				Individual mother = selected.get(selected.size()-j-1);

				double[] genes1 = new double[father.genes.length];
				double[] genes2 = new double[father.genes.length];

				// Selected two point
				int p1 = RandomUtil.nextInt(genes1.length-1);
				int p2 = p1+1+RandomUtil.nextInt(genes1.length-1-p1);

				int k;
				for (k = 0; k < p1; k++) {
					genes1[k] = father.genes[k];
					genes2[k] = mother.genes[k];
				}
				
				for (; k < p2; k++) {
					genes1[k] = mother.genes[k];
					genes2[k] = father.genes[k];
				}

				for (; k < genes1.length; k++) {
					genes1[k] = father.genes[k];
					genes2[k] = mother.genes[k];
				}

				newPopulation.add(new Individual(this, genes1, new double[genes1.length]));
				newPopulation.add(new Individual(this, genes2, new double[genes1.length]));
			}

			// Step 7: Mutation
			selected.forEach(ind -> {
				ind.rand1 = 4*ind.rand1*(1-ind.rand1);
				ind.rand2 = 4*ind.rand2*(1-ind.rand2);
			});

			// Add to population
			newPopulation.addAll(selected);

			// Set new population
			population = newPopulation;

			// Step 8: PSO
			population.forEach(ind -> {
				// Rand j
				double rand1 = ind.rand1;
				double rand2 = ind.rand2;

				for (int j = 0; j < ind.genes.length; j++) {
					ind.velocity[j] = ind.velocity[j]+c1*rand1*(ind.pbest.genes[j]-ind.genes[j])+c2*rand2*(gbest.genes[j]-ind.genes[j]);
					ind.genes[j] = ind.genes[j]+ind.velocity[j];
				}
				ind.callFitness();
				if (ind.fitness < ind.pbest.fitness)
					ind.pbest = new Solution(ind);
				if (ind.fitness < gbest.fitness)
					gbest.copy(ind);
			});
			
			// Step 9:
			System.out.println("Iter "+i+": gbest="+gbest.fitness);
		}

		this.Nm = gbest.fitness;
		this.barriers = gbest.barriers;
		
		System.out.print("Solution "+gbest.fitness+" [");
		for (int i = 0; i < gbest.genes.length; i++)
			System.out.print(gbest.genes[i]+", ");
		System.out.println("]");
		for (int i = 0; i < d.getK(); i++)
			System.out.println(gbest.barriers[i]);
	}

	public static void main(String args[]) {
		run(new IPSO(), 1);
	}

}
