package com.neuronrobotics.sdk.util;

public class ThreadedNsTimer extends Thread {
	private IThreadedNsTimerListener listener;
	private long startTime;
	private long currentInterval;
	private long timerInterval;
	private long loopIndex=0;
	private boolean running=true;
	private boolean failOnRealtime;
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
	
	private long recalculateTarget(){
		currentInterval = getStartTime() + (timerInterval*loopIndex);

		return System.nanoTime()-currentInterval;
	}
	
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

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public long getStartTime() {
		return startTime;
	}

	private void setStartTime(long startTime) {
		this.startTime = startTime;
	}


}
