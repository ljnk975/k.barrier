package k.barrier.algorithm.ipso2;

import k.barrier.algorithm.Algorithm;
import k.barrier.algorithm.gsa.GSAIndividual;
import k.barrier.model.Barrier;

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
		int oldFit = this.fitness;
		Barrier[] oldBarrier = this.barriers;
		super.callFitness2();
		if (oldBarrier != null) {
			if (this.fitness > oldFit) {
				this.fitness = oldFit;
				this.barriers = oldBarrier;
			}
		}
		this.affinity = 1D/(fitness+1D);
		return fitness;
	}

}
