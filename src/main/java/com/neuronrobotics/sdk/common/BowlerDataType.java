/*******************************************************************************
 * Copyright 2010 Neuron Robotics, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.neuronrobotics.sdk.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Enum BowlerMethod.
 */
public enum BowlerDataType implements ISendable {
	
	/**  8 bit unsigned int. */
	I08(8),
	
	/**  16 bit signed int. */
	I16(16),
	
	/**  32 bit signed int. */
	I32(32),
	
	/**  stream of 8 bit  unsigned ints, first byte is unsigned char to indicate number of data values in the stream. */
	STR(37),
	
	/**  stream of 32 bit  signed ints, first byte is unsigned char to indicate number of data values in the stream. */
	I32STR(38),
	
	/**  ASCII String null terminated. */
	ASCII(39),
	
	/**  Signed Fixed point times 100. */
	FIXED100(41),
	
	/**  Signed Fixed point times 1000. */
	FIXED1k(42),
	
	/**  Boolean value, 0 is false, not 0 is true. */
	BOOL(43),
	
	/**  stream of floats, first byte is unsigned char to indicate number of data values in the stream. */
	FIXED1k_STR(44),
	
	/**  Unknown*. */
	INVALID(0);
	
	
	/** The Constant lookup. */
	private static final Map<Byte,BowlerDataType> lookup = new HashMap<Byte,BowlerDataType>();
	/** The value. */
	private byte value=0;
	
	static {
		for(BowlerDataType cm : EnumSet.allOf(BowlerDataType.class)) {
			lookup.put(cm.getValue(), cm);
		}
	}

	
	/**
	 * Instantiates a new bowler method.
	 *
	 * @param val the val
	 */
	private BowlerDataType(int val) {
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
    public static BowlerDataType get(byte code) {
    	try{
    		BowlerDataType tmp =lookup.get(code);
    		if(tmp == null){
    			throw new RuntimeException("Unrecognized Bowler data type "+code);
    		}
    		return tmp;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    	return BowlerDataType.INVALID;
    	 
    }

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	public byte[] getBytes() {
		byte [] b = {getValue()};
		return b;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		String s="(NOT VALID)";
		switch (value){
		case 8:
			return "(char)";
		case 16:
			return "(short)";
		case 32:
			return "(int)";
		case 37:
			return "(char [])";
		case 38:
			return "(int [])";
		case 39:
			return "(String)";
		case 41:
			return "(Fixed 100)";
		case 42:
			return "(Fixed 1k)";
		case 43:
			return "(Boolean)";
		}
		return s;
	}
}
