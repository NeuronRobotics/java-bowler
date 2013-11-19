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
	private timerThreadClass timerThread;
	
	/** The timed out. */
	private boolean timedOut = true;
	private IthreadedTimoutListener listener;
	private long startTime=0;
	
	
	/**
	 * Instantiates a new threaded timeout.
	 *
	 * @param time the time
	 */
	public ThreadedTimeout() {
		timerThread = new timerThreadClass();
		timerThread.start();
	}
	
	/**
	 * Checks if is timed out.
	 *
	 * @return true, if is timed out
	 */
	public boolean isTimedOut() {
		return System.currentTimeMillis()>(startTime+getTime());
	}
	/**
	 * Checks if is timed out.
	 *
	 * @return true, if is timed out
	 */
	private boolean isTimedOutLocal() {
		return timedOut;
	}

	
	public void initialize(int sleepTime) {
		// TODO Auto-generated method stub
		IthreadedTimoutListener tmp =listener;
		listener=null;
		setTimedOutLocal(true);
		if(!timerThread.isReset())
			while(!timerThread.isReset()){
				ThreadUtil.wait(0,5);
			}
		//ThreadUtil.wait(10);
		this.time = (sleepTime);
		startTime=System.currentTimeMillis();
		setTimedOutLocal(false);
		listener=tmp;
	}

	public int getTime() {
		return time;
	}

	public void setTimeoutListener(IthreadedTimoutListener listener) {
		this.listener = listener;
	}
	private void setTimedOutLocal(boolean timedOut) {
		this.timedOut = timedOut;
	}
	private class timerThreadClass extends Thread{
		private boolean reset;

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			while(true){
				setReset(true);
				while(isTimedOutLocal()){
					ThreadUtil.wait(0,1);
				}
				setReset(false);
				while(!isTimedOutLocal()){
					ThreadUtil.wait(0,1);
					if(System.currentTimeMillis()>(startTime+getTime()) && !isTimedOutLocal() )
					if(System.currentTimeMillis()>(startTime+getTime()) && !isTimedOutLocal() ){
						setTimedOutLocal(true);
						if(listener!=null)
							listener.onTimeout("");
					}
				}
				
			}
		}

		public boolean isReset() {
			return reset;
		}

		public void setReset(boolean reset) {
			this.reset = reset;
		}
	}
}
