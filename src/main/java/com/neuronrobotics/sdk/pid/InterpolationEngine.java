package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.addons.kinematics.time.ITimeProvider;
import com.neuronrobotics.sdk.addons.kinematics.time.TimeKeeper;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearInterpolationEngine.
 */
public class InterpolationEngine extends TimeKeeper{
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
	private double interpElapsed;
	private double sinPortion;
	private double lengthOfLinearMode;
	private double unitLienear;
	private double unitRamp;
	private double unitStartRampDown;
	private double increment;
	private double sinPortion2;
	private double increment2;
	private double sinPortion3;
	private double t;
	private double p0;
	private double p1;
	private double p2;
	private double p3;
	private double setpointDiff;
	private double newSetpoint;
	
	public InterpolationEngine(ITimeProvider t) {
		setTimeProvider(t);
	}
	

	/**
	 * Sets the velocity.
	 *
	 * @param unitsPerSecond the units per second
	 */
	public void SetVelocity(double unitsPerSecond) {

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
	public void StartLinearMotion(double setpoint,double seconds,long startTimeMs){
		setSetpointWithTime(startTimeMs,setpoint, seconds, InterpolationType.LINEAR);
	}
	/**
	 * Sets the pid set point.
	 *
	 * @param setpoint the setpoint
	 * @param seconds the seconds
	 */
	public void StartSinusoidalMotion(double setpoint,double seconds,long startTimeMs){
		setSetpointWithTime(startTimeMs,setpoint, seconds, InterpolationType.SINUSOIDAL);
	}

	public void setSetpointWithTime(long startTimeMs ,double setpoint,double seconds, InterpolationType mode,double ...conf) {
		if(InterpolationType.TRAPEZOIDAL==mode) {
			TRAPEZOIDAL_time =conf[0];
		}
		if(InterpolationType.BEZIER==mode) {
			BEZIER_P0 = conf[0];
			BEZIER_P1 = conf[1];
		}
		type = mode;
		if(seconds<0.001)
			seconds = 0.001;// one ms garunteed
		//setPause(true);

		duration = (long) (seconds*1000);
		startTime=startTimeMs;
		if(new Double(setpoint).isNaN()) {
			new RuntimeException("Setpopint in virtual device can not be set to nan").printStackTrace();
		
		}else
			endSetpoint=setpoint;
		startSetpoint = getTicks();
		
		//setPause(false);
	}
	public void StartTrapezoidalMotion(double setpoint,double seconds, double trapazoidalTime,long startTimeMs) {

		if (trapazoidalTime * 2 > seconds) {
			StartSinusoidalMotion(setpoint, seconds,startTimeMs);
			return;
		}
		setSetpointWithTime(startTimeMs,setpoint, seconds, InterpolationType.TRAPEZOIDAL,trapazoidalTime);
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
	void StartBezierMotion(double setpoint,double seconds, double Control_0 , double Control_1,long startTimeMs)
	{
		setSetpointWithTime(startTimeMs,setpoint, seconds, InterpolationType.BEZIER,Control_0,Control_1);
	}
	
	
	/**
	 * Update.
	 *
	 * @return true, if successful
	 */
	public boolean update(long time){
		interpolate( time);
		if((getTicks()!=lastTick)) {
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
		//setPause(true);
		//ThreadUtil.wait((int)(threadTime*2));
		setTicks(value);
		lastTick=value;
		endSetpoint=value;
		duration=0;
		startTime=currentTimeMillis();
		startSetpoint=value;
		//setPause(false);	
	}
	

	double myFmapBounded(double x, double in_min, double in_max, double out_min, double out_max) {

		if (x > in_max)
			return out_max;
		if (x < in_min)
			return out_min;
		return ((x - in_min) * (out_max - out_min) / (in_max - in_min)) + out_min;
	}
	
	public  double getInterpolationUnitIncrement(long time) {
		interpElapsed = (double)(time - startTime);
		if (interpElapsed < duration && duration > 0)
		{
			
			setUnitDuration(interpElapsed / duration);
			if(type==InterpolationType.SINUSOIDAL) {
				sinPortion = (Math.cos(-Math.PI * getUnitDuration()) / 2) + 0.5;
				setUnitDuration(1 - sinPortion);
			}
			if (type == InterpolationType.TRAPEZOIDAL)
			{
				lengthOfLinearMode = duration - (TRAPEZOIDAL_time * 2);
				unitLienear = lengthOfLinearMode / duration;
				unitRamp = ((double)TRAPEZOIDAL_time) / duration;
				unitStartRampDown = unitLienear + unitRamp;
				if (getUnitDuration() < unitRamp)
				{
					increment = 1 - (getUnitDuration()) / (unitRamp * 2);
					sinPortion2 = 1 + Math.cos(-Math.PI * increment);
					setUnitDuration(sinPortion2 * unitRamp);
				}
				else if (getUnitDuration() > unitRamp && getUnitDuration() < unitStartRampDown)
				{
					// constant speed
				}
				else if (getUnitDuration() > unitStartRampDown)
				{
					increment2 = (getUnitDuration() - unitStartRampDown) / (unitRamp * 2) + 0.5;
					sinPortion3 = 0.5 - ((Math.cos(-Math.PI * increment2) / 2) + 0.5);
					setUnitDuration((sinPortion3 * 2) * unitRamp + unitStartRampDown);
				}
			}
			if (type == InterpolationType.BEZIER)
			{
				if (getUnitDuration() > 0 && getUnitDuration() < 1)
				{
					t = getUnitDuration();
					p0 = 0;
					p1 = BEZIER_P0;
					p2 = BEZIER_P1;
					p3 = 1;
					setUnitDuration(Math.pow((1 - t), 3) * p0 + 3 * t * Math.pow((1 - t), 2) * p1 + 3 * Math.pow(t, 2) * (1 - t) * p2 + Math.pow(t, 3) * p3);
				}
			}
			return getUnitDuration();
		}
		return 1;
	}
	
	
	
	/**
	 * Interpolate.
	 * @param time 
	 */
	private void interpolate(long time) {
		setUnitDuration(getInterpolationUnitIncrement(time));
		if (getUnitDuration() < 1) {
			setpointDiff = endSetpoint - startSetpoint;
			newSetpoint = startSetpoint + (setpointDiff * getUnitDuration());
			setTicks(newSetpoint);
		} else {
			// If there is no interpoation to perform, set the setpoint to the end state
			setTicks(endSetpoint);
		}
	}
	
//	/**
//	 * Checks if is pause.
//	 *
//	 * @return true, if is pause
//	 */
//	public boolean isPause() {
//		return pause;
//	}
//	
//	/**
//	 * Sets the pause.
//	 *
//	 * @param pause the new pause
//	 */
//	private void setPause(boolean pause) {
//		this.pause = pause;
//	}
	
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
//		if(new Double(ticks).isNaN()) {
//			new RuntimeException("Ticks in virtual device can not be set to nan").printStackTrace();
//			return;
//		}
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
