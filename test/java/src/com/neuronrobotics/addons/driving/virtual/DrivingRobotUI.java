package com.neuronrobotics.addons.driving.virtual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.neuronrobotics.addons.driving.AbstractRobot;

public class DrivingRobotUI {
	//These represent where the robots base frame is in pixel-space. This is passed in at instantiation.
	private double startx;
	private double starty;

	private AbstractRobot robot;
	
	private int robotDiameter = 60;
	private double pixelToCm=5;
	private ArrayList<SensorDot> dots= new ArrayList<SensorDot> ();
	public DrivingRobotUI(AbstractRobot robot, double botstartx, double botstarty) {
		setRobot(robot);
		startx=botstartx;
		starty=botstarty;
	}
	
	
	private int getCmToPixel(double cm){
		return (int)(cm*(pixelToCm));
	}
	
	private int getRobotXToPixel(){
		//This converts from robot coordinantes to pixel space, note that X and Y seem swapped, this is correct
		return (int)(getCmToPixel(getRobot().getCurrentY())+startx);
	}
	private int getRobotYToPixel(){
		//This converts from robot coordinantes to pixel space, note that X and Y seem swapped, this is correct
		return (int)(getCmToPixel(getRobot().getCurrentX())+starty);
	}
	
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
		
	}


	public void setRobot(AbstractRobot robot) {
		this.robot = robot;
	}

	public AbstractRobot getRobot() {
		return robot;
	}

	public int[] getSensorPixelLocation(double deltLateral, double deltForward) {
		int [] back = new int[2];
		
		double [] loc = getRobot().getPositionOffset(deltLateral, deltForward);
		
		back[0]=(int)(getCmToPixel(loc[1])+startx);
		back[1]=(int)(getCmToPixel(loc[0])+starty);
		
		return back;
	}

	public void addSensorDisplayDot(double deltLateral, double deltForward, Color c) {
		dots.add(new SensorDot(deltLateral, deltForward, c));
	}
	
	private class SensorDot{
		public double deltLateral; 
		public double deltForward; 
		public Color color;
		public SensorDot(double l, double f, Color c){
			deltForward=f;
			deltLateral=l;
			color=c;
		}
	}

}
