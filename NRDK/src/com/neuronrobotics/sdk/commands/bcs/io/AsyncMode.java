package com.neuronrobotics.sdk.commands.bcs.io;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import com.neuronrobotics.sdk.common.ISendable;

public enum AsyncMode implements ISendable{
	/**
	 * 	#define		NOTEQUAL 		0
		#define		DEADBAND 		1
		#define		THRESHHOLD 		2
		#define		AUTOSAMP 		3
	 */
	/** The STATUS. */
	NOTEQUAL(0x00),
	
	/** The GET. */
	DEADBAND(0x01),
	
	/** The POST. */
	THRESHHOLD(0x02),
	
	/** The CRITICAL. */
	AUTOSAMP(0x03);
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		String s="NOT VALID";
		switch (value){
		case 0x00:
			return "NOTEQUAL";
		case 0x01:
			return "DEADBAND";
		case 0x02:
			return "THRESHHOLD";
		case 0x03:
			return "AUTOSAMP";
		}
		return s;
	}
	
	/** The Constant lookup. */
	private static final Map<Byte,AsyncMode> lookup = new HashMap<Byte,AsyncMode>();
	
	static {
		for(AsyncMode cm : EnumSet.allOf(AsyncMode.class)) {
			lookup.put(cm.getValue(), cm);
		}
	}
	
	/** The value. */
	private byte value;
	
	/**
	 * Instantiates a new bowler method.
	 *
	 * @param val the val
	 */
	private AsyncMode(int val) {
		value = (byte) val;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public byte getValue() {
		return value; 
	}

    /**
     * Gets the.
     *
     * @param code the code
     * @return the bowler method
     */
    public static AsyncMode get(byte code) { 
    	return lookup.get(code); 
    }

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		byte [] b = {getValue()};
		return b;
	}
	

}
