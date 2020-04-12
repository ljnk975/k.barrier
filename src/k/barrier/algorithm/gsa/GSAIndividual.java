package k.barrier.algorithm.gsa;

import k.barrier.algorithm.Algorithm;
import k.barrier.util.RandomUtil;

public class GSAIndividual extends Solution {

	/**
	 * Velocity
	 */
	public double[] velocity;

	/**
	 * Individual best position
	 */
	public Solution pbest;

	/**
	 * r1
	 */
	public double rand1;

	/**
	 * r2
	 */
	public double rand2;

	protected GSAIndividual(Algorithm alg) {
		super(alg);
		this.velocity = new double[size];
		this.rand1    = RandomUtil.randX();
		this.rand2    = RandomUtil.randX();
	}

	public GSAIndividual(Algorithm alg, double[] genes, double[] velocity) {
		this(alg);
		for (int i = 0; i < genes.length; i++)
			this.genes[i] = genes[i];
		for (int i = 0; i < velocity.length; i++)
			this.velocity[i] = velocity[i];
		this.callFitness();
		this.pbest = new Solution(this);
	}

	public GSAIndividual(GSAIndividual ind) {
		super(ind);

		this.velocity = new double[size];

		for (int i = 0; i < velocity.length; i++)
			this.velocity[i] = ind.velocity[i];

		this.rand1    = RandomUtil.randX();
		this.rand2    = RandomUtil.randX();

		this.pbest    = ind.pbest;
	}

	public static GSAIndividual random(Algorithm gsa) {
		double[] genes = new double[gsa.getInput().getListSensor().size()+1+gsa.getInput().getK()];
		for (int j = 1; j < genes.length; j++)
			genes[j] = RandomUtil.nextDouble()*100+1;
		double[] velocitys = new double[genes.length];
		for (int j = 1; j < genes.length; j++)
			velocitys[j] = RandomUtil.nextDouble()*2-1;
		return new GSAIndividual(gsa, genes, velocitys);
	}
/*
	@Override
	public void obtainKBarrier() {
		Data d = alg.getInput();

		boolean[] selected = new boolean[size];
		
		for (int i = 0; i < d.getK(); i++) {
//			System.out.println(i);
			List<Sensor> path = new ArrayList<>(); path.add(Sensor.s);

			while(true) {
//				List<Integer> select  = new ArrayList<>();
//				List<Double> probadly = new ArrayList<>();

				List<Integer> select  = IntStream.range(1, size).filter(j -> !selected[j]).boxed().collect(Collectors.toList());
				List<Double> probadly = select.stream().map(j -> this.genes[j]).collect(Collectors.toList());

				int s = ChoiceUtil.rouletteChoice(select, probadly, 1).get(0); selected[s] = true;
				if (s > d.getListSensor().size())
					break;

				path.add(d.getWBG().getSensor(s));
			}
			path.add(Sensor.t);
			
			barriers[i] = new Barrier(path);
		}
	}
*/
}

