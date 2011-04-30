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

import com.neuronrobotics.sdk.commands.bcs.io.AsyncThreshholdEdgeType;
import com.neuronrobotics.sdk.commands.bcs.io.SetChannelValueCommand;
import com.neuronrobotics.sdk.common.ISendable;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.IDyIOChannel;

/**
 * 
 */
public abstract class DyIOAbstractPeripheral implements IDyIOChannel {
	private DyIOChannel channel;
	private boolean enabled = false;
	/**
	 * DyIOAbstractPeripheral.
	 * 
	 * @param channel
	 *            The channel object to set up as whatever peripheral is needed
	 * @throws Exception 
	 */
	public DyIOAbstractPeripheral(DyIOChannel channel, DyIOChannelMode myMode) {
		this.channel = channel;
		this.enabled = true;
		if(channel.getMode() != myMode)
			channel.setMode(myMode, false);
	}
	
	public  DyIOChannelMode getClassMode() {
		return channel.getMode();
	}
	
	/**
	 * isEnabled.
	 * 
	 * @return if the channel is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * setMode.
	 * 
	 * @param mode
	 *            the DyIO mode to set the channel to
	 * @return if the set worked. Not all channels have all peripherals
	 * @throws Exception 
	 */
	public boolean setMode(){
		return setMode( false);
	}
	
	/**
	 * setMode.
	 * 
	 * @param mode
	 *            the DyIO mode to set the channel to
	 * @param async
	 *            If the channel should be set into async mode
	 * @return if the set worked. Not all channels have all peripherals
	 * @throws Exception 
	 */
	public boolean setMode( boolean async) {
		return channel.setMode(getClassMode(), async);
	}
	
	/**
	 * setMode.
	 * 
	 * @param mode
	 *            the DyIO mode to set the channel to
	 * @param async
	 *            If the channel should be set into async mode
	 * @return if the set worked. Not all channels have all peripherals
	 * @throws Exception 
	 * @throws InvalidResponseException
	 */
	public boolean setMode(DyIOChannelMode mode, boolean async) {
		if(mode != getClassMode())
			throw new RuntimeException("The mode being set does not match the defined channel mode: "+mode+" is not"+getClassMode());
		return channel.setMode(getClassMode(), async);
	}
	
	/**
	 * getChannel.
	 * 
	 * @return returns the channel object
	 */
	 
	public DyIOChannel getChannel() {
		return channel.getChannel();
	}
	
	/**
	 * getMode.
	 * 
	 * @return returns the mode of this channel
	 */
	 
	public DyIOChannelMode getMode() {
		return channel.getMode();
	}
	
	/**
	 * setValue.
	 * 
	 * @param value
	 *            Sets this value to the channel
	 * @return if the set worked
	 * @throws InvalidResponseException
	 */
	 
	public boolean setValue(int value) throws InvalidResponseException {
		return channel.setValue(value);
	}
	
	/**
	 * setValue.
	 * 
	 * @param value
	 *            Sets this value to the channel
	 * @return if the set worked
	 * @throws InvalidResponseException
	 */
	 
	public boolean setValue(ISendable value)throws InvalidResponseException {
		return channel.setValue(value);
	}
	
	/**
	 * getValue.
	 * 
	 * @return the value of the channel on the DyIO
	 * @throws InvalidResponseException
	 */
	 
	public int getValue()throws InvalidResponseException {
		return channel.getValue();
	}
	
	
	/**
	 * SavePosition.
	 * 
	 * @param pos
	 *            the position to set as the new starting point for the channel
	 * @return if the save worked or not.
	 */
	public boolean SavePosition(int pos){
		try {
			DyIOChannelMode mode = getChannel().getMode();
			switch(mode){
			case SERVO_OUT:
			case PWM_OUT :
				getChannel().send(new SetChannelValueCommand(getChannel().getChannelNumber(), pos , getMode(), true));
				return true;
			default:
				return false;
			}
			
		} catch (InvalidResponseException e) {
			return false;
		}
	}
	
	abstract public boolean hasAsync();
	
	
	public boolean configAdvancedAsyncNotEqual(){
		return getChannel().configAdvancedAsyncNotEqual(100);
	}
	public boolean configAdvancedAsyncDeadBand(int deadbandSize){
		return  getChannel().configAdvancedAsyncDeadBand(100,deadbandSize);
	}
	public boolean configAdvancedAsyncTreshhold(int threshholdValue, AsyncThreshholdEdgeType edgeType){
		return  getChannel().configAdvancedAsyncTreshhold(100, threshholdValue, edgeType);
	}
	public boolean configAdvancedAsyncAutoSample(){
		return  getChannel().configAdvancedAsyncAutoSample(100);
	}
	
	public boolean configAdvancedAsyncNotEqual(int msTime){
		return getChannel().configAdvancedAsyncNotEqual(msTime);
	}
	public boolean configAdvancedAsyncDeadBand(int msTime,int deadbandSize){
		return  getChannel().configAdvancedAsyncDeadBand(msTime,deadbandSize);
	}
	public boolean configAdvancedAsyncTreshhold(int msTime,int threshholdValue, AsyncThreshholdEdgeType edgeType){
		return  getChannel().configAdvancedAsyncTreshhold(msTime, threshholdValue, edgeType);
	}
	public boolean configAdvancedAsyncAutoSample(int msTime){
		return  getChannel().configAdvancedAsyncAutoSample(msTime);
	}
	

	public boolean flush() {
		return getChannel().flush();
	}

	public void setAsync(boolean b) {
		channel.setMode(getClassMode(), b);
	}
}
