package com.neuronrobotics.sdk.pid;

public class LinearInterpolationEngine {
	
	private double ticks=0;
	private double lastTick=getTicks();
	private double lastInterpolationTime;
	private double setPoint;
	private double duration;
	private double startTime;
	private double startPoint;
	private boolean pause = false;
	private boolean velocityRun=false;
	private double unitsPerMs;
	private int chan;
	private PIDConfiguration configs;
	public LinearInterpolationEngine(int index,PIDConfiguration configs ){
		setChan(index);
		this.setConfigs(configs);
	}
	public void SetVelocity(double unitsPerSecond) {
		//System.out.println("Setting velocity to "+unitsPerSecond+"ticks/second");
		setPause(true);
		
		this.unitsPerMs=unitsPerSecond/1000;
		lastInterpolationTime=System.currentTimeMillis();
		velocityRun=true;
		
		setPause(false);
	}
	public int getPosition() {
		return (int) getTicks();
	}
	
	
	
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
	public boolean update(){
		if(getConfigs().isEnabled())
			interpolate();
		if((getTicks()!=lastTick) && !isPause()) {
			lastTick=getTicks();
			return true;
		}	
		return false;
	}

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
	private void setChan(int chan) {
		this.chan = chan;
	}
	public int getChan() {
		return chan;
	}
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
	public boolean isPause() {
		return pause;
	}
	private void setPause(boolean pause) {
		this.pause = pause;
	}
	public double getTicks() {
		return ticks;
	}
	public void setTicks(double ticks) {
		this.ticks = ticks;
	}
	public PIDConfiguration getConfigs() {
		return configs;
	}
	public void setConfigs(PIDConfiguration configs) {
		this.configs = configs;
	}
}
