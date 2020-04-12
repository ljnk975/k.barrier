package runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import k.barrier.algorithm.Algorithm;
import k.barrier.data.Data;

public class Runner {

	@SuppressWarnings("deprecation")
	public static void run(Class<? extends Algorithm> calg, int nRun) {
		Algorithm alg;

		try {
			alg = calg.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

        FileOutputStream fos = null;
        PrintStream ps = null;

        try {
            fos = new FileOutputStream("ketqua"+alg.getName()+".txt");
            ps = new PrintStream(fos);
            ps.println("filename          best      avg      dlc     time ");
    		File dir = new File("./data/Data/");
    		for(File f : dir.listFiles()) {
    			String name;
    			if(!f.isDirectory() && (name = f.getName()).endsWith(".txt")) {
                    double[] kqO = new double[nRun];
                    double kqAv = 0;
                    double timeAv = 0;
                    double doLC = 0;
                    double best = -1;

                    name = name.substring(0, name.lastIndexOf(".txt"));
    				Data data = Data.readData(f);

    				System.out.println("Data: "+name);
    				for(int r = 0; r < nRun; r++) {
    					alg.solve("./data/Result/", "Data_"+name+"_Run_"+r, data);

    					kqO[r] = alg.getNm();
                        kqAv += alg.getNm();
                        timeAv += alg.getTimeElapse();
                        if(best < 0 || alg.getNm() < best)
                        	best = alg.getNm();
    				}
                    timeAv /= nRun;
                    kqAv /= nRun;
                    for (int v = 0; v < nRun; v++)
                        doLC += (kqO[v] - kqAv) * (kqO[v] - kqAv);
                    doLC = Math.sqrt(doLC/nRun);
                    ps.println(name + "      " + best + "      " + kqAv + "      " + doLC + "      " + timeAv);
                    ps.println("-------------------------------------------------");
    			}
    		}
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
            try {
                ps.close();
                fos.close();
            } catch (Exception e) {
            }
        }
	}

}
