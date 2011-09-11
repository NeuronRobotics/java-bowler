package com.neuronrobotics.addons.driving.virtual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;

public class DrivingRobotUI {
	//These represent where the robots base frame is in pixel-space. This is passed in at instantiation.
	private double startx;
	private double starty;
	private double orentation = 0;
	private AbstractDrivingRobot robot;
	
	private int robotDiameter = 60;
	private double pixelToCm=5;
	
	public DrivingRobotUI(AbstractDrivingRobot robot, double botstartx, double botstarty) {
		this.robot=robot;
		startx=botstartx;
		starty=botstarty;
	}
	
	
	private int getCmToPixel(double cm){
		return (int)(cm*(pixelToCm));
	}
	
	private int getRobotXToPixel(){
		//This converts from robot coordinantes to pixel space, note that X and Y seem swapped, this is correct
		return (int)(getCmToPixel(robot.getCurrentY())+startx);
	}
	private int getRobotYToPixel(){
		//This converts from robot coordinantes to pixel space, note that X and Y seem swapped, this is correct
		return (int)(getCmToPixel(robot.getCurrentX())+starty);
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
		
		int x2 = (int) (centerx+Math.cos(robot.getCurrentTheta()-(Math.PI/2))*orVe);
		int y2 = (int) (centery-Math.sin(robot.getCurrentTheta()-(Math.PI/2))*orVe);
		g.setColor(Color.magenta);
		g.drawLine(x1, y1, x2, y2);
		
	}
}
