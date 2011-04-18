package com.neuronrobotics.sdk.dyio;

public class DyIOPowerEvent implements IDyIOEvent {
	private DyIOPowerState bankAState;
	private DyIOPowerState bankBState;
	private double voltage;
	public DyIOPowerEvent(DyIOPowerState bankA, DyIOPowerState bankB, double batteryVoltage) {
		bankAState = bankA;
		bankBState = bankB;
		setVoltage(batteryVoltage);
	}
	
	public DyIOPowerState getChannelAMode() {
		return bankAState;
	}
	
	public DyIOPowerState getChannelBMode() {
		return bankBState;
	}

	private void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public double getVoltage() {
		return voltage;
	}
	@Override 
	public String toString(){
		String s="";
		s+="Battery Voltage: "+getVoltage()+"V, Bank A state: "+bankAState+", Bank B state: "+bankBState;
		return s;
	}
}
