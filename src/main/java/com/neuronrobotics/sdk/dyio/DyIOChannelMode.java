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
// TODO: Auto-generated Javadoc
/**
 * An enumeration of all the possible DyIO channel modes.
 * @author rbreznak
 *
 */
public enum DyIOChannelMode implements ISendable {
	
	/** The no change. */
	NO_CHANGE     (0x00, "No Change"),
	
	/** The off. */
	OFF           (0x01, "Off"),
	
	/** The digital in. */
	DIGITAL_IN    (0x02, "Digital In"),
	
	/** The digital out. */
	DIGITAL_OUT   (0x03, "Digital Out"),
	
	/** The analog in. */
	ANALOG_IN     (0x04, "Analog In"),
	
	/** The analog out. */
	ANALOG_OUT    (0x05, "Analog Out"),
	
	/** The pwm out. */
	PWM_OUT       (0x06, "PWM Out"),
	
	/** The servo out. */
	SERVO_OUT     (0x07, "Servo Out"),
	
	/** The usart tx. */
	USART_TX      (0x08, "USART Tx"),
	
	/** The usart rx. */
	USART_RX      (0x09, "USART Rx"),
	
	/** The spi mosi. */
	SPI_MOSI      (0x0A, "SPI MoSi"),
	
	/** The spi miso. */
	SPI_MISO      (0x0B, "SPI MiSo"),
	
	/** The spi clock. */
	SPI_CLOCK     (0x0C, "SPI Clock"),
	
	/** The spi select. */
	SPI_SELECT    (0x0D, "SPI Select"),
	
	/** The count in int. */
	COUNT_IN_INT  (0x0E, "Counter In Int"),
	
	/** The count in dir. */
	COUNT_IN_DIR  (0x0F, "Counter In Dir"),
	
	/** The count in home. */
	COUNT_IN_HOME (0x10, "Counter In Home"),
	
	/** The count out int. */
	COUNT_OUT_INT (0x11, "Counter Out Int"),
	
	/** The count out dir. */
	COUNT_OUT_DIR (0x12, "Counter Out Dir"),
	
	/** The count out home. */
	COUNT_OUT_HOME(0x13, "Counter Out Home"),
	
	/** The dc motor vel. */
	DC_MOTOR_VEL  (0x14, "DC Motor Velocity"),
	
	/** The dc motor dir. */
	DC_MOTOR_DIR  (0x15, "DC Motor Direction"),
	
	/** The ppm in. */
	PPM_IN  	  (0x16, "PPM Reader");
	
	/** The Constant lookup. */
	private static final Map<Byte,DyIOChannelMode> lookup = new HashMap<Byte,DyIOChannelMode>();
	
	/** The value. */
	private byte value;
	
	/** The readable name. */
	private String readableName;
	
	static {
		for(DyIOChannelMode cm : EnumSet.allOf(DyIOChannelMode.class)) {
			lookup.put(cm.getValue(), cm);
		}
	}

	/**
	 * Instantiates a new dy io channel mode.
	 *
	 * @param val the val
	 * @param name the name
	 */
	private DyIOChannelMode(int val, String name) {
		value = (byte) val;
		readableName = name;
	}
	
	/**
	 * Gets the modes.
	 *
	 * @return the modes
	 */
	public static Collection<DyIOChannelMode> getModes() {
		return lookup.values();
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
     * @return the dy io channel mode
     */
    public static DyIOChannelMode get(byte code) { 
    	return lookup.get(code);
    }
    
    /**
     * Gets the from slug.
     *
     * @param slug the slug
     * @return the from slug
     */
	public static DyIOChannelMode getFromSlug(String slug) {
		for (DyIOChannelMode cm : EnumSet.allOf(DyIOChannelMode.class)) {
			if(cm.toSlug().toLowerCase().contentEquals(slug.toLowerCase()))
				return cm;
		}
		throw new RuntimeException("No mode availible for slug: "+slug);
	}
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    public String toString() {
    	return readableName;
    }
    
    /**
     * To slug.
     *
     * @return the string
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
