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


import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;


/**
 * 
 */
public class CounterOutputChannel extends DyIOAbstractPeripheral {
	
	/**
	 * CounterChannel.
	 * 
	 * @param channel
	 *            The channel object to set up as counter
	 */
	public CounterOutputChannel(DyIOChannel channel) {
		super(channel,DyIOChannelMode.COUNT_OUT_INT);
		
		DyIOChannelMode mode = DyIOChannelMode.COUNT_OUT_INT;
		
		if(!setMode()) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + mode + " mode.");
		}

	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral#setValue(int)
	 */
	@Override
	public boolean setValue(int value){
		Log.info("Setting counter set point");
		ByteList b = new ByteList();
		b.addAs32(value);
		return setValue(b);
	}

}
