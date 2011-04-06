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

import com.neuronrobotics.sdk.commands.bcs.io.SetChannelValueCommand;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;

/**
 * 
 */
public class ServoChannel extends DyIOAbstractPeripheral {
	
	private float cachedTime=0;
	
	/**
	 * 
	 * 
	 * @param channel
	 */
	public ServoChannel(DyIOChannel channel){
		super(channel,DyIOChannelMode.SERVO_OUT);
		if(!setMode()) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + DyIOChannelMode.SERVO_OUT +  " mode");
		}
		channel.setDap(this);
	}
	
	/**
	 * Set the servo to a given position.
	 * 
	 * @param pos
	 * @return if the action was successful
	 */
	public boolean SetPosition(int pos){
		if(!validate()) {
			return false;
		}
		cachedTime=0;
		return setValue(pos);
	}
	
	/**
	 * Steps the servo though a transformation over a given amount of time.
	 * 
	 * @param pos - the end position 
	 * @param time - the number of seconds for the transition to take place
	 * @return if the action was successful
	 */
	public boolean SetPosition(int pos, float time){
		if(!validate()) {
			return false;
		}
		//System.out.println("Setting Servo to pos: "+pos);
		cachedTime=time;
		getChannel().setCachedValue(pos);
		if(getChannel().getCachedMode()) {
			return true;
		}
		return flush();
	}
	
	@Override
	public boolean flush() { 
		//System.out.println("Flushing servo");
		return flush(cachedTime);
	}
	
	private boolean flush(float time) {
		try {
			getChannel().send(new SetChannelValueCommand(getChannel().getChannelNumber(), getChannel().getCachedValue(), time, getMode()));
			cachedTime=0;
			return true;
		} catch (InvalidResponseException e) {
			return false;
		}
	}
	
	
	private boolean validate() {
		if(!isEnabled()) {
			//return false;
		}
		
		return getMode() == DyIOChannelMode.SERVO_OUT;
	}
}
