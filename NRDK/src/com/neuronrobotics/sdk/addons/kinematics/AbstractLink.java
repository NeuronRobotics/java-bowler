package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.Log;

// Kevin Shouldn't the Link's channel be kept in this level of Abstraction? The way I designg AbstractCartesianPositonDevice  Requires this
public abstract class AbstractLink {
	private double scale;
	private int upperLimit;
	private int lowerLimit;
	private int home;
	private int targetValue=0;
	
	private double targetEngineeringUnits=0;
	
	private ArrayList<ILinkListener> links = new ArrayList<ILinkListener>();
	private LinkConfiguration conf =null;
	
	public AbstractLink(int home,int lowerLimit,int upperLimit,double scale){
		setScale(scale);
		setUpperLimit(upperLimit);
		setLowerLimit(lowerLimit);
		setHome(home);
	}
	
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
	 * This method will force one link to update its position in the given time (seconds)
	 * This will also flush the host controller
	 * @param time (seconds) for the position update to take
	 */
	public abstract void flushAll(double time);
	
	/**
	 * This method should return the current position of the link
	 * This method is expected to perform a communication with the device 
	 * @return the current position of the link
 	 */
	public abstract int getCurrentPosition();
	
	public double toEngineeringUnits(int value){
		return ((value-getHome())*getScale());
	}
	public int toLinkUnits(double euValue){
		return ((int) (euValue/getScale()))+getHome();
	}
	
	public void addLinkListener(ILinkListener l){
		if(links.contains(l))
			return;
		links.add(l);
	}
	public void removeLinkListener(ILinkListener l){
		if(links.contains(l))
			links.remove(l);
	}
	/**
	 * This method sends the updated angle value to all listeners
	 * 
	 * @param value in un-scaled link units. This method converts to an angle then sends to listeners. 
	 */
	public void fireLinkListener(int linkUnitsValue){
		for(ILinkListener l:links){
			//Log.info("Link Event, RAW="+linkUnitsValue);
			l.onLinkPositionUpdate(this,toEngineeringUnits(linkUnitsValue));
		}
	}
	
	
	
	public void Home(){
		setTargetValue(getHome());
		cacheTargetValue();
	}
	
	public void incrementEngineeringUnits(double inc){
		setTargetEngineeringUnits(targetEngineeringUnits+inc);
	}
	public void setTargetEngineeringUnits(double pos) {
		targetEngineeringUnits = pos;
		setPosition(toLinkUnits(targetEngineeringUnits));
	}

	public void setCurrentEngineeringUnits(double angle) {
		double current = (double)(getCurrentPosition()-getHome());
		if(current != 0)
			setScale(angle/current);
	}
	public double getCurrentEngineeringUnits(){
		int link = getCurrentPosition();
		double back = toEngineeringUnits(link);
		//Log.info("Link space: "+link+" Joint space: "+back);
		return back;
	}
	public double getTargetEngineeringUnits() {
		return toEngineeringUnits(getTargetValue());
	}
	public double getMaxEngineeringUnits() {
		return toEngineeringUnits(getUpperLimit());
	}
	public double getMinEngineeringUnits() {
		return toEngineeringUnits(getLowerLimit());
	}
	public boolean isMaxEngineeringUnits() {
		if(getTargetValue() == getUpperLimit()) {
			return true;
		}
		return false;
	}
	public boolean isMinEngineeringUnits() {
		if(getTargetValue() == getLowerLimit()) {
			return true;
		}
		return false;
	}
	
	protected void setPosition(int val) {
		//if(getTargetValue() != val){
			setTargetValue(val);
		//}
		cacheTargetValue();
	}
	private boolean useLimits=true;
	protected void setTargetValue(int val) {
		if(isUseLimits()){
			if(val>getUpperLimit()){
				this.targetValue = getUpperLimit();
				cacheTargetValue();
				throw new RuntimeException("Value upper limit bounded");
			}
			if(val<getLowerLimit()) {
				this.targetValue =getLowerLimit();
				cacheTargetValue();
				throw new RuntimeException("Value lower limit bounded");
			}
		}
		Log.info("Setting cached value :"+val);
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
	
	public void setCurrentAsUpperLimit() {
		setUpperLimit(getCurrentPosition());
	}
	
	public void setCurrentAsLowerLimit() {
		setLowerLimit(getCurrentPosition());
	}

	public void setUseLimits(boolean useLimits) {
		this.useLimits = useLimits;
	}

	public boolean isUseLimits() {
		return useLimits;
	}

	public void setLinkConfiguration(LinkConfiguration conf) {
		this.conf = conf;
	}

	public LinkConfiguration getLinkConfiguration() {
		return conf;
	}
	
	
}
