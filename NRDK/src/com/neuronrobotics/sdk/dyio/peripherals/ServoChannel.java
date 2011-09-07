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
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;

/**
 * 
 */
public class ServoChannel extends DyIOAbstractPeripheral {
	
	/**
	 * 
	 * 
	 * @param channel
	 */
	public ServoChannel(DyIOChannel channel){
		super(channel,DyIOChannelMode.SERVO_OUT,false);
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
		return SetPosition(pos, 0);
	}
	/**
	 * Steps the servo though a transformation over a given amount of time.
	 * 
	 * @param pos - the end position 
	 * @param time - the number of seconds for the transition to take place
	 * @return if the action was successful
	 */
	public boolean SetPosition(int pos, double time){
		return SetPosition( pos, (float) time);
	}
	
	/**
	 * Steps the servo though a transformation over a given amount of time.
	 * 
	 * @param pos - the end position 
	 * @param time - the number of seconds for the transition to take place
	 * @return if the action was successful
	 */
	public boolean SetPosition(int pos, float time){
		if(time == 0)
			time = (float) .001;
		if(!validate()) {
			return false;
		}
		getChannel().setCachedValue(pos);
		getChannel().setCachedTime(time);
		if(getChannel().getCachedMode()) {
			Log.debug(getClass()+" In cached mode, NOT flushing");
			return true;
		}
		
		return flush();
	}
	
	
	private boolean validate() {
		if(!isEnabled()) {
			//return false;
		}
		return getMode() == DyIOChannelMode.SERVO_OUT;
	}

	@Override
	public boolean hasAsync() {
		return false;
	}
}
