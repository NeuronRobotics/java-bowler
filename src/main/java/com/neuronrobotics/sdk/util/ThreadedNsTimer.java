package com.neuronrobotics.sdk.util;

// TODO: Auto-generated Javadoc
/**
 * The Class ThreadedNsTimer.
 */
public class ThreadedNsTimer extends Thread {
	
	/** The listener. */
	private IThreadedNsTimerListener listener;
	
	/** The start time. */
	private long startTime;
	
	/** The current interval. */
	private long currentInterval;
	
	/** The timer interval. */
	private long timerInterval;
	
	/** The loop index. */
	private long loopIndex=0;
	
	/** The running. */
	private boolean running=true;
	
	/** The fail on realtime. */
	private boolean failOnRealtime;
	
	/**
	 * Instantiates a new threaded ns timer.
	 *
	 * @param l the l
	 * @param nsInterval the ns interval
	 * @param failOnRealtime the fail on realtime
	 */
	public ThreadedNsTimer(IThreadedNsTimerListener l, long nsInterval, boolean failOnRealtime) {
		listener=l;
		timerInterval = nsInterval;
		this.failOnRealtime = failOnRealtime;
		if(l==null)
			throw new NullPointerException();
		if(nsInterval<10000){
			throw new RuntimeException("This is below the resolution of the timer: "+nsInterval);
		}
				
	}
	
	/**
	 * Recalculate target.
	 *
	 * @return the long
	 */
	private long recalculateTarget(){
		currentInterval = getStartTime() + (timerInterval*loopIndex);

		return System.nanoTime()-currentInterval;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		setName("Bowler Platform Threaded timer instance");
		setStartTime(System.nanoTime());
		recalculateTarget();
		while(isRunning()){
			java.util.concurrent.locks.LockSupport.parkNanos(1);
			long diff = recalculateTarget();
			if(diff>0){
				listener.onTimerInterval(loopIndex++);
				if(recalculateTarget()>0){
					if(failOnRealtime ){
						try{
							throw new RuntimeException("Real time broken!!");
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			}
		}
		throw new RuntimeException("Real time exeted");
	}

	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Sets the running.
	 *
	 * @param running the new running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Gets the start time.
	 *
	 * @return the start time
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time.
	 *
	 * @param startTime the new start time
	 */
	private void setStartTime(long startTime) {
		this.startTime = startTime;
	}


}
