package com.neuronrobotics.sdk.commands.bcs.io;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.neuronrobotics.sdk.common.ISendable;

public enum AsyncThreshholdEdgeType implements ISendable{
	/**
#define		ASYN_BOTH 		0
#define		ASYN_RISING 	1
#define		ASYN_FALLING 	2
	 */
	/** The STATUS. */
	BOTH(0x00),
	
	/** The GET. */
	RISING(0x01),
	
	/** The POST. */
	FALLING(0x02);

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		String s="NOT VALID";
		switch (value){
		case 0x00:
			return "ASYN_BOTH";
		case 0x01:
			return "ASYN_RISING ";
		case 0x02:
			return "ASYN_FALLING";
		}
		return s;
	}
	
	/** The Constant lookup. */
	private static final Map<Byte,AsyncThreshholdEdgeType> lookup = new HashMap<Byte,AsyncThreshholdEdgeType>();
	
	static {
		for(AsyncThreshholdEdgeType cm : EnumSet.allOf(AsyncThreshholdEdgeType.class)) {
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
	private AsyncThreshholdEdgeType(int val) {
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
    public static AsyncThreshholdEdgeType get(byte code) { 
    	return lookup.get(code); 
    }

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	public byte[] getBytes() {
		byte [] b = {getValue()};
		return b;
	}
	

}
