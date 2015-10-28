package com.neuronrobotics.sdk.dyio;

// TODO: Auto-generated Javadoc
/**
 * The Class DyIOPowerEvent.
 */
public class DyIOPowerEvent implements IDyIOEvent {
	
	/** The bank a state. */
	private DyIOPowerState bankAState;
	
	/** The bank b state. */
	private DyIOPowerState bankBState;
	
	/** The voltage. */
	private double voltage;
	
	/**
	 * Instantiates a new dy io power event.
	 *
	 * @param bankA the bank a
	 * @param bankB the bank b
	 * @param batteryVoltage the battery voltage
	 */
	public DyIOPowerEvent(DyIOPowerState bankA, DyIOPowerState bankB, double batteryVoltage) {
		bankAState = bankA;
		bankBState = bankB;
		setVoltage(batteryVoltage);
	}
	
	/**
	 * Gets the channel a mode.
	 *
	 * @return the channel a mode
	 */
	public DyIOPowerState getChannelAMode() {
		return bankAState;
	}
	
	/**
	 * Gets the channel b mode.
	 *
	 * @return the channel b mode
	 */
	public DyIOPowerState getChannelBMode() {
		return bankBState;
	}

	/**
	 * Sets the voltage.
	 *
	 * @param voltage the new voltage
	 */
	private void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	/**
	 * Gets the voltage.
	 *
	 * @return the voltage
	 */
	public double getVoltage() {
		return voltage;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString(){
		String s="";
		s+="Battery Voltage: "+getVoltage()+"V, Bank A state: "+bankAState+", Bank B state: "+bankBState;
		return s;
	}
}
