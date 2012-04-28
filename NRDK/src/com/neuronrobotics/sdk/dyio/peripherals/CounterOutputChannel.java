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
public class CounterOutputChannel extends DyIOAbstractPeripheral implements IChannelEventListener{
	private ArrayList<ICounterOutputListener> listeners = new ArrayList<ICounterOutputListener>();
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public CounterOutputChannel(int channel){
		this(DyIORegestry.get().getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public CounterOutputChannel(DyIO dyio,int channel){
		this(dyio.getChannel(channel));	
	}
	
	/**
	 * Constructor.
	 * Creates an counter input input channel that is syncronous only by default.
	 * 
	 * @param channel - the channel object requested from the DyIO
	 */
	public CounterOutputChannel(DyIOChannel channel){
		this(channel,true);	
	}

	public CounterOutputChannel(DyIOChannel channel,boolean isAsync) {
		super(channel,DyIOChannelMode.COUNT_OUT_INT,isAsync);
		init(channel,isAsync);
	}
	private void init(DyIOChannel channel,boolean isAsync){
		DyIOChannelMode mode = DyIOChannelMode.COUNT_OUT_INT;
		channel.addChannelEventListener(this);
		if(!channel.setMode(mode, isAsync)) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + mode + " mode.");
		}
		channel.resync(true);
	}
	
	/**
	 * Set the Counter to a given position.
	 * 
	 * @param pos
	 * @return if the action was successful
	 */
	public boolean SetPosition(int pos){
		return SetPosition(pos, 0);
	}
	
	/**
	 * Steps the Counter though a transformation over a given amount of time.
	 * 
	 * @param pos - the end position 
	 * @param time - the number of seconds for the transition to take place
	 * @return if the action was successful
	 */
	public boolean SetPosition(int pos, float time){
		if(!validate()) {
			return false;
		}
		getChannel().setCachedValue(pos);
		getChannel().setCachedTime(time);
		if(getChannel().getCachedMode()) {
			return true;
		}
		return flush();
	}
	
	
	/**
	 * addCounterOutputListener.
	 * 
	 * @param l
	 *            add this listener to this channels event listeners
	 */
	public void addCounterOutputListener(ICounterOutputListener l) {
		if(listeners.contains(l)) {
			return;
		}
		
		listeners.add(l);
	}
	
	/**
	 * removeCounterOutputListener.
	 * 
	 * @param l
	 *            remove this listener to this channels event listeners
	 */
	public void removeCounterOutputListener(ICounterOutputListener l) {
		if(!listeners.contains(l)) {
			return;
		}
		
		listeners.add(l);
	}
	
	/**
	 * 
	 */
	public void removeAllCounterOutputListeners() {
		listeners.clear();
	}
	
	/**
	 * 
	 * 
	 * @param value
	 */
	protected void fireOnCounterOutput(int value) {
		for(ICounterOutputListener l : listeners) {
			l.onCounterValueChange(this, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral#setValue(int)
	 */
	 
	public boolean setValue(int value){
		Log.info("Setting counter set point");
		ByteList b = new ByteList();
		b.addAs32(value);
		return setValue(b);
	}
	/**
	 * onChannelEvent Send the counter value to all the listening objects.
	 * 
	 * @param e
	 */
	 
	public void onChannelEvent(DyIOChannelEvent e) {
		fireOnCounterOutput(e.getSignedValue());
	}
	 
	public boolean hasAsync() {
		return true;
	}
	/**
	 * 
	 * 
	 * @param isAsync
	 */
	public void setAsync(boolean isAsync) {
		setMode(DyIOChannelMode.COUNT_OUT_INT, isAsync);
	}
	private boolean validate() {
		if(!isEnabled()) {
			//return false;
		}
		return getMode() == DyIOChannelMode.COUNT_OUT_INT;
	}

}
