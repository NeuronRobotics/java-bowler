package com.neuronrobotics.addons.driving.virtual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;

public class DrivingRobotUI {
	private double startx;
	private double starty;
	private double orentation = 45;
	private AbstractDrivingRobot robot;
	
	private int robotDiameter = 60;
	
	public DrivingRobotUI(AbstractDrivingRobot robot, double botstartx, double botstarty) {
		this.robot=robot;
		startx=botstartx;
		starty=botstarty;
	}
	

	public void drawRobot(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.setStroke(new BasicStroke(3));
		g.fillOval((int)startx-robotDiameter/2,(int) starty-robotDiameter/2,robotDiameter, robotDiameter);
		int x1 = (int) startx;
		int y1 = (int) starty;
		int orVe = robotDiameter/2+5;
		
		int x2 = (int) (startx+orVe);
		int y2 = (int) starty;
		g.setColor(Color.magenta);
		g.drawLine(x1, y1, x2, y2);
		
	}
}
