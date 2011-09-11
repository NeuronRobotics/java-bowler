package com.neuronrobotics.addons.driving.virtual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;

public class DrivingRobotUI {
	//These represent where the robots base frame is in pixel-space. This is passed in at instantiation.
	private double startx;
	private double starty;

	private AbstractDrivingRobot robot;
	
	private int robotDiameter = 60;
	private double pixelToCm=5;
	
	public DrivingRobotUI(AbstractDrivingRobot robot, double botstartx, double botstarty) {
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
		int orVe = robotDiameter/2+5;
		
		//System.out.println("Robot center coordinante x="+x1+" y="+y1);
		
		int x2 = (int) (centerx+Math.cos(getRobot().getCurrentOrentation()-(Math.PI/2))*orVe);
		int y2 = (int) (centery-Math.sin(getRobot().getCurrentOrentation()-(Math.PI/2))*orVe);
		g.setColor(Color.magenta);
		g.drawLine(x1, y1, x2, y2);
		
	}


	public void setRobot(AbstractDrivingRobot robot) {
		this.robot = robot;
	}

	public AbstractDrivingRobot getRobot() {
		return robot;
	}


	public int getXpix(int deltLateral, int deltForward) {
		
		double x = getRobot().getCurrentX();
		double y = getRobot().getCurrentY();
		double o = getRobot().getCurrentOrentation();
		
		x+=deltForward*Math.cos(o);
		y+=deltForward*Math.sin(o);
		
		x+=deltLateral*Math.sin(o);
		y+=deltLateral*Math.cos(o);
		
		return (int)(getCmToPixel(y)+startx);
	}


	public int getYpix(int deltLateral, int deltForward)  {
		double x = getRobot().getCurrentX();
		double y = getRobot().getCurrentY();
		double o = getRobot().getCurrentOrentation();
		
		x+=deltForward*Math.cos(o);
		y+=deltForward*Math.sin(o);
		
		x+=deltLateral*Math.sin(o);
		y+=deltLateral*Math.cos(o);
		
		
		return (int)(getCmToPixel(x)+starty);
	}

}
