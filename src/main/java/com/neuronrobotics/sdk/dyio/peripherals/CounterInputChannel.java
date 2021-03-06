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
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;

// TODO: Auto-generated Javadoc
/**
 * The Class CounterInputChannel.
 */
public class CounterInputChannel extends DyIOAbstractPeripheral implements IChannelEventListener {
	
	/** The listeners. */
	private ArrayList<ICounterInputListener> listeners = new ArrayList<ICounterInputListener>();
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public CounterInputChannel(int channel){
		this(((DyIO) DeviceManager.getSpecificDevice(DyIO.class, null)).getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 *
	 * @param dyio the dyio
	 * @param channel - the channel object requested from the DyIO
	 */
	public CounterInputChannel(DyIO dyio,int channel){
		this(dyio.getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public CounterInputChannel(DyIOChannel channel){
		this(channel,true);	
	}
	
	/**
	 * Instantiates a new counter input channel.
	 *
	 * @param channel the channel
	 * @param isAsync the is async
	 */
	public CounterInputChannel(DyIOChannel channel,boolean isAsync) {
		super(channel,DyIOChannelMode.COUNT_IN_INT,isAsync);
		init(channel,isAsync);
	}
	
	/**
	 * Inits the.
	 *
	 * @param channel the channel
	 * @param isAsync the is async
	 */
	private void init(DyIOChannel channel,boolean isAsync){
		DyIOChannelMode mode = DyIOChannelMode.COUNT_IN_INT;
		channel.addChannelEventListener(this);
		if(!channel.setMode(mode, isAsync)) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + mode + " mode.");
		}
		channel.resync(true);
	}
	
	/**
	 * addCounterInputListener.
	 * 
	 * @param l
	 *            add this listener to this channels event listeners
	 */
	public void addCounterInputListener(ICounterInputListener l) {
		if(listeners.contains(l)) {
			return;
		}
		
		listeners.add(l);
	}
	
	/**
	 * removeCounterInputListener.
	 * 
	 * @param l
	 *            remove this listener to this channels event listeners
	 */
	public void removeCounterInputListener(ICounterInputListener l) {
		if(!listeners.contains(l)) {
			return;
		}
		
		listeners.add(l);
	}
	
	/**
	 * Removes the all counter input listeners.
	 */
	public void removeAllCounterInputListeners() {
		listeners.clear();
	}
	
	/**
	 * Fire on counter input.
	 *
	 * @param value the value
	 */
	protected void fireOnCounterInput(int value) {
		for(ICounterInputListener l : listeners) {
			l.onCounterValueChange(this, value);
		}
	}
	
	/**
	 * onChannelEvent Send the counter value to all the listening objects.
	 *
	 * @param e the e
	 */
	 
	public void onChannelEvent(DyIOChannelEvent e) {
		fireOnCounterInput(e.getSignedValue());
	}
	
	/**
	 * Sets the async.
	 *
	 * @param isAsync the new async
	 */
	public void setAsync(boolean isAsync) {
		setMode(DyIOChannelMode.COUNT_IN_INT, isAsync);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral#setValue(int)
	 */
	 
	public boolean setValue(int value){
		ByteList b = new ByteList();
		b.addAs32(value);
		return setValue(b);
	}

	 
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral#hasAsync()
	 */
	public boolean hasAsync() {
		return true;
	}
}
