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
public class DCMotorOutputChannel extends DyIOAbstractPeripheral {
	
	/**
	 * DCMotorOutputChannel.
	 * 
	 * @param channel
	 *            The channel object to set up as a full duty, hardware PWM
	 */
	public DCMotorOutputChannel(DyIOChannel channel) {
		super(channel,DyIOChannelMode.DC_MOTOR_VEL);
	
		if(!setMode()) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to DC motor output mode");
		}
	}
	
	/**
	 * This takes in a velocity from 100 to -100
	 * 100 is full on forward, -100 is full on backward and 0 is stop
	 * @param velocity
	 */
	public void SetVelocity(float velocity){
		
		if (velocity > 100) { 
			velocity = 100;
		}
		
		if (velocity<-100) {
			velocity=-100;	
		}
		
		setValue((int)((velocity / 100) * 128)+128);
	}

	@Override
	public boolean hasAsync() {
		return false;
	}
}
