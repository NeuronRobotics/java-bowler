package com.neuronrobotics.sdk.bowlercam.device;

public class ItemMarker {
	private int x;
	private int y;
	private int radius;
	public ItemMarker(int x, int y,int radius){
		setX(x);
		setY(y);
		setRadius(radius);
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getY() {
		return y;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public int getRadius() {
		return radius;
	}
	@Override
	public String toString(){
		String s="X: "+x+", Y: "+y+", Rad: "+radius;
		return s;
	}
}
