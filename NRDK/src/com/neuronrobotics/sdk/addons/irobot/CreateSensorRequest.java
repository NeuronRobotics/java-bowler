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
package com.neuronrobotics.sdk.addons.irobot;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public enum CreateSensorRequest {
	ALL(0x00),
	IO(0x01),
	DRIVE(0x02),
	BATTERY(0x03),
	NONE(0xFF);
	private byte value;
	private static final Map<Byte,CreateSensorRequest> lookup = new HashMap<Byte,CreateSensorRequest>();
	private CreateSensorRequest(int val) {
		value = (byte) val;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public byte getValue() {
		return value; 
	}
	
	/**
	 * 
	 * 
	 * @param code
	 * @return
	 */
	public static CreateSensorRequest get(byte code) { 
    	return lookup.get(code); 
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		switch (this){
		case ALL:
			return "All sensors";
		case IO:
			return "IO sensors sensors";
		case DRIVE:
			return "Drive sensors";
		case BATTERY:
			return "Battery sensors";
		default:
			return "Unknown, dont know how that would happen";
		}
	}
}
