package k.barrier.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import k.barrier.model.Sensor;

/**
 * Generate data randomly
 * 
 * @author ljnk975
 *
 */
public class GenData {

    private static final int W = 1000;
    private static final int H = 1000;

    private static final int[] k  = new int [] {10, 20, 50, 100};
    private static final int[] ns = new int [] {5000, 2000, 1000, 500, 100};

    private static final double l = 10;

    private static final Random rand = new Random();

    /**
     * Generate sensor randomly
     * @param n
     * @param r
     * @return list of generated sensor
     */
    private ArrayList<Sensor> randomData(int n, double r) {
        ArrayList<Sensor> list = new ArrayList<Sensor>();
        double rangeMin_x = 0, rangeMax_x = W;
        double rangeMin_y = 0, rangeMax_y = H;
        for(int i = 0; i < n; i++) {
        	double x = rangeMin_x + (rangeMax_x - rangeMin_x) * rand.nextDouble();
        	double y = rangeMin_y + (rangeMax_y - rangeMin_y) * rand.nextDouble();
        	Sensor s = new Sensor(0, x, y, r);
        	list.add(s);
        }
        return list;
    }

    /**
     * Generate data randomly
     * @return the generated data
     */
    public Data randomData(int ns, int k) {
    	return new Data(W, H, k, randomData(ns, l), l);
    }

	/**
	 * Ve ra 1 file
	 * @param file duong dan de ve
	 */
	private void draw(String file, Data d) {
		BufferedImage bitmap = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)bitmap.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        d.draw(g);

		try {
    		ImageIO.write(bitmap, "png", new FileOutputStream(file));
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
	}

	// Ghi du lieu vs k la so lan chay
    private void writeData(int ns, int k) {
    	String path = "data/Data";
        String fileName = "data_" + ns +"_" + k + ".txt";
        
        File f = new File(path);
        if (!f.exists())
        	f.mkdirs();

        try {
        	Data d = randomData(ns, k);

        	FileOutputStream fos = new FileOutputStream(path+"/"+fileName);
            PrintStream ps = new PrintStream(fos);
            ps.println(d.getL() + " " + d.getH() + " " + d.getK() +" "+ d.getLr());
            List<Sensor> list = d.getListSensor();
            ps.println(list.size());
            for (int i = 0; i < list.size(); i++) {
                Sensor s = list.get(i);
                ps.println(s.getCircle().x + " " + s.getCircle().y + " " + " "+s.getR());
            }
            ps.close();
            fos.close();
            
            draw(path+"/"+fileName+".png", d);
        } catch(Exception e) {
        	e.printStackTrace();
        }

        System.out.println("Done: " + fileName);
    }

    public static void main(String[] args) {
        GenData g = new GenData();
        System.out.println("begin");
        for (int i = 0; i < ns.length; i++)
        	for (int j = 0; j < k.length; j++)
        		g.writeData(ns[i], k[j]);
        System.out.println("Done");
    }
    
}
