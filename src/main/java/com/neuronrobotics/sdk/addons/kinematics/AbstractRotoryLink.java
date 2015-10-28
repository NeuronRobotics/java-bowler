package com.neuronrobotics.sdk.addons.kinematics;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractRotoryLink.
 */
public abstract class AbstractRotoryLink extends AbstractLink {

	/**
	 * Instantiates a new abstract rotory link.
	 *
	 * @param conf the conf
	 */
	public AbstractRotoryLink(LinkConfiguration conf) {
		super(conf);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Increment angle.
	 *
	 * @param inc the inc
	 */
	public void incrementAngle(double inc){
		incrementEngineeringUnits(inc);
	}
	
	/**
	 * Sets the target angle.
	 *
	 * @param pos the new target angle
	 */
	public void setTargetAngle(double pos) {
		setTargetEngineeringUnits(pos);
	}
	
	/**
	 * Sets the current as angle.
	 *
	 * @param angle the new current as angle
	 */
	public void setCurrentAsAngle(double angle) {
		setCurrentEngineeringUnits(angle);
	}
	
	/**
	 * Gets the current angle.
	 *
	 * @return the current angle
	 */
	public double getCurrentAngle(){
		return getCurrentEngineeringUnits();
	}
	
	/**
	 * Gets the target angle.
	 *
	 * @return the target angle
	 */
	public double getTargetAngle() {
		return getTargetEngineeringUnits();
	}
	
	/**
	 * Gets the max angle.
	 *
	 * @return the max angle
	 */
	public double getMaxAngle() {
		return getMaxEngineeringUnits();
	}
	
	/**
	 * Gets the min angle.
	 *
	 * @return the min angle
	 */
	public double getMinAngle() {
		return getMinEngineeringUnits();
	}
	
	/**
	 * Checks if is max angle.
	 *
	 * @return true, if is max angle
	 */
	public boolean isMaxAngle() {
		return isMaxEngineeringUnits();
	}
	
	/**
	 * Checks if is min angle.
	 *
	 * @return true, if is min angle
	 */
	public boolean isMinAngle() {
		return isMinEngineeringUnits();
	}

}
