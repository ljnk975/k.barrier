package k.barrier.algorithm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import k.barrier.data.Data;
import k.barrier.model.Barrier;
import k.barrier.model.Sensor;

/**
 * Define an algorithm interface
 * 
 * @author ljnk975
 *
 */
public abstract class Algorithm {

	// Ten cua thuat toan
	public String name;

	/// -> Input
	protected Data d;

	/// -> Output
	/**
	 * Time elapse
	 */
	protected long timeElapse;

	/**
	 * Number of mobile sensor
	 */
	protected int Nm;

	/**
	 * k Barrier
	 */
	protected Barrier[] barriers;

	protected Algorithm(String name) {
		this.name = name;
	}

	/**
	 * @return ten thuat toan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Main algorithm
	 * 
	 */
	protected abstract void doAlgorithm();

	/**
	 * @return thoi gian tinh
	 */
	public long getTimeElapse() {
		return timeElapse;
	}

	/**
	 * Get number of mobile sensor
	 * @return the mobile sensor
	 */
	public int getNm() {
		return this.Nm;
	}

	/**
	 * Get k barrier
	 * @return the list of barrier
	 */
	public Barrier[] getBarriers() {
		return this.barriers;
	}

	public Data getInput() {
		return this.d;
	}

	/**
	 * Read output directory
	 * @param filename
	 */
	private final void readOutput(String filename) {
		Scanner in = null;
		try {
			in  = new Scanner(new File(filename));
			d.setK(in.nextInt());
			Nm  = in.nextInt();
			timeElapse = in.nextLong();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch(Exception e) {
			}
		}
	}

	/**
	 * Solve proplem
	 * @param odir  output directory
	 * @param fname file name
	 * @param data  data
	 */
	public final void solve(String odir, String fname, Data data) {
		this.d = data;

		String filename = odir + name + "/" + fname+".txt"; // output file path

		File f = new File(filename.substring(0, filename.lastIndexOf("/"))); // output directory
		if(!f.exists()) // create if not exists
			f.mkdirs();

		f = new File(filename); // output file
		if(f.exists()) { // if exists
			this.readOutput(filename); // read the output
			return;
		}

		this.timeElapse = System.currentTimeMillis(); // start time
		this.doAlgorithm(); // do solve
		this.timeElapse = System.currentTimeMillis()-this.timeElapse; // end time

		PrintStream ps = null; // printstream
		try {
			ps = new PrintStream(new FileOutputStream(filename));

			// Output
			ps.println(d.getK());
			ps.println(Nm);
			ps.println(timeElapse);
			
			// Barrier
			for(int i = 0; i < d.getK(); i++) {
				ps.print("[");
				Barrier b = barriers[i];
				for (int j = 0; j < b.size()-1; j++)
					ps.print(b.get(j).getIndex()+",");
				ps.println("1]");
			}

			ps.close(); // dong file

			draw(odir, fname+".png", data); // ve hinh output
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch(Exception e) {
			}
		}
	}

	// ve ra file
	protected final void draw(String odir, String fname, Data data) {
		BufferedImage bitmap = new BufferedImage(data.getL(), data.getH()+40, BufferedImage.TYPE_INT_ARGB);

		this.doDraw((Graphics2D) bitmap.getGraphics(), data);

    	try {
    		ImageIO.write(bitmap, "png", new FileOutputStream(odir + name + "/" + fname));
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
	}

	// ve len graphics
	protected void doDraw(Graphics2D g, Data data) {
		((Graphics2D)g).setStroke(new BasicStroke(2));

		g.setClip(0, 0, data.getL()-1, data.getH()-1);

		data.draw(g);

		g.setColor(Color.RED);
		((Graphics2D)g).setStroke(new BasicStroke(3));

		Barrier[] list = this.barriers;
		
		for (Barrier b : list) {
			Sensor s = b.get(0);
			for (int i = 1; i < b.size(); i++) {
				Sensor t = b.get(i);
				if (s == Sensor.s && t == Sensor.t || s == Sensor.t && t == Sensor.s)
					break;
				if (s == Sensor.s)
					g.drawLine(0, (int)t.getCircle().y, (int)t.getCircle().x, (int)t.getCircle().y);
				else if (s == Sensor.t)
					g.drawLine(d.getL(), (int)t.getCircle().y, (int)t.getCircle().x, (int)t.getCircle().y);
				else if (t == Sensor.s)
					g.drawLine((int)s.getCircle().x, (int)s.getCircle().y, 0, (int)s.getCircle().y);
				else if (t == Sensor.t)
					g.drawLine((int)s.getCircle().x, (int)s.getCircle().y, d.getL(), (int)s.getCircle().y);
				else
					g.drawLine((int)s.getCircle().x, (int)s.getCircle().y, (int)t.getCircle().x, (int)t.getCircle().y);
				s = t;
			}
		}

		g.setColor(Color.BLACK);
		g.setClip(0, 0, data.getL(), data.getH()+40);

		g.setColor(Color.BLACK);
		g.drawRect(0, 0, data.getL()-2, data.getH()-2);

		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString(String.format("Nm: %d", this.Nm), 5, data.getH()+20);
		g.drawString(String.format("Time: %5.3fs", (float)this.timeElapse/1000), 5, data.getH()+40);
	}

	@Override
	public String toString() {
		return "Algothirm "+name;
	}

	public static void run(Algorithm alg, int nRun) {
        FileOutputStream fos = null;
        PrintStream ps = null;

        try {
            fos = new FileOutputStream("ketqua"+alg.getName()+".txt");
            ps = new PrintStream(fos);
            if (nRun == 1)
                ps.println("filename          result     time ");
            else
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
                    if (nRun == 1)
                    	ps.println(name + "      " + best + "      " + timeAv);
                    else
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
