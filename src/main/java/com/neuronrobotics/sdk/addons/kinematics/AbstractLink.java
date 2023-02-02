package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;


import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.IGcodeExecuter;
import com.neuronrobotics.sdk.addons.kinematics.imu.IMU;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.IFlushable;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.TickToc;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEventType;

import javafx.scene.transform.Affine;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractLink.
 */
// Kevin Shouldn't the Link's channel be kept in this level of Abstraction? The way I designg AbstractCartesianPositonDevice  Requires this
public abstract class AbstractLink implements  IFlushable{

	/** The target value. */
	private double targetValue=0;
	
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

	private Object linksLocation=null;
	
	/**
	 * The object for communicating IMU information and registering it with the hardware
	 */
	private IMU imu = new IMU();
	/**
	 * Override this method to specify a larger range
	 * @return the maximum value possible for a link
	 */
	public double getDeviceMaximumValue() {
		return conf.getDeviceTheoreticalMax();
	}
	/**
	 * Override this method to specify a larger range
	 * @return the minimum value possible for a link
	 */
	public double getDeviceMinimumValue() {
		return conf.getDeviceTheoreticalMin();
	}
	/**
	 * Override this method to specify a larger range
	 */
	public void setDeviceMaximumValue(double max) {
		 conf.setDeviceTheoreticalMax(max);
	}
	/**
	 * Override this method to specify a larger range

	 */
	public void setDeviceMinimumValue(double min) {
		 conf.setDeviceTheoreticalMin(min);
	}
	/**
	 * Gets the max engineering units.
	 *
	 * @return the max engineering units
	 */
	public double getDeviceMaxEngineeringUnits() {
		if(conf.getScale()>0)
			return toEngineeringUnits(getDeviceMaximumValue());
		else
			return toEngineeringUnits(getDeviceMinimumValue());
	}
	
	/**
	 * Gets the min engineering units.
	 *
	 * @return the min engineering units
	 */
	public double getDeviceMinEngineeringUnits() {
		if(conf.getScale()>0)
			return toEngineeringUnits(getDeviceMinimumValue());
		else
			return toEngineeringUnits(getDeviceMaximumValue());
	}
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
	public abstract double getCurrentPosition();
	
	/**
	 * To engineering units.
	 *
	 * @param value the value
	 * @return the double
	 */
	public double toEngineeringUnits(double value){
		return ((value-getHome())*getScale());
	}
	
	/**
	 * To link units.
	 *
	 * @param euValue the eu value
	 * @return the int
	 */
	public double toLinkUnits(double euValue){
		return  (euValue/getScale())+getHome();
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
	public void fireLinkListener(double linkUnitsValue){
		ArrayList<ILinkListener> links2 = getLinks();
		for (int i = 0; i < links2.size(); i++) {
			ILinkListener l = links2.get(i);
			//Log.info("Link Event, RAW="+linkUnitsValue);
			try {
				l.onLinkPositionUpdate(this,toEngineeringUnits(linkUnitsValue));
			}catch( Throwable t) {
				t.printStackTrace(System.out);
			}
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
		//TickToc.tic("Nan check link "+getLinkConfiguration().getLinkIndex());
		if(new Double(pos).isNaN()) {
			new RuntimeException("Setpopint in setTargetEngineeringUnits can not be set to nan").printStackTrace();
			return;
		}
		//TickToc.tic("Nan check link done");
		targetEngineeringUnits = pos;
		double linkUnits = toLinkUnits(targetEngineeringUnits);
		//TickToc.tic("to link units");
		setPosition(linkUnits);
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
		double link = getCurrentPosition();
		if(new Double(link).isNaN())
			link=0;
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
	 * Sets the upper limit.
	 *
	 * @param upperLimit the new upper limit
	 */
	public void setMinEngineeringUnits(double minLimit ) {
		if(conf.getScale()>0)
			conf.setLowerLimit( toLinkUnits(minLimit));
		else
			conf.setUpperLimit( toLinkUnits(minLimit));
	}
	
	/**
	 * Sets the lower limit.
	 *
	 * @param lowerLimit the new lower limit
	 */
	public void setMaxEngineeringUnits(double maxLimit) {
		if(conf.getScale()>0)
			conf.setUpperLimit( toLinkUnits(maxLimit));
		else
			conf.setLowerLimit( toLinkUnits(maxLimit));
	}
	
	
	/**
	 * Gets the max engineering units.
	 *
	 * @return the max engineering units
	 */
	public double getMaxVelocityEngineeringUnits() {
		return Math.abs(toEngineeringUnits(conf.getUpperVelocity()));
	}
	/**
	 * Gets the max engineering units.
	 *
	 * @return the max engineering units
	 */
	public void setMaxVelocityEngineeringUnits(double max) {
		conf.setUpperVelocity(toLinkUnits(max)); 
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
	protected void setPosition(double val) {
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
	protected void setTargetValue(double val) {
		//TickToc.tic("setTargetValue nan check");

		if(new Double(val).isNaN()) {
			new RuntimeException("Setpopint in virtual device can not be set to nan").printStackTrace();
			return;
		}
		//TickToc.tic("setTargetValue nan check done ");
		Log.info("Setting cached value :"+val);
		this.targetValue = val;
		for(LinkConfiguration c:slaveLinks){
			//generate the links
			AbstractLink link = getSlaveFactory().getLink(c);
			link.setTargetValue(targetValue);
		}
		//TickToc.tic("followers set ");
	
		double ub = getMaxEngineeringUnits();
		double lb = getMinEngineeringUnits();
		boolean flip = getScale()<0;
		boolean belowLower = targetValue<getLowerLimit();
		boolean aboveUpper = targetValue>getUpperLimit();
		String execpt = "Attempted="+toEngineeringUnits(targetValue)+" (engineering units) Device Units="+targetValue
				+" \nUpper Bound="+ub+" (engineering units) Device Units="+getUpperLimit()
				+ "\nLower Bound="+lb+" (engineering units) Device Units="+getLowerLimit();
		if(flip?belowLower:aboveUpper){
			this.targetValue = flip?getLowerLimit():getUpperLimit();
			for(LinkConfiguration c:slaveLinks){
				//generate the links
				AbstractLink link = getSlaveFactory().getLink(c);
				link.setTargetValue(targetValue);
			}
			cacheTargetValue();
			fireLinkLimitEvent(
					new PIDLimitEvent(
							conf.getHardwareIndex(),
							targetValue ,
							PIDLimitEventType.UPPERLIMIT,
							System.currentTimeMillis()
							)
					);
			if(isUseLimits())throw new RuntimeException("Joint hit Upper software bound\n"+execpt);
		}
		if(flip?aboveUpper:belowLower) {
			this.targetValue =flip?getUpperLimit():getLowerLimit();
			for(LinkConfiguration c:slaveLinks){
				//generate the links
				AbstractLink link = getSlaveFactory().getLink(c);
				link.setTargetValue(targetValue);
			}
			cacheTargetValue();
			fireLinkLimitEvent(
					new PIDLimitEvent(
							conf.getHardwareIndex(),
							targetValue ,
							PIDLimitEventType.LOWERLIMIT,
							System.currentTimeMillis()
							)
					);
			if(isUseLimits())throw new RuntimeException("Joint hit Lower software bound\n"+execpt);
			
		}else{
			Log.info("Abstract Link: limits disabled");
		}
		//TickToc.tic("link bound set done");
	}

	/**
	 * Gets the target value.
	 *
	 * @return the target value
	 */
	public double getTargetValue() {
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
	public double getUpperLimit() {
		return (double) conf.getUpperLimit();
	}

	/**
	 * Gets the lower limit.
	 *
	 * @return the lower limit
	 */
	public double getLowerLimit() {
		return  conf.getLowerLimit();
	}

	/**
	 * Gets the home.
	 *
	 * @return the home
	 */
	public double getHome() {
		return  conf.getStaticOffset();
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

	public void setGlobalPositionListener(Object Object) {
//		if(!Affine.class.isInstance(Object)) {
//			RuntimeException runtimeException = new RuntimeException("Must be an Affine");
//			runtimeException.printStackTrace();
//			throw runtimeException;
//		}
		this.linksLocation = Object;
	}

	public Object getGlobalPositionListener() {
		return linksLocation;
	}
	public LinkFactory getSlaveFactory() {
		return slaveFactory;
	}
	public void setSlaveFactory(LinkFactory slaveFactory) {
		this.slaveFactory = slaveFactory;
	}

	public IMU getImu() {
		return imu;
	}
	public void addChangeListener(ILinkConfigurationChangeListener l) {
		conf.addChangeListener(l);
	}
	public void removeChangeListener(ILinkConfigurationChangeListener l) {
		conf.removeChangeListener(l);
	}
	public void clearChangeListener() {
		conf.clearChangeListener();
	}
}
