package com.neuronrobotics.sdk.pid;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearInterpolationEngine.
 */
public class LinearInterpolationEngine {
	
	/** The ticks. */
	private double ticks=0;
	
	/** The last tick. */
	private double lastTick=getTicks();
	
	/** The last interpolation time. */
	private double lastInterpolationTime;
	
	/** The set point. */
	private double setPoint;
	
	/** The duration. */
	private double duration;
	
	/** The start time. */
	private double startTime;
	
	/** The start point. */
	private double startPoint;
	
	/** The pause. */
	private boolean pause = false;
	
	/** The velocity run. */
	private boolean velocityRun=false;
	
	/** The units per ms. */
	private double unitsPerMs;
	
	/** The chan. */
	private int chan;
	
	/** The configs. */
	private PIDConfiguration configs;
	
	/**
	 * Instantiates a new linear interpolation engine.
	 *
	 * @param index the index
	 * @param configs the configs
	 */
	public LinearInterpolationEngine(int index,PIDConfiguration configs ){
		setChan(index);
		this.setConfigs(configs);
	}
	
	/**
	 * Sets the velocity.
	 *
	 * @param unitsPerSecond the units per second
	 */
	public void SetVelocity(double unitsPerSecond) {
		//System.out.println("Setting velocity to "+unitsPerSecond+"ticks/second");
		setPause(true);
		
		this.unitsPerMs=unitsPerSecond/1000;
		lastInterpolationTime=System.currentTimeMillis();
		velocityRun=true;
		
		setPause(false);
	}
	
	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public int getPosition() {
		return (int) getTicks();
	}
	
	
	
	/**
	 * Sets the pid set point.
	 *
	 * @param setpoint the setpoint
	 * @param seconds the seconds
	 */
	public synchronized  void SetPIDSetPoint(int setpoint,double seconds){
		getConfigs().setEnabled(true);
		velocityRun=false;
		setPause(true);
		//ThreadUtil.wait((int)(threadTime*2));
		double TPS = (double)setpoint/seconds;
		//Models motor saturation
//		if(TPS >  configs.getMaxTicksPerSecond()){
//			seconds = (double)setpoint/ getMaxTicksPerSecond();
//			//throw new RuntimeException("Saturated PID on channel: "+chan+" Attempted Ticks Per Second: "+TPS+", when max is"+getMaxTicksPerSecond()+" set: "+setpoint+" sec: "+seconds);
//		}
		duration = (long) (seconds*1000);
		startTime=System.currentTimeMillis();
		setPoint=setpoint;
		startPoint = getTicks();
		
		setPause(false);
		//System.out.println("Setting Setpoint Ticks to: "+setPoint);
	}
	
	/**
	 * Update.
	 *
	 * @return true, if successful
	 */
	public boolean update(){
		if(getConfigs().isEnabled())
			interpolate();
		if((getTicks()!=lastTick) && !isPause()) {
			lastTick=getTicks();
			return true;
		}	
		return false;
	}

	/**
	 * Reset encoder.
	 *
	 * @param value the value
	 */
	public synchronized  void ResetEncoder(int value) {
		//System.out.println("Resetting channel "+getChan());
		velocityRun=false;
		setPause(true);
		//ThreadUtil.wait((int)(threadTime*2));
		setTicks(value);
		lastTick=value;
		setPoint=value;
		duration=0;
		startTime=System.currentTimeMillis();
		startPoint=value;
		setPause(false);	
	}
	
	/**
	 * Sets the chan.
	 *
	 * @param chan the new chan
	 */
	private void setChan(int chan) {
		this.chan = chan;
	}
	
	/**
	 * Gets the chan.
	 *
	 * @return the chan
	 */
	public int getChan() {
		return chan;
	}
	
	/**
	 * Interpolate.
	 */
	private void interpolate() {
		double back;
		double diffTime;
		if(duration > 0 ){
			diffTime = System.currentTimeMillis()-startTime;
			if((diffTime < duration) && (diffTime>0) ){
				double elapsed = 1-((duration-diffTime)/duration);
				double tmp=((float)startPoint+(float)(setPoint-startPoint)*elapsed);
				if(setPoint>startPoint){
					if((tmp>setPoint)||(tmp<startPoint))
						tmp=setPoint;
				}else{
					if((tmp<setPoint) || (tmp>startPoint))
						tmp=setPoint;
				}
				back=tmp;
			}else{
				// Fixes the overflow case and the timeout case
				duration=0;
				back=setPoint;
			}
		}else{
			back=setPoint;
			duration = 0;
		}
		if(velocityRun){
			double ms = (double) (System.currentTimeMillis()-lastInterpolationTime);
			back=(getTicks()+unitsPerMs*ms);
			//System.out.println("Time Diff="+ms+" \n\ttick difference="+unitsPerMs*ms+" \n\tticksPerMs="+unitsPerMs +" \n\tCurrent value="+back );
		}
		setTicks(back);
		lastInterpolationTime=System.currentTimeMillis();
	}
	
	/**
	 * Checks if is pause.
	 *
	 * @return true, if is pause
	 */
	public boolean isPause() {
		return pause;
	}
	
	/**
	 * Sets the pause.
	 *
	 * @param pause the new pause
	 */
	private void setPause(boolean pause) {
		this.pause = pause;
	}
	
	/**
	 * Gets the ticks.
	 *
	 * @return the ticks
	 */
	public double getTicks() {
		return ticks;
	}
	
	/**
	 * Sets the ticks.
	 *
	 * @param ticks the new ticks
	 */
	public void setTicks(double ticks) {
		this.ticks = ticks;
	}
	
	/**
	 * Gets the configs.
	 *
	 * @return the configs
	 */
	public PIDConfiguration getConfigs() {
		return configs;
	}
	
	/**
	 * Sets the configs.
	 *
	 * @param configs the new configs
	 */
	public void setConfigs(PIDConfiguration configs) {
		this.configs = configs;
	}
}
