package com.neuronrobotics.addons.driving;

public class RobotLocationData {
	private double x,y,o;
	RobotLocationData(double deltaX, double deltaY, double deltaOrentation){
		 setX(deltaX);
		 
		 setY(deltaY);
		 setO(deltaOrentation);
	}
	private void setX(double x) {
		this.x = x;
	}
	public double getDeltaX() {
		return x;
	}
	private void setY(double y) {
		this.y = y;
	}
	public double getDeltaY() {
		return y;
	}
	private void setO(double o) {
		this.o = o;
	}
	public double getDeltaOrentation() {
		return o;
	}
}
