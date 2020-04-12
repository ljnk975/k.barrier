package k.barrier.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChoiceUtil {

	public static int rouletteChoice(double[] probadly, List<Integer> choice) {
		List<Integer> list = IntStream.range(0, probadly.length).filter(i -> !choice.contains(i)).boxed().collect(Collectors.toList());
		double sum = list.stream().mapToDouble(i -> probadly[i]).sum();
		double roll = sum*RandomUtil.nextDouble();
		double s = 0; int selected = 0;
		for (int i = 0; i < list.size(); i++) {
			int k = list.get(i);
			if (i == list.size()-1 || roll >= s && roll < (s=s+probadly[k])) {
				selected = k;
				break;
			}
		}
		choice.add(selected);
		return selected;
	}

}
