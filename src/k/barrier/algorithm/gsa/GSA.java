package k.barrier.algorithm.gsa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import k.barrier.algorithm.Algorithm;
import k.barrier.util.RandomUtil;

import static k.barrier.algorithm.gsa.Config.*;

public class GSA extends Algorithm {

	public GSA() {
		super("GSA");
	}

	@Override
	protected void doAlgorithm() {
		POPNUM = 2*d.getListSensor().size();

		List<GSAIndividual> population = new ArrayList<GSAIndividual>();
		for (int i = 0; i < POPNUM; i++)
			population.add(GSAIndividual.random(this));

		Comparator<GSAIndividual> compareFitness = (ind1, ind2) -> ind1.fitness-ind2.fitness;

		// Gbest
		Solution gbest = new Solution(Collections.min(population, compareFitness));

		// M
		double[] M = new double[POPNUM];
		
		// F
		double F[] = new double[population.get(0).genes.length];

		for (int i = 0; i < ITER_MAX; i++) {
			// Find best and worst individual
			GSAIndividual best  = Collections.min(population, compareFitness);
			GSAIndividual worst = Collections.max(population, compareFitness);

			// Print
			System.out.println("Iter "+i+" best="+best.fitness+" worst="+worst.fitness+" gbest="+gbest.fitness);

			// Local optimal
			if (best.fitness == worst.fitness)
				break;

			// Calc G
			double G = G0*Math.exp(-beta*i/ITER_MAX);

			// Calc M
			double Ma = 0;
			for (int j = 0; j < POPNUM; j++) {
				M[j] = (population.get(j).fitness-worst.fitness)/(best.fitness-worst.fitness);
				Ma += M[j];
			}
			for (int j = 0; j < POPNUM; j++)
				M[j] /= Ma;

			// update individual
			for (int j = 0; j < POPNUM; j++) {
				GSAIndividual ind = population.get(j);

				// Calc F
				for (int k = 0; k < ind.genes.length; k++)
					F[k] = 0;

				for (int k = 0; k < POPNUM; k++) {
					if (k == j) continue;

					GSAIndividual ind2 = population.get(k);

					double distance = Math.sqrt(IntStream.range(0, ind.genes.length).mapToDouble(l -> Math.pow((ind.genes[l]-ind2.genes[l]), 2)).sum());
					double tmp = RandomUtil.nextDouble()*G*(M[k]*M[j])/(distance + epsilon);

					for (int l = 0; l < ind.genes.length; l++)
						F[l] += tmp*(ind2.genes[l]-ind.genes[l]);
				}

				for (int k = 0; k < ind.genes.length; k++) {
					// Rand j
					double rand0 = RandomUtil.nextDouble();
					double rand1 = RandomUtil.nextDouble();
					double rand2 = RandomUtil.nextDouble();

					// Update v
					ind.velocity[k] = rand0*ind.velocity[k] + rand1*c1*(ind.pbest.genes[k]-ind.genes[k]) + rand2*c2*(gbest.genes[k]-ind.genes[k]) + (M[j] == 0 ? 0 : F[k]/M[j]);

					// Update x
					ind.genes[k] += ind.velocity[k];
					
					if (ind.genes[k] > 100 || ind.genes[k] < 1)
						ind.genes[k] = RandomUtil.nextDouble()*100+1;
				}

				ind.callFitness();
				
				// Update pbest and gbest
				if (ind.fitness < ind.pbest.fitness)
					ind.pbest = new Solution(ind);
				if (ind.fitness < gbest.fitness)
					gbest = new Solution(ind);
			}
		}

		this.Nm = gbest.fitness;
		this.barriers = gbest.barriers;
		
		System.out.print("Solution "+gbest.fitness+" [");
		for (int i = 0; i < gbest.genes.length; i++)
			System.out.print(gbest.genes[i]+", ");
		System.out.println("]");
		for (int i = 0; i < gbest.barriers.length; i++)
			System.out.println(gbest.barriers[i]);
	}

	public static void main(String args[]) {
		run(new GSA(), 1);
	}

}
