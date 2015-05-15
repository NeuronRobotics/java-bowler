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
package com.neuronrobotics.sdk.dyio;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.neuronrobotics.sdk.common.ISendable;
/**
 * An enumeration of all the possible DyIO channel modes.
 * @author rbreznak
 *
 */
public enum DyIOChannelMode implements ISendable {
	NO_CHANGE     (0x00, "No Change"),
	OFF           (0x01, "Off"),
	DIGITAL_IN    (0x02, "Digital In"),
	DIGITAL_OUT   (0x03, "Digital Out"),
	ANALOG_IN     (0x04, "Analog In"),
	ANALOG_OUT    (0x05, "Analog Out"),
	PWM_OUT       (0x06, "PWM Out"),
	SERVO_OUT     (0x07, "Servo Out"),
	USART_TX      (0x08, "USART Tx"),
	USART_RX      (0x09, "USART Rx"),
	SPI_MOSI      (0x0A, "SPI MoSi"),
	SPI_MISO      (0x0B, "SPI MiSo"),
	SPI_CLOCK     (0x0C, "SPI Clock"),
	SPI_SELECT    (0x0D, "SPI Select"),
	COUNT_IN_INT  (0x0E, "Counter In Int"),
	COUNT_IN_DIR  (0x0F, "Counter In Dir"),
	COUNT_IN_HOME (0x10, "Counter In Home"),
	COUNT_OUT_INT (0x11, "Counter Out Int"),
	COUNT_OUT_DIR (0x12, "Counter Out Dir"),
	COUNT_OUT_HOME(0x13, "Counter Out Home"),
	DC_MOTOR_VEL  (0x14, "DC Motor Velocity"),
	DC_MOTOR_DIR  (0x15, "DC Motor Direction"),
	PPM_IN  	  (0x16, "PPM Reader");
	
	private static final Map<Byte,DyIOChannelMode> lookup = new HashMap<Byte,DyIOChannelMode>();
	private byte value;
	private String readableName;
	
	static {
		for(DyIOChannelMode cm : EnumSet.allOf(DyIOChannelMode.class)) {
			lookup.put(cm.getValue(), cm);
		}
	}

	private DyIOChannelMode(int val, String name) {
		value = (byte) val;
		readableName = name;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public static Collection<DyIOChannelMode> getModes() {
		return lookup.values();
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
    public static DyIOChannelMode get(byte code) { 
    	return lookup.get(code);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    public String toString() {
    	return readableName;
    }
    
    /**
	 * 
	 * 
	 * @return
	 */
    public String toSlug() {
    	return toString().replace(" ", "-").toLowerCase();
    }
    
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.ISendable#getBytes()
	 */
	 
	public byte[] getBytes() {
		byte [] b = {getValue()};
		return b;
	}
}
