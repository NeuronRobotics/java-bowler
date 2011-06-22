package com.neuronrobotics.sdk.addons.kinematics;

public abstract class AbstractLink {
	private double scale;
	private int upperLimit;
	private int lowerLimit;
	private int home;
	private int targetValue=0;
	
	private double targetAngle=0;
	
	/**
	 * This method is called in order to take the target value and pass it to the implementation's target value
	 * This method should not alter the position of the implementations link
	 * If the implementation target does not handle chached values, this should be chached in code
	 */
	public abstract void cacheTargetValue();
	/**
	 * This method will force one link to update its position in the given time (seconds)
	 * @param time (seconds) for the position update to take
	 */
	public abstract void flush(double time);
	
	/**
	 * This method should return the current position of the link 
	 * @return the current position of the link
 	 */
	public abstract int getCurrentPosition();
	
	
	public AbstractLink(int home,int lowerLimit,int upperLimit,double scale){
		setScale(scale);
		setUpperLimit(upperLimit);
		setLowerLimit(lowerLimit);
		setHome(home);
	}
	
	
	public void Home(double time){
		setPosition(this.getHome(),(float) time);
		cacheTargetValue();
	}
	
	public void incrementAngle(double inc,double time){
		setTargetAngle(targetAngle+inc,time);
	}
	public void setTargetAngle(double pos,double time) {
		this.targetAngle = pos;
		setPosition(((int) (pos/getScale()))+getHome(),(float) time);
	}
	public double getTargetAngle() {
		return ((getTargetValue()-getHome())*getScale());
	}
	
	public double getMaxAngle() {
		return (getUpperLimit()-getHome())*getScale();
	}
	public double getMinAngle() {
		return (getLowerLimit()-getHome())*getScale();
	}
	public boolean isMaxAngle() {
		if(getTargetValue() == getUpperLimit()) {
			return true;
		}
		return false;
	}
	public boolean isMinAngle() {
		if(getTargetValue() == getLowerLimit()) {
			return true;
		}
		return false;
	}
	
	protected void setPosition(int val,float time) {
		if(getTargetValue() == val)
			return;
		setTargetValue(val);
		cacheTargetValue();
		flush(time);
	}
	
	public void setTargetValue(int val) {
		if(val>getUpperLimit())
			val=getUpperLimit();
		if(val<getLowerLimit()) {
			//System.out.println("Attempting to set to value:"+val+" is below limit:"+lowerLimit);
			val=getLowerLimit();
		}
		this.targetValue = val;
	}
	public int getTargetValue() {
		return targetValue;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
	public double getScale() {
		return scale;
	}	
	public void setUpperLimit(int upperLimit) {
		this.upperLimit = upperLimit;
	}
	public int getUpperLimit() {
		return upperLimit;
	}
	public void setLowerLimit(int lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public int getLowerLimit() {
		return lowerLimit;
	}
	public void setHome(int home) {
		this.home = home;
	}
	public int getHome() {
		return home;
	}
	
}
