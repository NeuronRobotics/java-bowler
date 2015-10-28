package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class PDVelocityConfiguration.
 */
public class PDVelocityConfiguration {
	
	/** The group. */
	private int group=0;
	
	/** The kp. */
	private double KP=1;
	
	/** The kd. */
	private double KD=0;

	/**
	 * Instantiates a new PD velocity configuration.
	 */
	public PDVelocityConfiguration(){
		
	}

	/**
	 * Instantiates a new PD velocity configuration.
	 *
	 * @param group This is the PID group this configuration object represents
	 * @param enabled True if the controller is running, false otherwise
	 * @param inverted This inverts the output value. Set true if the controller diverges
	 * @param async sets the flag to send this channels async values upstream
	 * @param KP Proportional constant
	 * @param KI Integral constant
	 * @param KD Derivative constant
	 * @param latch The value to latch into the PID controller if the home switch is hit (encoder only, not used in analog PID)
	 * @param useLatch Use the value to latch into the PID controller if the home switch is hit (encoder only, not used in analog PID)
	 * @param stopOnLatch Set the setpoint of the controller to current if home switch is hit (encoder only, not used in analog PID)
	 */
	public PDVelocityConfiguration(int group,boolean enabled,boolean inverted,boolean async,double KP,double KI,double KD, double latch, boolean useLatch, boolean stopOnLatch){
		setGroup(group);
		setKP(KP);
		setKD(KD);
	}
	
	/**
	 * Used to parse a PID configuration out of a PID packet.
	 *
	 * @param conf the conf
	 */

	public PDVelocityConfiguration(BowlerDatagram conf) {
		setGroup(   conf.getData().get(0));
		setKP(((double)ByteList.convertToInt(conf.getData().getBytes(1, 4),false))/100);
		setKD(((double)ByteList.convertToInt(conf.getData().getBytes(5, 4),false))/100);
	}
	
	/**
	 * Instantiates a new PD velocity configuration.
	 *
	 * @param args the args
	 */
	public PDVelocityConfiguration(Object[] args) {
		setGroup((Integer) args[0]);
		setKP((Double) args[1]);
		setKD((Double) args[2]);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s="PD Velocity configuration group #"+getGroup();
		s+="\n\tConstants: P="+getKP()+" D="+getKD();
		return s;
	}
	
	/**
	 * Sets the group.
	 *
	 * @param group the new group
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	
	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * Sets the kp.
	 *
	 * @param kP the new kp
	 */
	public void setKP(double kP) {
		KP = kP;
	}
	
	/**
	 * Gets the kp.
	 *
	 * @return the kp
	 */
	public double getKP() {
		return KP;
	}

	/**
	 * Sets the kd.
	 *
	 * @param kD the new kd
	 */
	public void setKD(double kD) {
		KD = kD;
	}
	
	/**
	 * Gets the kd.
	 *
	 * @return the kd
	 */
	public double getKD() {
		return KD;
	}

	/**
	 * Gets the args.
	 *
	 * @return the args
	 */
	public Object[] getArgs() {
		return  new Object[]{
				group,
				KP,
				KD,
				};
	}
}
