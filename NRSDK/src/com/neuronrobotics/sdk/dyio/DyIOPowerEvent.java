package com.neuronrobotics.sdk.dyio;

public class DyIOPowerEvent implements IDyIOEvent {
	private DyIOPowerState bankAState;
	private DyIOPowerState bankBState;
	
	public DyIOPowerEvent(DyIOPowerState bankA, DyIOPowerState bankB) {
		bankAState = bankA;
		bankBState = bankB;
	}
	
	public DyIOPowerState getChannelAMode() {
		return bankAState;
	}
	
	public DyIOPowerState getChannelBMode() {
		return bankBState;
	}
}
