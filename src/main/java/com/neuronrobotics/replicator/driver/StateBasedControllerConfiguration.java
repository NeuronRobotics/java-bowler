package com.neuronrobotics.replicator.driver;

// TODO: Auto-generated Javadoc
/**
 * The Class StateBasedControllerConfiguration.
 */
public class StateBasedControllerConfiguration {

	/** The k p. */
	private double kP;
	
	/** The k i. */
	private double kI;
	
	/** The k d. */
	private double kD;
	
	/** The v kp. */
	private double vKP;
	
	/** The v kd. */
	private double vKD;
	
	/** The mm position resolution. */
	private double mmPositionResolution;
	
	/** The maximum m mper sec. */
	private double maximumMMperSec;
	
	/** The base radius. */
	private double baseRadius;
	
	/** The end effector radius. */
	private double endEffectorRadius;
	
	/** The max z. */
	private double maxZ;
	
	/** The min z. */
	private double minZ;
	
	/** The rod length. */
	private double rodLength;
	
	/** The use hard positioning. */
	private boolean useHardPositioning;
	
	/**
	 * Instantiates a new state based controller configuration.
	 *
	 * @param KP the kp
	 * @param KI the ki
	 * @param KD the kd
	 * @param VKP the vkp
	 * @param VKD the vkd
	 * @param mmPositionResolution the mm position resolution
	 * @param maximumMMperSec the maximum m mper sec
	 * @param BaseRadius the base radius
	 * @param EndEffectorRadius the end effector radius
	 * @param MaxZ the max z
	 * @param MinZ the min z
	 * @param RodLength the rod length
	 * @param useHardPositioning the use hard positioning
	 */
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
			double RodLength,
			boolean useHardPositioning
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
				setUseHardPositioning(useHardPositioning);
		
	}
	
	/**
	 * Instantiates a new state based controller configuration.
	 *
	 * @param data the data
	 */
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
				setUseHardPositioning((Boolean)data[12]);
	}
	
	/**
	 * Gets the data to send.
	 *
	 * @return the data to send
	 */
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
				  rodLength,
				  useHardPositioning
		};
		
		return ret;
	}

	/**
	 * Gets the k p.
	 *
	 * @return the k p
	 */
	public double getkP() {
		return kP;
	}

	/**
	 * Sets the k p.
	 *
	 * @param kP the new k p
	 */
	public void setkP(double kP) {
		this.kP = kP;
	}

	/**
	 * Gets the k i.
	 *
	 * @return the k i
	 */
	public double getkI() {
		return kI;
	}

	/**
	 * Sets the k i.
	 *
	 * @param kI the new k i
	 */
	public void setkI(double kI) {
		this.kI = kI;
	}

	/**
	 * Gets the k d.
	 *
	 * @return the k d
	 */
	public double getkD() {
		return kD;
	}

	/**
	 * Sets the k d.
	 *
	 * @param kD the new k d
	 */
	public void setkD(double kD) {
		this.kD = kD;
	}

	/**
	 * Gets the v kp.
	 *
	 * @return the v kp
	 */
	public double getvKP() {
		return vKP;
	}

	/**
	 * Sets the v kp.
	 *
	 * @param vKP the new v kp
	 */
	public void setvKP(double vKP) {
		this.vKP = vKP;
	}

	/**
	 * Gets the v kd.
	 *
	 * @return the v kd
	 */
	public double getvKD() {
		return vKD;
	}

	/**
	 * Sets the v kd.
	 *
	 * @param vKD the new v kd
	 */
	public void setvKD(double vKD) {
		this.vKD = vKD;
	}

	/**
	 * Gets the mm position resolution.
	 *
	 * @return the mm position resolution
	 */
	public double getMmPositionResolution() {
		return mmPositionResolution;
	}

	/**
	 * Sets the mm position resolution.
	 *
	 * @param mmPositionResolution the new mm position resolution
	 */
	public void setMmPositionResolution(double mmPositionResolution) {
		this.mmPositionResolution = mmPositionResolution;
	}

	/**
	 * Gets the maximum m mper sec.
	 *
	 * @return the maximum m mper sec
	 */
	public double getMaximumMMperSec() {
		return maximumMMperSec;
	}

	/**
	 * Sets the maximum m mper sec.
	 *
	 * @param maximumMMperSec the new maximum m mper sec
	 */
	public void setMaximumMMperSec(double maximumMMperSec) {
		this.maximumMMperSec = maximumMMperSec;
	}

	/**
	 * Gets the base radius.
	 *
	 * @return the base radius
	 */
	public double getBaseRadius() {
		return baseRadius;
	}

	/**
	 * Sets the base radius.
	 *
	 * @param baseRadius the new base radius
	 */
	public void setBaseRadius(double baseRadius) {
		this.baseRadius = baseRadius;
	}

	/**
	 * Gets the end effector radius.
	 *
	 * @return the end effector radius
	 */
	public double getEndEffectorRadius() {
		return endEffectorRadius;
	}

	/**
	 * Sets the end effector radius.
	 *
	 * @param endEffectorRadius the new end effector radius
	 */
	public void setEndEffectorRadius(double endEffectorRadius) {
		this.endEffectorRadius = endEffectorRadius;
	}

	/**
	 * Gets the max z.
	 *
	 * @return the max z
	 */
	public double getMaxZ() {
		return maxZ;
	}

	/**
	 * Sets the max z.
	 *
	 * @param maxZ the new max z
	 */
	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}

	/**
	 * Gets the min z.
	 *
	 * @return the min z
	 */
	public double getMinZ() {
		return minZ;
	}

	/**
	 * Sets the min z.
	 *
	 * @param minZ the new min z
	 */
	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}

	/**
	 * Gets the rod length.
	 *
	 * @return the rod length
	 */
	public double getRodLength() {
		return rodLength;
	}

	/**
	 * Sets the rod length.
	 *
	 * @param rodLength the new rod length
	 */
	public void setRodLength(double rodLength) {
		this.rodLength = rodLength;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
				"\r\n\tRod Length "+ rodLength+" mm "+
				"\r\n\tUse Hard Positioning="+ useHardPositioning
				;
	}

	/**
	 * Checks if is use hard positioning.
	 *
	 * @return true, if is use hard positioning
	 */
	public boolean isUseHardPositioning() {
		return useHardPositioning;
	}

	/**
	 * Sets the use hard positioning.
	 *
	 * @param useHardPositioning the new use hard positioning
	 */
	public void setUseHardPositioning(boolean useHardPositioning) {
		this.useHardPositioning = useHardPositioning;
	}
}
