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

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;

/**
 * 
 */
public class AnalogInputChannel extends DyIOAbstractPeripheral implements IChannelEventListener {
	
	public static final int ADCRESOLUTION = 1024;
	public static final int ADCVOLTAGE = 5;
	
	private ArrayList<IAnalogInputListener> listeners = new ArrayList<IAnalogInputListener>();
	
	/**
	 * Constructor.
	 * Creates an analog input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public AnalogInputChannel(int channel){
		this(DyIORegestry.get().getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an analog input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public AnalogInputChannel(DyIO dyio,int channel){
		this(dyio.getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an analog input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public AnalogInputChannel(DyIOChannel channel){
		this(channel,true);	
	}
	
	/**
	 * Constructor.
	 * Creates an analog input channel with the given ability for asyncronous communications.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 * @param async - boolean to determine if this is an async analog channel
	 */
	public AnalogInputChannel(DyIOChannel channel, boolean async){
		super(channel,DyIOChannelMode.ANALOG_IN,async);
		channel.addChannelEventListener(this);
		
		if(!setMode(async)) {
			throw new DyIOPeripheralException("Could not set channel " + getChannel() + " to " + DyIOChannelMode.ANALOG_IN +  " mode.");
		}	
	}
	

	
	/**
	 * Gets the value of the channel as a percentage.
	 * 
	 * @return a percent of the max voltage the is seeing
	 */
	public double getScaledValue() {
		return scaleValue(getValue());
	}
	
	/**
	 * Scales the value to the voltage read on the channel (between 0v and 5v).
	 * 
	 * @return the voltage on the pin
	 */
	public double getVoltage(){
		return getScaledValue() * ADCVOLTAGE;
	}
	
	/**
	 * Set the channel to be asyncronous or syncronous.
	 * 
	 * @param isAsync
	 */
	public void setAsync(boolean isAsync) {
		setMode(DyIOChannelMode.ANALOG_IN, isAsync);
	}
	
	/**
	 * removeAllAnalogInputListeners clears the list of async packet listeners.
	 */
	public void removeAllAnalogInputListeners() {
		listeners.clear();
	}
	
	/**
	 * removeAnalogInputListener.
	 * 
	 * @param l
	 *            remove the specified listener
	 */
	public void removeAnalogInputListener(IAnalogInputListener l) {
		if(!listeners.contains(l)) {
			return;
		}
		
		listeners.remove(l);
	}
	
	/**
	 * addAnalogInputListener.
	 * 
	 * @param l
	 *            add the specified listener
	 */
	public void addAnalogInputListener(IAnalogInputListener l) {
		if(listeners.contains(l)) {
			return;
		}
		
		listeners.add(l);
	}
	
	private void fireValueChanged(double value) {
		try{
			for(IAnalogInputListener l : listeners) {
				l.onAnalogValueChange(this, value);
			}
		}catch(Exception e){
			// TODO find out why this exception gets thrown
			Log.error(e.getMessage());
		}
	}
	
	private static double scaleValue(int value) {
		return (((double) value)/ADCRESOLUTION);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.IChannelEventListener#onChannelEvent(com.neuronrobotics.sdk.dyio.DyIOChannelEvent)
	 */
	 
	public void onChannelEvent(DyIOChannelEvent e) {
		fireValueChanged(ByteList.convertToInt(e.getData().getBytes()));
	}
	
	
	public boolean hasAsync() {
		return true;
	}
}
