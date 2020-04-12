package k.barrier.algorithm.optimal;

import java.util.ArrayList;
import java.util.List;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.WBG;
import k.barrier.data.WBG.Node;

public class OptimalAlgorithm extends Algorithm {

	public OptimalAlgorithm() {
		super("Optimal");
	}

	@Override
	protected void doAlgorithm() {
		@SuppressWarnings("unchecked")
		List<List<Node>> P[] = new List[d.getK()+1];

		for (int i = 0; i < P.length; i++)
			P[i] = new ArrayList<>();
		P[1].add(d.getWBG().dijkstra());

		int n = 1;
		while (n < d.getK()) {
			WBG ng = d.getWBG().transform(P[n]);
			List<Node> path = ng.dijkstra();
			if (path != null) {
				n++;
				
			}
		}
	}

}
