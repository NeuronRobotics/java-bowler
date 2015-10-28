package com.neuronrobotics.addons.driving;

// TODO: Auto-generated Javadoc
/**
 * The Class AckermanConfiguration.
 */
public class AckermanConfiguration {
	
	/** The ticks per revolution. */
	private double ticksPerRevolution = 4096;//ticks
	
	/** The wheel diameter. */
	private double wheelDiameter = 8.0*2.54;//cm
	
	/** The cm per revolution. */
	private double cmPerRevolution = 2*Math.PI*wheelDiameter;
	
	/** The ticks to cm. */
	private double ticksToCm = ticksPerRevolution/cmPerRevolution;
	
	/** The max ticks per seconds. */
	private double maxTicksPerSeconds = 200;
	
	/** The wheelbase. */
	private double wheelbase = 16.5*2.54;//cm
	
	/** The servo to steer angle. */
	private double servoToSteerAngle=1;
	
	/**
	 * Instantiates a new ackerman configuration.
	 */
	public AckermanConfiguration(){
		//use defaults
	}
	
	/**
	 * Instantiates a new ackerman configuration.
	 *
	 * @param ticksPerRevolution the ticks per revolution
	 * @param wheelDiameter the wheel diameter
	 * @param maxTicksPerSeconds the max ticks per seconds
	 * @param wheelbase the wheelbase
	 * @param scale the scale
	 */
	public AckermanConfiguration(	double ticksPerRevolution,
									double wheelDiameter,
									double maxTicksPerSeconds ,
									double wheelbase,
									double scale){
		this.ticksPerRevolution=ticksPerRevolution;
		this.wheelDiameter = wheelDiameter;
		this.maxTicksPerSeconds =maxTicksPerSeconds ;
		this.wheelbase = wheelbase;
		this.servoToSteerAngle = scale;
	}
	
	/**
	 * Sets the max ticks per seconds.
	 *
	 * @param maxTicksPerSeconds the new max ticks per seconds
	 */
	public void setMaxTicksPerSeconds(double maxTicksPerSeconds) {
		this.maxTicksPerSeconds = maxTicksPerSeconds;
	}

	/**
	 * Gets the max ticks per seconds.
	 *
	 * @return the max ticks per seconds
	 */
	public double getMaxTicksPerSeconds() {
		return maxTicksPerSeconds;
	}

	/**
	 * Convetrt to cm.
	 *
	 * @param ticks the ticks
	 * @return the double
	 */
	public double convetrtToCm(int ticks){
		double back =ticks/ticksToCm;
		//System.out.println(ticks+" ticks = "+back+" cm");
		return back;
	}
	
	/**
	 * Convert to ticks.
	 *
	 * @param cm the cm
	 * @return the int
	 */
	public int convertToTicks(double cm){
		int back = (int)(cm*ticksToCm);
		//System.out.println(cm+"cm = "+back+"ticks");
		return back;
	}
	
	/**
	 * Gets the wheelbase.
	 *
	 * @return the wheelbase
	 */
	public double getWheelbase() {
		return wheelbase;
	}

	/**
	 * Gets the steer angle to servo.
	 *
	 * @return the steer angle to servo
	 */
	public double getSteerAngleToServo() {
		return 1/servoToSteerAngle;
	}

	/**
	 * Gets the servo to steer angle.
	 *
	 * @return the servo to steer angle
	 */
	public double getServoToSteerAngle() {
		return servoToSteerAngle;
	}

}
