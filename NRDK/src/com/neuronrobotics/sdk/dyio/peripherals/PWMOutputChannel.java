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

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIORegestry;


/**
 * 
 */
public class PWMOutputChannel extends DyIOAbstractPeripheral {
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public PWMOutputChannel(int channel){
		this(DyIORegestry.get().getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public PWMOutputChannel(DyIO dyio,int channel){
		this(dyio.getChannel(channel));	
	}
	
	
	/**
	 * PWMOutputChannel.
	 * 
	 * @param channel
	 *            The channel object to set up as a full duty, hardware PWM
	 */
	public PWMOutputChannel(DyIOChannel channel) {
		super(channel,DyIOChannelMode.PWM_OUT,false);
	
		if(!setMode()) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to PWM output mode");
		}
	}
	
	/**
	 * This takes in a duty cycle in percent time on
	 * 100 is 100% on and 0 is 0% on. 
	 * @param duty
	 */
	public void SetDutyPercent(float duty){
		
		if (duty > 100) { 
			duty = 100;
		}
		
		if (duty<0) {
			duty=0;	
		}
		
		setValue((int)((duty / 100) * 255));
	}

	@Override
	public boolean hasAsync() {
		return false;
	}
}
