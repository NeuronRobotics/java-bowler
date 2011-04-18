package com.neuronrobotics.sdk.dyio;

public enum DyIOPowerState {
	REGULATED,
	BATTERY_UNPOWERED,
	BATTERY_POWERED;
	
	public static DyIOPowerState valueOf(int code) {
		switch(code) {
		case 1:
			return DyIOPowerState.REGULATED;
		case 0:
			return DyIOPowerState.BATTERY_UNPOWERED;
		default:
			return DyIOPowerState.BATTERY_POWERED;
		}
	}
}
