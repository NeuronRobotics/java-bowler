package com.neuronrobotics.sdk.pid;
public interface IPIDControl {
	public boolean ResetPIDChannel(int group);
	public boolean ResetPIDChannel(int group,int valueToSetCurrentTo);
	public boolean ConfigurePIDController(PIDConfiguration config);
	public PIDConfiguration getPIDConfiguration(int group);
	public boolean SetPIDSetPoint(int group,int setpoint,double seconds);
	public boolean SetAllPIDSetPoint(int []setpoints,double seconds);
	public int GetPIDPosition(int group);
	public int [] GetAllPIDPosition();
	public void addPIDEventListener(IPIDEventListener l);
	public void flushPIDChannels(double time);
	public boolean SetPIDVelicity(int group,int unitsPerSecond,double seconds);
}
