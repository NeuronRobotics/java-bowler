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
package com.neuronrobotics.sdk.pid;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc

public enum PIDLimitEventType {
	

	LOWERLIMIT(0x00),
	
	INDEXEVENT(0x01),
	
	UPPERLIMIT(0x02),
	
	OVERCURRENT(0x03);
	
	/** The Constant lookup. */
	private static final Map<Byte,PIDLimitEventType> lookup = new HashMap<Byte,PIDLimitEventType>();
	
	static {
		for(PIDLimitEventType cm : EnumSet.allOf(PIDLimitEventType.class)) {
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
	private PIDLimitEventType(int val) {
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
    public static PIDLimitEventType get(byte code) { 
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
