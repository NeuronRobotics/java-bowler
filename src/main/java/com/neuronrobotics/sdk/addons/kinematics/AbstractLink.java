package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import javafx.scene.transform.Affine;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractLink.
 */
// Kevin Shouldn't the Link's channel be kept in this level of Abstraction? The way I designg AbstractCartesianPositonDevice  Requires this
public abstract class AbstractLink {

	/** The target value. */
	private int targetValue=0;
	
	/** The target engineering units. */
	private double targetEngineeringUnits=0;
	
	/** The links. */
	private ArrayList<ILinkListener> links = new ArrayList<ILinkListener>();
	
	/** The conf. */
	private LinkConfiguration conf =null;
	private ArrayList<LinkConfiguration> slaveLinks;
	private LinkFactory slaveFactory = new LinkFactory();
	/** The use limits. */
	private boolean useLimits=true;

	private Affine linksLocation=new Affine();
	
	/**
	 * Instantiates a new abstract link.
	 *
	 * @param conf the conf
	 */
	public AbstractLink(LinkConfiguration conf){
		this.conf=conf;
		slaveLinks = conf.getSlaveLinks();
		if(slaveLinks.size()>0)
			System.out.println(conf.getName()+" has slaves: "+slaveLinks.size());
		for(LinkConfiguration c:slaveLinks){
			//generate the links
			getSlaveFactory().getLink(c);
		}
	}
	/**
	 * This method is called in order to take the target value and pass it to the implementation's target value
	 * This method should not alter the position of the implementations link
	 * If the implementation target does not handle chached values, this should be chached in code.
	 */
	public void cacheTargetValue(){
		for(LinkConfiguration c:slaveLinks){
			//generate the links
			AbstractLink link = getSlaveFactory().getLink(c);
			link.cacheTargetValue();
		}
		cacheTargetValueDevice();
	}
	
	/**
	 * This method is called in order to take the target value and pass it to the implementation's target value
	 * This method should not alter the position of the implementations link
	 * If the implementation target does not handle chached values, this should be chached in code.
	 */
	public abstract void cacheTargetValueDevice();
	
	/**
	 * This method will force one link to update its position in the given time (seconds).
	 *
	 * @param time (seconds) for the position update to take
	 */
	public void flush(double time){
		for(LinkConfiguration c:slaveLinks){
			//generate the links
			AbstractLink link = getSlaveFactory().getLink(c);
			link.flush(time);
		}
		flushDevice(time);
	}
	
	/**
	 * This method will force one link to update its position in the given time (seconds)
	 * This will also flush the host controller.
	 *
	 * @param time (seconds) for the position update to take
	 */
	public void flushAll(double time){
		for(LinkConfiguration c:slaveLinks){
			//generate the links
			AbstractLink link = getSlaveFactory().getLink(c);
			link.flushAll(time);
		}
		flushAllDevice(time);
	}
	
	/**
	 * This method will force one link to update its position in the given time (seconds).
	 *
	 * @param time (seconds) for the position update to take
	 */
	public abstract void flushDevice(double time);
	
	/**
	 * This method will force one link to update its position in the given time (seconds)
	 * This will also flush the host controller.
	 *
	 * @param time (seconds) for the position update to take
	 */
	public abstract void flushAllDevice(double time);
	
	/**
	 * This method should return the current position of the link
	 * This method is expected to perform a communication with the device .
	 *
	 * @return the current position of the link
	 */
	public abstract int getCurrentPosition();
	
	/**
	 * To engineering units.
	 *
	 * @param value the value
	 * @return the double
	 */
	public double toEngineeringUnits(int value){
		return ((value-getHome())*getScale());
	}
	
	/**
	 * To link units.
	 *
	 * @param euValue the eu value
	 * @return the int
	 */
	public int toLinkUnits(double euValue){
		return ((int) (euValue/getScale()))+getHome();
	}
	
	/**
	 * Adds the link listener.
	 *
	 * @param l the l
	 */
	public void addLinkListener(ILinkListener l){
		//Log.info("Adding link listener: "+l);
		if(getLinks().contains(l))
			return;
		getLinks().add(l);
	}
	
	/**
	 * Removes the link listener.
	 *
	 * @param l the l
	 */
	public void removeLinkListener(ILinkListener l){
		//Log.info("Removing link listener: "+l);
		if(getLinks().contains(l))
			getLinks().remove(l);
		//throw new RuntimeException();
	}
	
	/**
	 * This method sends the updated angle value to all listeners.
	 *
	 * @param linkUnitsValue the link units value
	 */
	public void fireLinkListener(int linkUnitsValue){
		for(ILinkListener l:getLinks()){
			//Log.info("Link Event, RAW="+linkUnitsValue);
			l.onLinkPositionUpdate(this,toEngineeringUnits(linkUnitsValue));
		}
	}
	
	/**
	 * This fires off a limit event.
	 *
	 * @param e the e
	 */
	public void fireLinkLimitEvent(PIDLimitEvent e){
		for(ILinkListener l:getLinks()){
			//Log.info("Link Event, RAW="+linkUnitsValue);
			l.onLinkLimit(this, e);
		}
	}
	
	/**
	 * Home.
	 */
	public void Home(){
		setTargetValue(getHome());
		cacheTargetValue();
	}
	
	/**
	 * Increment engineering units.
	 *
	 * @param inc the inc
	 */
	public void incrementEngineeringUnits(double inc){
		setTargetEngineeringUnits(targetEngineeringUnits+inc);
	}
	
	/**
	 * Sets the target engineering units.
	 *
	 * @param pos the new target engineering units
	 */
	public void setTargetEngineeringUnits(double pos) {
		targetEngineeringUnits = pos;
		setPosition(toLinkUnits(targetEngineeringUnits));
	}

	/**
	 * Sets the current engineering units.
	 *
	 * @param angle the new current engineering units
	 */
	public void setCurrentEngineeringUnits(double angle) {
		double current = (double)(getCurrentPosition()-getHome());
		if(current != 0)
			conf.setScale(angle/current);
	}
	
	/**
	 * Gets the current engineering units.
	 *
	 * @return the current engineering units
	 */
	public double getCurrentEngineeringUnits(){
		int link = getCurrentPosition();
		double back = toEngineeringUnits(link);
		//Log.info("Link space: "+link+" Joint space: "+back);
		return back;
	}
	
	/**
	 * Gets the target engineering units.
	 *
	 * @return the target engineering units
	 */
	public double getTargetEngineeringUnits() {
		return toEngineeringUnits(getTargetValue());
	}
	
	/**
	 * Gets the max engineering units.
	 *
	 * @return the max engineering units
	 */
	public double getMaxEngineeringUnits() {
		if(conf.getScale()>0)
			return toEngineeringUnits(getUpperLimit());
		else
			return toEngineeringUnits(getLowerLimit());
	}
	
	/**
	 * Gets the min engineering units.
	 *
	 * @return the min engineering units
	 */
	public double getMinEngineeringUnits() {
		if(conf.getScale()>0)
			return toEngineeringUnits(getLowerLimit());
		else
			return toEngineeringUnits(getUpperLimit());
	}
	
	/**
	 * Checks if is max engineering units.
	 *
	 * @return true, if is max engineering units
	 */
	public boolean isMaxEngineeringUnits() {
		if(getTargetValue() == getUpperLimit()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is min engineering units.
	 *
	 * @return true, if is min engineering units
	 */
	public boolean isMinEngineeringUnits() {
		if(getTargetValue() == getLowerLimit()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the position.
	 *
	 * @param val the new position
	 */
	protected void setPosition(int val) {
		//if(getTargetValue() != val){
			setTargetValue(val);
		//}
		for(LinkConfiguration c:slaveLinks){
			//generate the links
			AbstractLink link = getSlaveFactory().getLink(c);
			link.cacheTargetValue();
		}
		cacheTargetValue();
	}
	

	
	/**
	 * Sets the target value.
	 *
	 * @param val the new target value
	 */
	protected void setTargetValue(int val) {
		Log.info("Setting cached value :"+val);
		this.targetValue = val;
		for(LinkConfiguration c:slaveLinks){
			//generate the links
			AbstractLink link = getSlaveFactory().getLink(c);
			link.setTargetValue(targetValue);
		}
		if(isUseLimits()){
			double ub = getMaxEngineeringUnits();
			double lb = getMinEngineeringUnits();
			String execpt = "Attempted="+toEngineeringUnits(targetValue)+" (engineering units) Device Units="+targetValue
					+" \nUpper Bound="+ub+" (engineering units) Device Units="+getUpperLimit()
					+ "\nLower Bound="+lb+" (engineering units) Device Units="+getLowerLimit();
			if(val>getUpperLimit()){
				this.targetValue = getUpperLimit();
				for(LinkConfiguration c:slaveLinks){
					//generate the links
					AbstractLink link = getSlaveFactory().getLink(c);
					link.setTargetValue(targetValue);
				}
				cacheTargetValue();
				throw new RuntimeException("Joint hit Upper software bound\n"+execpt);
			}
			if(val<getLowerLimit()) {
				this.targetValue =getLowerLimit();
				for(LinkConfiguration c:slaveLinks){
					//generate the links
					AbstractLink link = getSlaveFactory().getLink(c);
					link.setTargetValue(targetValue);
				}
				cacheTargetValue();
				throw new RuntimeException("Joint hit Lower software bound\n"+execpt);
			}
		}else{
			Log.info("Abstract Link: limits disabled");
		}
	}

	/**
	 * Gets the target value.
	 *
	 * @return the target value
	 */
	public int getTargetValue() {
		return targetValue;
	}
	
	/**
	 * Sets the upper limit.
	 *
	 * @param upperLimit the new upper limit
	 */
	public void setUpperLimit(int upperLimit) {
		conf.setUpperLimit(upperLimit);
	}
	
	/**
	 * Sets the lower limit.
	 *
	 * @param lowerLimit the new lower limit
	 */
	public void setLowerLimit(int lowerLimit) {
		conf.setLowerLimit(lowerLimit);
	}
	
	/**
	 * Sets the home.
	 *
	 * @param home the new home
	 */
	public void setHome(int home) {
		conf.setStaticOffset(home);
	}

	/**
	 * Sets the scale.
	 *
	 * @param d the new scale
	 */
	public void setScale(double d) {
		conf.setScale(d);
	}

	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public double getScale() {
		return conf.getScale();
	}	

	/**
	 * Gets the upper limit.
	 *
	 * @return the upper limit
	 */
	public int getUpperLimit() {
		return (int) conf.getUpperLimit();
	}

	/**
	 * Gets the lower limit.
	 *
	 * @return the lower limit
	 */
	public int getLowerLimit() {
		return (int) conf.getLowerLimit();
	}

	/**
	 * Gets the home.
	 *
	 * @return the home
	 */
	public int getHome() {
		return (int) conf.getStaticOffset();
	}
	
	/**
	 * Sets the current as upper limit.
	 */
	public void setCurrentAsUpperLimit() {
		conf.setUpperLimit(getCurrentPosition());
	}
	
	/**
	 * Sets the current as lower limit.
	 */
	public void setCurrentAsLowerLimit() {
		conf.setLowerLimit(getCurrentPosition());
	}

	/**
	 * Sets the use limits.
	 *
	 * @param useLimits the new use limits
	 */
	public void setUseLimits(boolean useLimits) {
		this.useLimits = useLimits;
	}

	/**
	 * Checks if is use limits.
	 *
	 * @return true, if is use limits
	 */
	public boolean isUseLimits() {
		return useLimits;
	}

	/**
	 * Sets the link configuration.
	 *
	 * @param conf the new link configuration
	 */
	public void setLinkConfiguration(LinkConfiguration conf) {
		this.conf = conf;
	}

	/**
	 * Gets the link configuration.
	 *
	 * @return the link configuration
	 */
	public LinkConfiguration getLinkConfiguration() {
		return conf;
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public ArrayList<ILinkListener> getLinks() {
		return links;
	}

	/**
	 * Sets the links.
	 *
	 * @param links the new links
	 */
	public void setLinks(ArrayList<ILinkListener> links) {
		this.links = links;
	}

	/**
	 * Removes the all link listener.
	 */
	public void removeAllLinkListener() {
		links.clear();
	}

	public void setGlobalPositionListener(Affine affine) {
		this.linksLocation = affine;
	}

	public Affine getGlobalPositionListener() {
		return linksLocation;
	}
	public LinkFactory getSlaveFactory() {
		return slaveFactory;
	}
	public void setSlaveFactory(LinkFactory slaveFactory) {
		this.slaveFactory = slaveFactory;
	}
	
}
