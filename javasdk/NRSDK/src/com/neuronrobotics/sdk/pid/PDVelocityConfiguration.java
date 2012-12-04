package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;

public class PDVelocityConfiguration {
	private int group=0;
	private double KP=1;
	private double KD=0;

	public PDVelocityConfiguration(){
		
	}

	/**
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
	 * Used to parse a PID configuration out of a PID packet
	 * @param conf
	 */

	public PDVelocityConfiguration(BowlerDatagram conf) {
		setGroup(   conf.getData().get(0));
		setKP(((double)ByteList.convertToInt(conf.getData().getBytes(1, 4),false))/100);
		setKD(((double)ByteList.convertToInt(conf.getData().getBytes(5, 4),false))/100);
	}
	@Override
	public String toString(){
		String s="PD Velocity configuration group #"+getGroup();
		s+="\n\tConstants: P="+getKP()+" D="+getKD();
		return s;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getGroup() {
		return group;
	}

	public void setKP(double kP) {
		KP = kP;
	}
	public double getKP() {
		return KP;
	}

	public void setKD(double kD) {
		KD = kD;
	}
	public double getKD() {
		return KD;
	}
}
