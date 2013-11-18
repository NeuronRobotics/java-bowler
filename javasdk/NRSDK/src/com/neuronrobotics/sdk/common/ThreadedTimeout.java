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
public class ThreadedTimeout {
	
	/** The time. */
	private int time;
	private Thread timerThread;
	
	/** The timed out. */
	private boolean timedOut = true;
	private IthreadedTimoutListener listener;
	
	
	
	/**
	 * Instantiates a new threaded timeout.
	 *
	 * @param time the time
	 */
	public ThreadedTimeout(int time) {
		this.time = time;
		timerThread = new Thread(){
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			public void run(){
				while(true){
					while(timedOut){
						ThreadUtil.wait(100);
					}
					for(int i=0;i<10;i++){
						ThreadUtil.wait(getTime()/10);
						if(i==9){
							timedOut = true;
							if(listener!=null)
								listener.onTimeout();
						}
					}
					
				}
			}
		};
		timerThread.start();
	}
	
	/**
	 * Checks if is timed out.
	 *
	 * @return true, if is timed out
	 */
	public boolean isTimedOut() {
		return timedOut;
	}
	

	
	public void initialize(int sleepTime) {
		// TODO Auto-generated method stub
		this.time = (sleepTime);
		timedOut = false;
	}

	public int getTime() {
		return time;
	}

	public void setTimeoutListener(IthreadedTimoutListener listener) {
		this.listener = listener;
	}
}
