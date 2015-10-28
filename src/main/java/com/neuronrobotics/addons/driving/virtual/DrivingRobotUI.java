package com.neuronrobotics.addons.driving.virtual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;

// TODO: Auto-generated Javadoc
/**
 * The Class DrivingRobotUI.
 */
public class DrivingRobotUI {
	
	/** The startx. */
	//These represent where the robots base frame is in pixel-space. This is passed in at instantiation.
	private double startx;
	
	/** The starty. */
	private double starty;

	/** The robot. */
	private AbstractRobotDrive robot;
	
	/** The robot diameter. */
	private int robotDiameter = 60;

	/** The world. */
	private VirtualWorld world;
	
	/** The dots. */
	private ArrayList<SensorDot> dots= new ArrayList<SensorDot> ();
	
	/** The range. */
	int [] range = null;
	
	/**
	 * Instantiates a new driving robot ui.
	 *
	 * @param w the w
	 * @param robot the robot
	 * @param botstartx the botstartx
	 * @param botstarty the botstarty
	 */
	public DrivingRobotUI(VirtualWorld w ,AbstractRobotDrive robot, double botstartx, double botstarty) {
		setRobot(robot);
		startx=botstartx;
		starty=botstarty;
		world=w;
	}
	

	/**
	 * Gets the robot x to pixel.
	 *
	 * @return the robot x to pixel
	 */
	public int getRobotXToPixel(){
		//This converts from robot coordinantes to pixel space, note that X and Y seem swapped, this is correct
		return (int)(world.getCmToPixel(getRobot().getCurrentY())+startx);
	}
	
	/**
	 * Gets the robot y to pixel.
	 *
	 * @return the robot y to pixel
	 */
	public int getRobotYToPixel(){
		//This converts from robot coordinantes to pixel space, note that X and Y seem swapped, this is correct
		return (int)(world.getCmToPixel(getRobot().getCurrentX())+starty);
	}
	
	/**
	 * Draw robot.
	 *
	 * @param g the g
	 */
	public void drawRobot(Graphics2D g) {
		//System.out.println("Drawing robot on map");
		g.setColor(Color.CYAN);
		g.setStroke(new BasicStroke(3));
		
		int centerx=getRobotXToPixel();
		int centery=getRobotYToPixel();
		g.fillOval((int)centerx-robotDiameter/2,(int)centery-robotDiameter/2,robotDiameter, robotDiameter);
		
		
		int x1 = (int) centerx;
		int y1 = (int) centery;
		int orVe = robotDiameter/2;
		
		//System.out.println("Robot center coordinante x="+x1+" y="+y1);
		
		int x2 = (int) (centerx+Math.cos(getRobot().getCurrentOrentation()-(Math.PI/2))*orVe);
		int y2 = (int) (centery-Math.sin(getRobot().getCurrentOrentation()-(Math.PI/2))*orVe);
		g.setColor(Color.magenta);
		g.drawLine(x1, y1, x2, y2);
		
		for(SensorDot s:dots){
			int [] loc = getSensorPixelLocation(s.deltLateral, s.deltForward);
			int d=3;
			g.setColor(s.color);
			g.fillOval(loc[0]-d,loc[1]-d,d*2, d*2);
		}
		
		if(range!=null){
			g.setColor(Color.green);
			g.setStroke(new BasicStroke(1));
			g.drawLine(x1, y1, range[0], range[1]);
		}
		
	}


	/**
	 * Sets the robot.
	 *
	 * @param robot the new robot
	 */
	public void setRobot(AbstractRobotDrive robot) {
		this.robot = robot;
	}

	/**
	 * Gets the robot.
	 *
	 * @return the robot
	 */
	public AbstractRobotDrive getRobot() {
		return robot;
	}

	/**
	 * Gets the sensor pixel location.
	 *
	 * @param deltLateral the delt lateral
	 * @param deltForward the delt forward
	 * @return the sensor pixel location
	 */
	public int[] getSensorPixelLocation(double deltLateral, double deltForward) {
		int [] back = new int[2];
		
		double [] loc = getRobot().getPositionOffset(deltLateral, deltForward);
		
		back[0]=(int)(world.getCmToPixel(loc[1])+startx);
		back[1]=(int)(world.getCmToPixel(loc[0])+starty);
		
		return back;
	}

	/**
	 * Adds the sensor display dot.
	 *
	 * @param deltLateral the delt lateral
	 * @param deltForward the delt forward
	 * @param c the c
	 */
	public void addSensorDisplayDot(double deltLateral, double deltForward, Color c) {
		dots.add(new SensorDot(deltLateral, deltForward, c));
	}
	
	/**
	 * The Class SensorDot.
	 */
	private class SensorDot{
		
		/** The delt lateral. */
		public double deltLateral; 
		
		/** The delt forward. */
		public double deltForward; 
		
		/** The color. */
		public Color color;
		
		/**
		 * Instantiates a new sensor dot.
		 *
		 * @param l the l
		 * @param f the f
		 * @param c the c
		 */
		public SensorDot(double l, double f, Color c){
			deltForward=f;
			deltLateral=l;
			color=c;
		}
	}
	
	/**
	 * Clear range vector.
	 */
	public void clearRangeVector() {
		range=null;
	}
	
	/**
	 * Sets the range vector.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setRangeVector(int x, int y) {
		range = new int[2];
		range[0]=x;
		range[1]=y;
	}

}
