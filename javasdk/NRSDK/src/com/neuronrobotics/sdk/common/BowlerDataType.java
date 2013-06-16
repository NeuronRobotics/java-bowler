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
	
	/** 8 bit int */
	I08(8),
	
	/** 16 bit int */
	I16(16),
	
	/** 32 bit int */
	I32(32),
	
	/** stream of 8 bit ints*/
	STR(37),
	
	/** stream of 32 bit ints*/
	I32STR(38),
	
	/** String*/
	ASCII(39),
	
	/** Fixed point times 100*/
	FIXED100(41),
	
	/** Fixed point times 1000*/
	FIXED1k(42),
	
	/** Unknown**/
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
			return "(Int 8)";
		case 16:
			return "(Int 16)";
		case 32:
			return "(Int 32)";
		case 37:
			return "(Int 8 Stream)";
		case 38:
			return "(Int 32 Stream)";
		case 39:
			return "(ASCII)";
		case 41:
			return "(Fixed 100)";
		case 42:
			return "(Fixed 1k)";
		}
		return s;
	}
}
