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
package com.neuronrobotics.sdk.commands.bcs.io;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;

import com.neuronrobotics.sdk.common.ISendable;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.InvalidChannelOperationException;

/**
 * 
 */
public class SetChannelValueCommand extends BowlerAbstractCommand {	
	/**
	 * 
	 * 
	 * @param channel
	 * @param value
	 * @param mode
	 */
	public SetChannelValueCommand(int channel, int value, DyIOChannelMode mode) {
		setMethod(BowlerMethod.POST);
		setOpCode("schv");
		getCallingDataStorage().add(channel);
		switch(mode) {
		case ANALOG_OUT:
			getCallingDataStorage().addAs16(value);
			break;
		case COUNT_OUT_INT:
		case COUNT_OUT_DIR:
		case COUNT_OUT_HOME:
			getCallingDataStorage().addAs32(value);
			break;
		default:
			getCallingDataStorage().add(value);
			break;
		}
	}
	
	/**
	 * SetChannelValueCommand.
	 * 
	 * @param channel
	 *            the DyIO pin to use
	 * @param value
	 *            the value to set the pin to
	 * @param time
	 *            the time it should take for the action to be performed in
	 *            seconds
	 * @param mode
	 *            the mode the channel is in
	 */
	public SetChannelValueCommand(int channel, int value, float time, DyIOChannelMode mode) {
		setMethod(BowlerMethod.POST);
		setOpCode("schv");
		
		getCallingDataStorage().add(channel);
		
		if((mode == DyIOChannelMode.SERVO_OUT)){
			//
			getCallingDataStorage().add(value);
			//Time is in seconds, the converts to Ms then sends as 16 bit value
			getCallingDataStorage().addAs16((int)(time*1000));
		}else{
			getCallingDataStorage().add(value);
		}
	}
	
	/**
	 * 
	 * 
	 * @param channel
	 * @param data
	 */
	public SetChannelValueCommand(int channel, ISendable data) {
		setMethod(BowlerMethod.POST);
		setOpCode("schv");
		
		getCallingDataStorage().add(channel);
		getCallingDataStorage().add(data);
	}
	
	/**
	 * SetChannelValueCommand.
	 * 
	 * @param channel
	 *            the DyIO pin to use
	 * @param value
	 *            the value to set the pin to
	 * @param mode
	 *            the mode the channel is in
	 * @param saveTheValue
	 *            if true, the value is saved as the starting position for the
	 *            channel
	 */
	public SetChannelValueCommand(int channel, int value, DyIOChannelMode mode,boolean saveTheValue) {
		switch(mode) {
		case SERVO_OUT:
		case PWM_OUT:
			if(saveTheValue)
				setMethod(BowlerMethod.CRITICAL);
			else
				setMethod(BowlerMethod.POST);
			setOpCode("schv");
			getCallingDataStorage().add(channel);
			getCallingDataStorage().add(value);
			break;
		default:
			throw new InvalidChannelOperationException();
		}
	}
	/**
	 * SetChannelValueCommand.
	 * 
	 * @param channel
	 *            the DyIO pin to use
	 * @param value
	 *            the value to set the pin to
	 * @param mode
	 *            the mode the channel is in
	 * @param saveTheValue
	 *            if true, the value is saved as the starting position for the
	 *            channel
	 */
	public SetChannelValueCommand(int channel, int [] value, DyIOChannelMode mode) {
		switch(mode) {
		case PPM_IN:
			setMethod(BowlerMethod.POST);
			setOpCode("schv");
			getCallingDataStorage().add(channel);
			getCallingDataStorage().add(value);
			break;
		default:
			throw new InvalidChannelOperationException();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractCommand#parseResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram validate(BowlerDatagram data) throws InvalidResponseException {
		super.validate(data);
		if (data==null)
			throw new InvalidResponseException("Set Channel Value did not respond");
		if(!data.getRPC().equals("_rdy") && !data.getRPC().equals("schv") ) {
			throw new InvalidResponseException("Set Channel Value did not return '_rdy'.");
		}
		
		return data;
	}

}
