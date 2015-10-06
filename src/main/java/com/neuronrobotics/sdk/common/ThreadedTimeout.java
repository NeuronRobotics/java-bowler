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
import java.util.ArrayList;

import com.neuronrobotics.sdk.util.ThreadUtil;


// TODO: Auto-generated Javadoc
/**
 * The Class ThreadedTimeout.
 */
public class ThreadedTimeout {
	private static timerThreadClass timerThread;
	static{
		timerThread = new timerThreadClass();
		timerThread.start();
	}
	
	/** The time. */
	private long time;
	
	
	/** The timed out. */
	private IthreadedTimoutListener listener;
	private long startTime=0;
	
	
	/**
	 * Instantiates a new threaded timeout.
	 *
	 * @param time the time
	 */
	public ThreadedTimeout() {
		
	}
	
	/**
	 * Checks if is timed out.
	 *
	 * @return true, if is timed out
	 */
	public boolean isTimedOut() {
		return System.currentTimeMillis()>(getStartTime()+getAmountOfTimeForTimerToRun());
	}
	
	public void initialize(long sleepTime,IthreadedTimoutListener listener) {
		setStartTime(System.currentTimeMillis());
		this.time = (sleepTime);
		setTimeoutListener(listener);
		timerThread.addTimer(this);
	}

	public long getAmountOfTimeForTimerToRun() {
		return time;
	}

	private void setTimeoutListener(IthreadedTimoutListener listener) {
		this.listener = listener;
	}

	
	private static class timerThreadClass extends Thread{
		private ArrayList<ThreadedTimeout> timers = new ArrayList<ThreadedTimeout>();
		private ArrayList<ThreadedTimeout> toRemove = new ArrayList<ThreadedTimeout>();
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			setName("Bowler Platform Threaded timeout");
			while(true){
				if(timers.size()>0){
					toRemove.clear();
					
					for(int i=0;i<timers.size();i++){
						try{
							ThreadedTimeout t = timers.get(i);
							if(t!=null){
								if(t.isTimedOut()){
									if(t.listener!=null){
										t.listener.onTimeout("Timer timed out after "+t.time);
									}
									toRemove.add(t);
								}
							}
						}catch (IndexOutOfBoundsException e){
							//ignore edge case, try again later
						}catch (Exception ex){
							ex.printStackTrace();
						}
					}
					for(int i=0;i<toRemove.size();i++){
						removeTimer(toRemove.get(i));
					}
				}
				ThreadUtil.wait(1);
			}
		}
		public void addTimer(ThreadedTimeout time){
			try{
				synchronized(timers){
					if(!timers.contains(time))
						timers.add(time);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		public void removeTimer(ThreadedTimeout time){
			try{
				synchronized(timers){
					if(timers.contains(time))
						timers.remove(time);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			
		}
	}


	public void stop() {
		
		timerThread.removeTimer(this);
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
