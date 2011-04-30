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
public enum BowlerMethod implements ISendable {
	
	/** The STATUS. */
	STATUS(0x00),
	
	/** The GET. */
	GET(0x10),
	
	/** The POST. */
	POST(0x20),
	
	/** The CRITICAL. */
	CRITICAL(0x30);
	
	/** The Constant lookup. */
	private static final Map<Byte,BowlerMethod> lookup = new HashMap<Byte,BowlerMethod>();
	
	static {
		for(BowlerMethod cm : EnumSet.allOf(BowlerMethod.class)) {
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
	private BowlerMethod(int val) {
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
    public static BowlerMethod get(byte code) { 
    	return lookup.get(code); 
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
		String s="NOT VALID";
		switch (value){
		case 0x00:
			return "STATUS";
		case 0x10:
			return "GET";
		case 0x20:
			return "POST";
		case 0x30:
			return "CRITICAL";
		}
		return s;
	}
}
