package com.neuronrobotics.sdk.addons.kinematics;

public abstract class AbstractRotoryLink extends AbstractLink {

	public AbstractRotoryLink(int home, int lowerLimit, int upperLimit,double scale) {
		super(home, lowerLimit, upperLimit, scale);
		// TODO Auto-generated constructor stub
	}
	
	public void incrementAngle(double inc){
		incrementEngineeringUnits(inc);
	}
	public void setTargetAngle(double pos) {
		setTargetEngineeringUnits(pos);
	}
	public void setCurrentAsAngle(double angle) {
		setCurrentEngineeringUnits(angle);
	}
	public double getCurrentAngle(){
		return getCurrentEngineeringUnits();
	}
	public double getTargetAngle() {
		return getTargetEngineeringUnits();
	}
	public double getMaxAngle() {
		return getMaxEngineeringUnits();
	}
	public double getMinAngle() {
		return getMinEngineeringUnits();
	}
	public boolean isMaxAngle() {
		return isMaxEngineeringUnits();
	}
	public boolean isMinAngle() {
		return isMinEngineeringUnits();
	}

}
