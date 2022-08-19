package com.neuronrobotics.sdk.pid;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearInterpolationEngine.
 */
public class InterpolationEngine {
	private InterpolationType type = InterpolationType.LINEAR;
	/** The ticks. */
	private double ticks=0;
	
	/** The last tick. */
	private double lastTick=getTicks();
	
	/** The set point. */
	private double endSetpoint=0;
	
	/** The duration. */
	private double duration;
	
	/** The start time. */
	private double startTime;
	
	/** The start point. */
	private double startSetpoint;
	
	/** The pause. */
	private boolean pause = false;
	
	private double unitDuration;
	private double TRAPEZOIDAL_time=0;
	private double BEZIER_P0;
	private double BEZIER_P1;
	

	/**
	 * Sets the velocity.
	 *
	 * @param unitsPerSecond the units per second
	 */
	public void SetVelocity(double unitsPerSecond) {
		//System.out.println("Setting velocity to "+unitsPerSecond+"ticks/second");
		setPause(true);
		
		System.currentTimeMillis();
		setPause(false);
	}
	
	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public double getPosition() {
		return  getTicks();
	}
	
	
	
	/**
	 * Sets the pid set point.
	 *
	 * @param setpoint the setpoint
	 * @param seconds the seconds
	 */
	public void StartLinearMotion(double setpoint,double seconds){
		setSetpointWithTime(setpoint, seconds, InterpolationType.LINEAR);
	}
	/**
	 * Sets the pid set point.
	 *
	 * @param setpoint the setpoint
	 * @param seconds the seconds
	 */
	public void StartSinusoidalMotion(double setpoint,double seconds){
		setSetpointWithTime(setpoint, seconds, InterpolationType.SINUSOIDAL);
	}

	private void setSetpointWithTime(double setpoint,double seconds, InterpolationType mode) {
		type = mode;
		if(seconds<0.001)
			seconds = 0.001;// one ms garunteed
		setPause(true);

		duration = (long) (seconds*1000);
		startTime=System.currentTimeMillis();
		if(new Double(setpoint).isNaN()) {
			new RuntimeException("Setpopint in virtual device can not be set to nan").printStackTrace();
		
		}else
			endSetpoint=setpoint;
		startSetpoint = getTicks();
		
		setPause(false);
	}
	public void StartTrapezoidalMotion(double setpoint,double seconds, double trapazoidalTime) {

		if (trapazoidalTime * 2 > seconds) {
			StartSinusoidalMotion(setpoint, seconds);
			return;
		}
		TRAPEZOIDAL_time = trapazoidalTime;
		setSetpointWithTime(setpoint, seconds, InterpolationType.TRAPEZOIDAL);
	}
	/**
	 * SetSetpoint in degrees with time
	 * Set the setpoint for the motor in degrees
	 * @param newTargetInDegrees the new setpoint for the closed loop controller
	 * @param miliseconds the number of miliseconds to get from current position to the new setpoint
	 * @param Control_0 On a scale of 0 to 1, where should the first control  point in the equation go default= 0.5
	 * @param Control_1 On a scale of 0 to 1, where should the second control point in the equation go default= 1.0
	 * use Bezier interpolation
	 */
	void StartBezierMotion(double setpoint,double seconds, double Control_0 , double Control_1)
	{
		BEZIER_P0 = Control_0;
		BEZIER_P1 = Control_1;
		setSetpointWithTime(setpoint, seconds, InterpolationType.BEZIER);
	}
	
	/**
	 * Update.
	 *
	 * @return true, if successful
	 */
	public boolean update(){
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
	public synchronized  void ResetEncoder(double value) {
		setPause(true);
		//ThreadUtil.wait((int)(threadTime*2));
		setTicks(value);
		lastTick=value;
		endSetpoint=value;
		duration=0;
		startTime=System.currentTimeMillis();
		startSetpoint=value;
		setPause(false);	
	}
	

	double myFmapBounded(double x, double in_min, double in_max, double out_min, double out_max) {

		if (x > in_max)
			return out_max;
		if (x < in_min)
			return out_min;
		return ((x - in_min) * (out_max - out_min) / (in_max - in_min)) + out_min;
	}
	
	private double getInterpolationUnitIncrement() {
		double interpElapsed = (double)(System.currentTimeMillis() - startTime);
		if (interpElapsed < duration && duration > 0)
		{
			
			setUnitDuration(interpElapsed / duration);
			if(type==InterpolationType.SINUSOIDAL) {
				double sinPortion = (Math.cos(-Math.PI * getUnitDuration()) / 2) + 0.5;
				setUnitDuration(1 - sinPortion);
			}
			if (type == InterpolationType.TRAPEZOIDAL)
			{
				double lengthOfLinearMode = duration - (TRAPEZOIDAL_time * 2);
				double unitLienear = lengthOfLinearMode / duration;
				double unitRamp = ((double)TRAPEZOIDAL_time) / duration;
				double unitStartRampDown = unitLienear + unitRamp;
				if (getUnitDuration() < unitRamp)
				{
					// ramp up
					// range from 1 to 0.5
					double increment = 1 - (getUnitDuration()) / (unitRamp * 2);
					// range 0 to 1
					double sinPortion = 1 + Math.cos(-Math.PI * increment);
					setUnitDuration(sinPortion * unitRamp);
				}
				else if (getUnitDuration() > unitRamp && getUnitDuration() < unitStartRampDown)
				{
					// constant speed
				}
				else if (getUnitDuration() > unitStartRampDown)
				{
					double increment = (getUnitDuration() - unitStartRampDown) / (unitRamp * 2) + 0.5;
					double sinPortion = 0.5 - ((Math.cos(-Math.PI * increment) / 2) + 0.5);
					setUnitDuration((sinPortion * 2) * unitRamp + unitStartRampDown);
				}
			}
			if (type == InterpolationType.BEZIER)
			{
				if (getUnitDuration() > 0 && getUnitDuration() < 1)
				{
					double t = getUnitDuration();
					double P0 = 0;
					double P1 = BEZIER_P0;
					double P2 = BEZIER_P1;
					double P3 = 1;
					setUnitDuration(Math.pow((1 - t), 3) * P0 + 3 * t * Math.pow((1 - t), 2) * P1 + 3 * Math.pow(t, 2) * (1 - t) * P2 + Math.pow(t, 3) * P3);
				}
			}
			return getUnitDuration();
		}
		return 1;
	}
	
	
	
	/**
	 * Interpolate.
	 */
	private void interpolate() {
		setUnitDuration(getInterpolationUnitIncrement());
		if (getUnitDuration() < 1) {
			double setpointDiff = endSetpoint - startSetpoint;
			double newSetpoint = startSetpoint + (setpointDiff * getUnitDuration());
			setTicks(newSetpoint);
		} else {
			// If there is no interpoation to perform, set the setpoint to the end state
			setTicks(endSetpoint);
		}
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
		if(new Double(ticks).isNaN()) {
			new RuntimeException("Ticks in virtual device can not be set to nan").printStackTrace();
			return;
		}
		this.ticks = ticks;
	}
	
	public InterpolationType getType() {
		return type;
	}

	public void setType(InterpolationType type) {
		this.type = type;
	}

	public double getUnitDuration() {
		return unitDuration;
	}

	public void setUnitDuration(double unitDuration) {
		if(unitDuration>1)
			unitDuration=1;
		if(unitDuration<0)
			unitDuration=0;
		this.unitDuration = unitDuration;
	}
}
