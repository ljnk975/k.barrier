package k.barrier.algorithm.greedy;

import java.util.List;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.WBG.Node;
import k.barrier.model.Barrier;

public class GreedyAlgorithm extends Algorithm {

	public GreedyAlgorithm() {
		super("Greedy");
	}

	@Override
	protected void doAlgorithm() {
		int q = 0;
		
		this.Nm = 0;
		this.barriers = new Barrier[d.getK()];

		while (q < d.getK()) {
			List<Node> path = d.getWBG().dijkstra();
			Barrier barrier;
			if (path == null || (barrier = d.getWBG().barrierFromPath(path)).getLength() >= (int)(d.getL()/d.getLr()))
				break;

			this.barriers[q] = barrier;
			this.Nm += barrier.getLength();

			System.out.print("Barrier "+q+": [");
			for (int i = 0; i < barrier.size()-1; i++)
				System.out.print(barrier.get(i).getIndex()+", ");
			System.out.println("1] Nm="+Nm);
			q++;
			
			// Remove all vertex in WBG
			path.forEach(s -> d.getWBG().remove(s));
		}

		for (; q < d.getK(); q++) {
			Barrier barrier = d.getWBG().directBarrier();
			this.barriers[q] = barrier; this.Nm += barrier.getLength();
		}
	}

}


