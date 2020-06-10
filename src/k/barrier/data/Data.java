package k.barrier.data;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import k.barrier.model.IDraw;
import k.barrier.model.Sensor;

/**
 * Dinh nghia 1 topo du lieu dau vao, gom cac tap sensor
 * @author ljnk975
 *
 */
public class Data implements IDraw {

	/**
	 * Kich thuoc vung giam sat
	 */
	private int L, H;
	
	/**
	 * Tap sensor
	 */
	private List<Sensor> listSensor;
	
	/**
	 * Solve Weak barrier ?
	 */
	private boolean weak;

	/**
	 * Ban kinh cua sensor dong them vao
	 */
	private double lr;

	/**
	 * Number of barrier
	 */
	private int k;

	/**
	 * The weighted graph
	 */
	private WBG wbg;

	/**
	 * Ham tao data
	 * @param L
	 * @param H
	 * @param listSensor
	 * @param lr
	 * @param weak
	 */
	public Data(int L, int H, int k, List<Sensor> listSensor, double lr, boolean weak) {
		this.L = L;
		this.H = H;
		this.listSensor = listSensor;
		this.lr = lr;
		this.k = k;
		this.weak = weak;
	}

	/**
	 * Ham tao sensor
	 * @param L
	 * @param H
	 * @param listSensor
	 * @param lr
	 */
	public Data(int L, int H, int k, List<Sensor> listSensor, double lr) {
		this(L, H, k, listSensor, lr, false);
	}

	/**
	 * Tao data rong
	 */
	private Data() {
	}

	/**
	 * @return the l
	 */
	public int getL() {
		return L;
	}

	/**
	 * @param l the l to set
	 */
	public void setL(int l) {
		L = l;
	}

	/**
	 * @return the h
	 */
	public int getH() {
		return H;
	}

	/**
	 * @param h the h to set
	 */
	public void setH(int h) {
		H = h;
	}
	
	/**
	 * @return the listSensor
	 */
	public List<Sensor> getListSensor() {
		return listSensor;
	}

	/**
	 * @param listSensor the listSensor to set
	 */
	public void setListSensor(List<Sensor> listSensor) {
		this.listSensor = listSensor;
	}

	/**
	 * @return the weak
	 */
	public boolean isWeak() {
		return weak;
	}

	/**
	 * @param weak the weak to set
	 */
	public void setWeak(boolean weak) {
		this.weak = weak;
	}

	/**
	 * @return the lr
	 */
	public double getLr() {
		return lr;
	}

	/**
	 * @param lr the lr to set
	 */
	public void setLr(double lr) {
		this.lr = lr;
	}

	/**
	 * @return the number of sensor
	 */
	public int getK() {
		return k;
	}

	/**
	 * @param k the number of sensor to set
	 */
	public void setK(int k) {
		this.k = k;
	}

	/**
	 * Get the weighted graph
	 * @return the wbg
	 */
	public WBG getWBG() {
		if (this.wbg == null)
			this.wbg = new WBG(this);
		return this.wbg;
	}

	@Override
	public void draw(Graphics2D g) {
		for (Sensor sensor : listSensor)
			sensor.draw(g);
	}

	/**
	 * Doc data tu 1 duong dan
	 * @param fn duong dan den data file
	 * @return   Data doc duoc
	 */
	public static Data readData(String fn) {
		return readData(new File(fn));
	}

	/**
	 * Doc data tu 1 file dau vao
	 * @param fn file data
	 * @return   Data doc duoc
	 */
	public static Data readData(File fn) {
		Scanner in = null;
		try {
			Data d = new Data();

			in   = new Scanner(fn);

			d.L  = in.nextInt();
			d.H  = in.nextInt();
			d.lr = in.nextDouble();
			d.k = in.nextInt();
			d.listSensor = new ArrayList<Sensor>();

			int nSensor = in.nextInt();
			for (int i = 0; i < nSensor; i++) {
				double x = in.nextDouble();
				double y = in.nextDouble();
				double r = in.nextDouble();
				double a = in.nextDouble();
				double b = in.nextDouble();

				d.listSensor.add(new Sensor(i+2, x, y, r, a, b));
			}

			return d;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch(Exception e) {
			}
		}
		return null;
	}

}
