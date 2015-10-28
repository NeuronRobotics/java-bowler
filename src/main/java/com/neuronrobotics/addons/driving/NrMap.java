package com.neuronrobotics.addons.driving;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.neuronrobotics.addons.driving.virtual.ObsticleType;

// TODO: Auto-generated Javadoc
/**
 * The Class NrMap.
 */
public class NrMap extends JPanel{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1487461776000494761L;
	
	/** The pixel to cm. */
	private double pixelToCm=10;
	
	/** The lab. */
	JLabel lab=new JLabel();
	
	/** The display. */
	private BufferedImage display;
	
	/** The obs. */
	private ArrayList<userDefinedObsticles> obs = new ArrayList<userDefinedObsticles>();
	
	/** The Constant width. */
	protected static final double width = 1024;
	
	/** The Constant height. */
	protected static final double height = 1024;
	
	/**
	 * Instantiate a robot map using a default blank map. 
	 */
	public NrMap(){
		initGui();
	}
	
	/**
	 * Instantiate a robot map using a provided map.
	 *
	 * @param b the image of the desired map
	 */
	public NrMap(BufferedImage b){
		setDisplay(b);
		initGui();
	}
	
	/**
	 * Inits the gui.
	 */
	protected void initGui(){ 
        removeAll();
        updateMap();
        add(lab); 
	}
	
	/**
	 * Update map.
	 */
	protected void updateMap() {
		//System.out.println("Updating Map");
		BufferedImage display = getMap();
		setFinalDisplayImage(display);
	}
	
	/**
	 * Sets the final display image.
	 *
	 * @param d the new final display image
	 */
	protected void setFinalDisplayImage(BufferedImage d){
		lab.setIcon(new ImageIcon(d ) );
		lab.setVisible(true);
	}
	
	/**
	 * Sets the display.
	 *
	 * @param d the new display
	 */
	protected void setDisplay(BufferedImage d){
		display=d;
	}
	
	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public BufferedImage getMap() {
		if(display==null){
			return new BufferedImage((int)width,(int) height,BufferedImage.TYPE_INT_RGB);
		}
		BufferedImage d = new BufferedImage(display.getWidth(), display.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g =d.createGraphics();
		g.drawImage(display,0, 0, null);
		
		for(userDefinedObsticles o:obs) {
			o.drawUserObsticles(g);
		}
		return d;
	}
	
	/**
	 * Removes the all user defined obsticles.
	 */
	public void removeAllUserDefinedObsticles(){
		obs.clear();
	}
	
	/**
	 * Adds the user defined obsticle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param size the size
	 * @param type the type
	 */
	public void addUserDefinedObsticle(int x, int y, int size,ObsticleType type){
		if(display==null){
			display =  new BufferedImage((int)width,(int) height,BufferedImage.TYPE_INT_RGB);
		}
		obs.add(new userDefinedObsticles(x,y,size,type));
		
	}
	
	/**
	 * Gets the obsticle.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the obsticle
	 */
	public ObsticleType getObsticle(int x,int y) {
		return ObsticleType.get(new Color(getMap().getRGB(x, y)));
	}
	
	/**
	 * Gets the pixel to cm.
	 *
	 * @param pix the pix
	 * @return the pixel to cm
	 */
	public double getPixelToCm(int pix){
		return ((double)pix)/(pixelToCm);
	}
	
	/**
	 * Gets the cm to pixel.
	 *
	 * @param cm the cm
	 * @return the cm to pixel
	 */
	public double getCmToPixel(double cm){
		return (cm*(pixelToCm));
	}
	
	/**
	 * The Class userDefinedObsticles.
	 */
	private class userDefinedObsticles{
		
		/** The type. */
		ObsticleType type;
		
		/**
		 * Instantiates a new user defined obsticles.
		 *
		 * @param x2 the x2
		 * @param y2 the y2
		 * @param size2 the size2
		 * @param type the type
		 */
		public userDefinedObsticles(int x2, int y2, int size2,ObsticleType type) {
			this.type = type;
			setX(x2);
			setY(y2);
			setSize(size2);
		}

		/**
		 * Draw user obsticles.
		 *
		 * @param g the g
		 */
		public void drawUserObsticles(Graphics2D g) {
			g.setColor(type.getValue());
			g.fillRect(getX()-(getSize()/2),getY()-(getSize()/2), getSize(),getSize());
		}

		/**
		 * Sets the x.
		 *
		 * @param x the new x
		 */
		public void setX(int x) {
			this.x = x;
		}

		/**
		 * Gets the x.
		 *
		 * @return the x
		 */
		public int getX() {
			return x;
		}

		/**
		 * Sets the y.
		 *
		 * @param y the new y
		 */
		public void setY(int y) {
			this.y = y;
		}

		/**
		 * Gets the y.
		 *
		 * @return the y
		 */
		public int getY() {
			return y;
		}

		/**
		 * Sets the size.
		 *
		 * @param size the new size
		 */
		public void setSize(int size) {
			this.size = size;
		}

		/**
		 * Gets the size.
		 *
		 * @return the size
		 */
		public int getSize() {
			return size;
		}

		/** The x. */
		private int x;
		
		/** The y. */
		private int y;
		
		/** The size. */
		private int size;
	}
	
	/**
	 * Sets the user defined data.
	 *
	 * @param data the data
	 * @param type the type
	 */
	public void setUserDefinedData(ArrayList<DataPoint> data,ObsticleType type) {
		 //removeAllUserDefinedObsticles();
		 for(DataPoint d:data){
			 double pix =  getCmToPixel(d.getRange()/100);
			 double centerX=(width/2);
			 double centerY=(height/2);
			 if(!(pix>centerX || pix>centerY )){
				 double deltX = pix*Math.cos(Math.toRadians(d.getAngle()));
				 double deltY = pix*Math.sin(Math.toRadians(d.getAngle()));
				 addUserDefinedObsticle((int)(centerX+deltX), (int)(centerY+deltY), 2,type);
			 }else{
				 //System.out.println("Range too long: "+pix+" cm="+d.getRange()/100);
			 }
		 }
		 updateMap();
	}
	
}
