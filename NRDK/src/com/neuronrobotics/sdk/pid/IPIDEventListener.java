package com.neuronrobotics.sdk.pid;

public interface IPIDEventListener {
	public void onPIDEvent(PIDEvent e);
	public void onPIDLimitEvent(PIDLimitEvent e);
	public void onPIDReset(int group,int currentValue);
}
