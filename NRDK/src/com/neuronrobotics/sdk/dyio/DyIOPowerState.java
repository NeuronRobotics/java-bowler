package com.neuronrobotics.sdk.dyio;

public enum DyIOPowerState {
	REGULATED,
	BATTERY_UNPOWERED,
	BATTERY_POWERED;
	
	public static DyIOPowerState valueOf(int code, double batteryVoltage) {
		switch(code) {
		case 1:
			return DyIOPowerState.REGULATED;
		case 0:
			if(batteryVoltage<5.0)
				return DyIOPowerState.BATTERY_UNPOWERED;
		default:
			return DyIOPowerState.BATTERY_POWERED;
		}
	}
	@Override
	public String toString(){
		String s="";
		switch(this){
		case BATTERY_POWERED:
			s="BATTERY POWERED";
			break;
		case BATTERY_UNPOWERED:
			s="BATTERY UN-POWERED";
			break;
		case REGULATED:
			s="REGULATED";
			break;
		}
		return s;
	}
}
