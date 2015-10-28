package com.neuronrobotics.sdk.addons.kinematics;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractPrismaticLink.
 */
public abstract class AbstractPrismaticLink extends AbstractLink {

	/**
	 * Instantiates a new abstract prismatic link.
	 *
	 * @param conf the conf
	 */
	public AbstractPrismaticLink(LinkConfiguration conf) {
		super(conf);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Increment displacment.
	 *
	 * @param inc the inc
	 */
	public void incrementDisplacment(double inc){
		incrementEngineeringUnits(inc);
	}
	
	/**
	 * Sets the target displacment.
	 *
	 * @param pos the new target displacment
	 */
	public void setTargetDisplacment(double pos) {
		setTargetEngineeringUnits(pos);
	}
	
	/**
	 * Sets the current as displacment.
	 *
	 * @param Displacment the new current as displacment
	 */
	public void setCurrentAsDisplacment(double Displacment) {
		setCurrentEngineeringUnits(Displacment);
	}
	
	/**
	 * Gets the current displacment.
	 *
	 * @return the current displacment
	 */
	public double getCurrentDisplacment(){
		return getCurrentEngineeringUnits();
	}
	
	/**
	 * Gets the target displacment.
	 *
	 * @return the target displacment
	 */
	public double getTargetDisplacment() {
		return getTargetEngineeringUnits();
	}
	
	/**
	 * Gets the max displacment.
	 *
	 * @return the max displacment
	 */
	public double getMaxDisplacment() {
		return getMaxEngineeringUnits();
	}
	
	/**
	 * Gets the min displacment.
	 *
	 * @return the min displacment
	 */
	public double getMinDisplacment() {
		return getMinEngineeringUnits();
	}
	
	/**
	 * Checks if is max displacment.
	 *
	 * @return true, if is max displacment
	 */
	public boolean isMaxDisplacment() {
		return isMaxEngineeringUnits();
	}
	
	/**
	 * Checks if is min displacment.
	 *
	 * @return true, if is min displacment
	 */
	public boolean isMinDisplacment() {
		return isMinEngineeringUnits();
	}
}
