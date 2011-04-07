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

import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;

/**
 * 
 */
public class DigitalInputChannel extends DyIOAbstractPeripheral implements IChannelEventListener {
	
	private ArrayList<IDigitalInputListener> listeners = new ArrayList<IDigitalInputListener>();
	
	/**
	 * DigitalInputChannel.
	 * 
	 * @param channel
	 *            The channel object to set up as a Digital Input
	 * @param async
	 *            if this should be in async mode
	 */
	public DigitalInputChannel(DyIOChannel channel, boolean async){
		super(channel,DyIOChannelMode.DIGITAL_IN);
		channel.addChannelEventListener(this);
		 
		if(!setMode( async)) {
			throw new DyIOPeripheralException("Could not set channel " + getChannel() + " to " + DyIOChannelMode.DIGITAL_IN + " mode.");
		}	
	}
	
	/**
	 * DigitalInputChannel.
	 * 
	 * @param channel
	 *            The channel object to set up as a Digital Input
	 */
	public DigitalInputChannel(DyIOChannel channel){
		super(channel,DyIOChannelMode.DIGITAL_IN);
		channel.addChannelEventListener(this);
		
		if(!setMode( false)) {
			throw new DyIOPeripheralException("Could not set channel " + getChannel() + " to " + DyIOChannelMode.DIGITAL_IN + " mode.");
		}	
	}
	
	/**
	 * isHigh.
	 * 
	 * @return Checks to see if the pin is at logic high
	 */
	public boolean isHigh() {
		return getValue() != 0;
	}
	
	/**
	 * Set the channel to be asyncronous or syncronous.
	 * 
	 * @param isAsync - true if the channel should be set to asyncronous and syncronous, false if syncronous only
	 */
	public void setAsync(boolean isAsync) {
		setMode(DyIOChannelMode.DIGITAL_IN, isAsync);
	}
	
	/**
	 * removeAllDigitalInputListeners remove all the listeners.
	 */
	public void removeAllDigitalInputListeners() {
		listeners.clear();
	}
	
	/**
	 * removeDigitalInputListener.
	 * 
	 * @param l
	 *            remove the specified listener
	 */
	public void removeDigitalInputListener(IDigitalInputListener l) {
		if(!listeners.contains(l)) {
			return;
		}
		
		listeners.remove(l);
	}
	
	/**
	 * addDigitalInputListener.
	 * 
	 * @param l
	 *            add the specified listener
	 */
	public void addDigitalInputListener(IDigitalInputListener l) {
		if(listeners.contains(l)) {
			return;
		}
		
		listeners.add(l);
	}
	
	private void fireValueChanged(boolean value) {
		for(IDigitalInputListener l : listeners) {
			l.onDigitalValueChange(this, value);
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.IChannelEventListener#onChannelEvent(com.neuronrobotics.sdk.dyio.DyIOChannelEvent)
	 */
	@Override
	public void onChannelEvent(DyIOChannelEvent e) {
		fireValueChanged(e.getData().get(0) != 0);
	}

	@Override
	public boolean hasAsync() {
		return true;
	}
}
