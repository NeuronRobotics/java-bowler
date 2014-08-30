package com.neuronrobotics.replicator.driver;

public class StateBasedControllerConfiguration {

	private double kP;
	private double kI;
	private double kD;
	private double vKP;
	private double vKD;
	private double mmPositionResolution;
	private double maximumMMperSec;
	private double baseRadius;
	private double endEffectorRadius;
	private double maxZ;
	private double minZ;
	private double rodLength;

	public StateBasedControllerConfiguration(
		    double KP,
		    double KI,
		    double KD,
		    double VKP,
		    double VKD,
		    double mmPositionResolution,
		    double maximumMMperSec,	    
			double BaseRadius,
			double EndEffectorRadius,
			double MaxZ,
			double MinZ,
			double RodLength
		    ){
				setkP(KP);
				setkI(KI);
				setkD(KD);
				setvKP(VKP);
				setvKD(VKD);
				setMmPositionResolution(mmPositionResolution);
				setMaximumMMperSec(maximumMMperSec);
				setBaseRadius(BaseRadius);
				setEndEffectorRadius(EndEffectorRadius);
				setMaxZ(MaxZ);
				setMinZ(MinZ);
				setRodLength(RodLength);
		
	}
	
	public StateBasedControllerConfiguration(Object [] data){
				setkP((Double)data[0]);
				setkI((Double)data[1]);
				setkD((Double)data[2]);
				setvKP((Double)data[3]);
				setvKD((Double)data[4]);
				setMmPositionResolution((Double)data[5]);
				setMaximumMMperSec((Double)data[6]);
				setBaseRadius((Double)data[7]);
				setEndEffectorRadius((Double)data[8]);
				setMaxZ((Double)data[9]);
				setMinZ((Double)data[10]);
				setRodLength((Double)data[11]);
		
	}
	
	public Object [] getDataToSend(){
		Object [] ret =new Object []{
				  kP ,
				  kI ,
				  kD ,
				  vKP ,
				  vKD ,
				  mmPositionResolution ,
				  maximumMMperSec ,
				  baseRadius ,
				  endEffectorRadius ,
				  maxZ ,
				  minZ ,
				  rodLength
		};
		
		return ret;
	}

	public double getkP() {
		return kP;
	}

	public void setkP(double kP) {
		this.kP = kP;
	}

	public double getkI() {
		return kI;
	}

	public void setkI(double kI) {
		this.kI = kI;
	}

	public double getkD() {
		return kD;
	}

	public void setkD(double kD) {
		this.kD = kD;
	}

	public double getvKP() {
		return vKP;
	}

	public void setvKP(double vKP) {
		this.vKP = vKP;
	}

	public double getvKD() {
		return vKD;
	}

	public void setvKD(double vKD) {
		this.vKD = vKD;
	}

	public double getMmPositionResolution() {
		return mmPositionResolution;
	}

	public void setMmPositionResolution(double mmPositionResolution) {
		this.mmPositionResolution = mmPositionResolution;
	}

	public double getMaximumMMperSec() {
		return maximumMMperSec;
	}

	public void setMaximumMMperSec(double maximumMMperSec) {
		this.maximumMMperSec = maximumMMperSec;
	}

	public double getBaseRadius() {
		return baseRadius;
	}

	public void setBaseRadius(double baseRadius) {
		this.baseRadius = baseRadius;
	}

	public double getEndEffectorRadius() {
		return endEffectorRadius;
	}

	public void setEndEffectorRadius(double endEffectorRadius) {
		this.endEffectorRadius = endEffectorRadius;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}

	public double getMinZ() {
		return minZ;
	}

	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}

	public double getRodLength() {
		return rodLength;
	}

	public void setRodLength(double rodLength) {
		this.rodLength = rodLength;
	}
	
	@Override
	public String toString(){
		return "Configuration: "+
				"\r\n\tKP "+ kP+"  "+
			    "\r\n\tKI "+ kI+"  "+
			    "\r\n\tKD "+ kD+"  "+
			    "\r\n\tVkP "+ vKP+"  "+
			    "\r\n\tVkD "+ vKD+"  "+
			    "\r\n\tresolution "+ mmPositionResolution+" mm"+
			    "\r\n\tmax feed rate "+ maximumMMperSec+" mm/sec "+	    
				"\r\n\tBase Radius "+ baseRadius+" mm "+
				"\r\n\tEnd Effector Radius "+ endEffectorRadius+" mm "+
				"\r\n\tMax Z "+ maxZ+" mm "+
				"\r\n\tMin Z "+ minZ+" mm "+
				"\r\n\tRod Length "+ rodLength+" mm "
				;
	}
}
