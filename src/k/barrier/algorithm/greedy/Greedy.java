package k.barrier.algorithm.greedy;

import java.util.List;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.WBG.Node;
import k.barrier.model.Barrier;

public class Greedy extends Algorithm {

	public Greedy() {
		super("Greedy");
	}

	@Override
	protected void doAlgorithm() {
		this.Nm = 0;
		this.barriers = new Barrier[d.getK()];

		int q;
		for (q = 0; q < d.getK(); q++) {
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
			
			// Remove all vertex in WBG
			path.forEach(s -> d.getWBG().remove(s));
		}

		System.out.println("q="+q);
		for (; q < d.getK(); q++) {
			Barrier barrier = d.getWBG().directBarrier();
			this.barriers[q] = barrier; this.Nm += barrier.getLength();
		}

		StringBuilder buff = new StringBuilder();
		buff.append(Nm+" k=[");
		int k = 0;
		for (int i = 0; i < d.getK(); i++) {
			buff.append(k+", "); k += this.barriers[i].size();
		}
		buff.append(k+"] genes=[");
		for (int i = 0; i < d.getK(); i++) {
			Barrier barrier = this.barriers[i];
			for (int j = 0; j < barrier.size()-1; j++) {
				if (barrier.get(j).getIndex() == 0 || barrier.get(j).getIndex() == 1)
					continue;
				buff.append(barrier.get(j).getIndex()+", ");
			}
		}
		buff.append("]");
		System.out.println(buff);
	}

	public static void main(String args[]) {
		run(new Greedy(), 1);
	}

}


