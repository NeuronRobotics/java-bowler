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
import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIORegestry;

/**
 * 
 */
public class ServoChannel extends DyIOAbstractPeripheral {
	private ArrayList<IServoPositionUpdateListener >listeners = new ArrayList<IServoPositionUpdateListener >();
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public ServoChannel(int channel){
		this(DyIORegestry.get().getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public ServoChannel(DyIO dyio,int channel){
		this(dyio.getChannel(channel));	
	}
	
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
		firePositionUpdate(pos,time);
		getChannel().setCachedValue(pos);
		getChannel().setCachedTime(time);
		if(getChannel().getCachedMode()) {
			Log.debug(getClass()+" In cached mode, NOT flushing on Set");
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
	
	private void firePositionUpdate(int pos,double time){
		for(IServoPositionUpdateListener s:listeners){
			s.onServoPositionUpdate(this, pos,time);
		}
	}
	
	public void addIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		if(listeners.contains(l))
			return;
		listeners.add(l);
	}
	public void removeIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		if(listeners.contains(l))
			listeners.remove(l);
	}
	
	public void enablePowerOverride(){
		getChannel().getDevice().send(new powerOverridePacket(true) );
	}
	public void disablePowerOverride(){
		getChannel().getDevice().send(new powerOverridePacket(false) );
	}
	
	private class powerOverridePacket extends BowlerAbstractCommand{
		public powerOverridePacket(boolean ovr){
			setMethod(BowlerMethod.CRITICAL);
			setOpCode("povr");
			getCallingDataStorage().add(ovr?1:0);
		}
	}
	
}
