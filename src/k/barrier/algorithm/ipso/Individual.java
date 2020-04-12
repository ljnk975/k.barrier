package k.barrier.algorithm.ipso;

import k.barrier.algorithm.Algorithm;
import k.barrier.algorithm.gsa.GSAIndividual;

public class Individual extends GSAIndividual {

	public double affinity;
	public double density;

	private Individual(Algorithm alg) {
		super(alg);
	}

	public Individual(Algorithm alg, double[] genes, double[] velocity) {
		super(alg, genes, velocity);
	}

	protected Individual(GSAIndividual ind) {
		super(ind);
	}

	public static Individual random(Algorithm gsa) {
		return new Individual(GSAIndividual.random(gsa));
	}

	@Override
	public int callFitness() {
		super.callFitness();
		this.affinity = 1D/(fitness+1D);
		return fitness;
	}

}
