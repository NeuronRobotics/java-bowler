package com.neuronrobotics.sdk.bowlercam.device;

// TODO: Auto-generated Javadoc
/**
 * The Class ItemMarker.
 */
public class ItemMarker {
	
	/** The x. */
	private int x;
	
	/** The y. */
	private int y;
	
	/** The radius. */
	private int radius;
	
	/**
	 * Instantiates a new item marker.
	 *
	 * @param x the x
	 * @param y the y
	 * @param radius the radius
	 */
	public ItemMarker(int x, int y,int radius){
		setX(x);
		setY(y);
		setRadius(radius);
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
	 * Sets the radius.
	 *
	 * @param radius the new radius
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public int getRadius() {
		return radius;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s="X: "+x+", Y: "+y+", Rad: "+radius;
		return s;
	}
}
