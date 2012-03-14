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
import com.neuronrobotics.sdk.common.ByteList;
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
	 * @param async 
	 * @throws Exception 
	 */
	public DyIOAbstractPeripheral(DyIOChannel channel, DyIOChannelMode myMode, boolean async) {
		this.channel = channel;
		this.enabled = true;
		if(channel.getMode() != myMode)
			channel.setMode(myMode, async);
	}
	/**
	 * This method retrieves the channel mode of this peripheral
	 * @return
	 */
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
	 * This sets the mode of this peripheral
	 * 
	 * @param mode the DyIO mode to set the channel to
	 * @return if the set worked. Not all channels have all peripherals
	 * @throws Exception 
	 */
	public boolean setMode(){
		return channel.setMode(getClassMode());
	}
	
	/**
	 * This method sets the current mode of this peripheral
	 * 
	 * @param mode the DyIO mode to set the channel to
	 * @param async If the channel should be set into async mode
	 * @return if the set worked. Not all channels have all peripherals
	 * @throws Exception 
	 */
	public boolean setMode( boolean async) {
		return channel.setMode(getClassMode(), async);
	}
	
	/**
	 * This method sets the current mode of this peripheral
	 * 
	 * @param mode the DyIO mode to set the channel to
	 * @param async If the channel should be set into async mode
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
	 * Returns the channel object used by the peripheral
	 * 
	 * @return returns the channel object
	 */
	 
	public DyIOChannel getChannel() {
		return channel.getChannel();
	}
	
	/**
	 * This method gets the current mode of this peripheral
	 * 
	 * @return returns the mode of this channel
	 */
	 
	public DyIOChannelMode getMode() {
		return channel.getMode();
	}
	
	/**
	 * This method sets the value of the output of the giver peripheral
	 * 
	 * @param value Sets this value to the channel
	 * @return if the set worked
	 * @throws InvalidResponseException
	 */
	 
	public boolean setValue(int value) throws InvalidResponseException {
		return channel.setValue(value);
	}
	
	/**
	 * This method sets the value of the output of the giver peripheral
	 * 
	 * @param value Sets this value to the channel
	 * @return if the set worked
	 * @throws InvalidResponseException
	 */
	 
	public boolean setValue(ByteList value)throws InvalidResponseException {
		return channel.setValue(value);
	}
	
	/**
	 * This method gets the value of the given peripheral
	 * 
	 * @return the value of the channel on the DyIO
	 * @throws InvalidResponseException
	 */
	 
	public int getValue()throws InvalidResponseException {
		return channel.getValue();
	}
	
	
	/**
	 * This method sets the value of the output of the giver peripheral, and also stores this value as the "default"
	 * value in non volatile memory to use at startup of the peripheral.
	 * 
	 * @param pos the position to set as the new starting point for the channel
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
	
	/**
	 * This method is to check if this peripheral is capable to be set as up as asynchronous. 
	 * @return if it cna be async
	 */
	abstract public boolean hasAsync();
	
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are not equal.
	 * This sets the sample time to 100 ms
	 * @return true is success
	 */
	public boolean configAdvancedAsyncNotEqual(){
		return getChannel().configAdvancedAsyncNotEqual(100);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are outside a deadband
	 * This sets the sample time to 100 ms
	 * @param deadbandSize the size in sensor units of the deadband
	 * @return true if success
	 */
	public boolean configAdvancedAsyncDeadBand(int deadbandSize){
		return  getChannel().configAdvancedAsyncDeadBand(100,deadbandSize);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value crosses a threshhold
	 * This sets the sample time to 100 ms
	 * @param threshholdValue a value setpoint that triggers an even when it is crossed
	 * @param edgeType Rising, Falling, or both
	 * @return true if success
	 */
	public boolean configAdvancedAsyncTreshhold(int threshholdValue,AsyncThreshholdEdgeType edgeType){
		return  getChannel().configAdvancedAsyncTreshhold(100, threshholdValue, edgeType);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value is sampled on a real-time 
	 * This sets the sample time to 100 ms
	 * clock and sent as async regardless of value change
	 * @return true if success
	 */
	public boolean configAdvancedAsyncAutoSample(){
		return  getChannel().configAdvancedAsyncAutoSample(100);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are not equal.
	 * @param msTime the sample time in MiliSeconds
	 * @return true if success
	 */
	public boolean configAdvancedAsyncNotEqual(int msTime){
		return getChannel().configAdvancedAsyncNotEqual(msTime);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the values are outside a deadband
	 * @param msTime the sample time in MiliSeconds
	 * @param deadbandSize the size in sensor units of the deadband
	 * @return true if success
	 */
	public boolean configAdvancedAsyncDeadBand(int msTime,int deadbandSize){
		return  getChannel().configAdvancedAsyncDeadBand(msTime,deadbandSize);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value crosses a threshhold
	 * @param msTime the sample time in MiliSeconds
	 * @param threshholdValue a value setpoint that triggers an even when it is crossed
	 * @param edgeType Rising, Falling, or both
	 * @return true if success
	 */
	public boolean configAdvancedAsyncTreshhold(int msTime,int threshholdValue, AsyncThreshholdEdgeType edgeType){
		return  getChannel().configAdvancedAsyncTreshhold(msTime, threshholdValue, edgeType);
	}
	/**
	 * This method configures the advanced async mode for a given DyIO channel to trigger on any event where the value is sampled on a real-time 
	 * clock and sent as async regardless of value change
	 * @param msTime the sample time in MiliSeconds
	 * @return true if success
	 */
	public boolean configAdvancedAsyncAutoSample(int msTime){
		return  getChannel().configAdvancedAsyncAutoSample(msTime);
	}
	
	/**
	 * THis method performs a cache flush on the channel wrapped by this object
	 * @return true if success
	 */
	public boolean flush() {
		return getChannel().flush();
	}
	/**
	 * This method sets the async mode for this peripheral 
	 * @param b if it should be async or not
	 */
	public void setAsync(boolean b) {
		channel.setMode(getClassMode(), b);
	}
}
