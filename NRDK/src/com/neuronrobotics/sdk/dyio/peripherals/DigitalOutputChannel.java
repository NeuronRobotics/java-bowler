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
package com.neuronrobotics.sdk.dyio.peripherals;

import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;

/**
 * 
 */
public class DigitalOutputChannel extends DyIOAbstractPeripheral {
	
	/**
	 * DigitalOutChannel.
	 * 
	 * @param channel
	 *            The channel object to set up as a digital output
	 */
	public DigitalOutputChannel(DyIOChannel channel) {
		super(channel,DyIOChannelMode.DIGITAL_OUT,false);
	
		if(!setMode()) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to digital out mode");
		}
	}
	
	/**
	 * setHigh.
	 * 
	 * @param high
	 *            boolean to set high or not high
	 * @return if it worked
	 */
	public boolean setHigh(boolean high) {
		return setValue(high?1:0);
	}
	
	/**
	 * isHigh.
	 * 
	 * @return the state of the pin
	 */
	public boolean isHigh() {
		return getValue() != 0;
	}

	@Override
	public boolean hasAsync() {
		return false;
	}
}
