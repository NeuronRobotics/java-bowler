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
package com.neuronrobotics.sdk.common;

import com.neuronrobotics.sdk.util.ThreadUtil;


// TODO: Auto-generated Javadoc
/**
 * The Class ThreadedTimeout.
 */
public class ThreadedTimeout extends Thread {
	
	/** The time. */
	private int time;
	
	/** The timed out. */
	private boolean timedOut = false;
	
	/**
	 * Instantiates a new threaded timeout.
	 *
	 * @param time the time
	 */
	public ThreadedTimeout(int time) {
		this.time = time;
	}
	
	/**
	 * Checks if is timed out.
	 *
	 * @return true, if is timed out
	 */
	public boolean isTimedOut() {
		return timedOut;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		timedOut = false;
		ThreadUtil.wait(time);
		timedOut = true;
	}
}
