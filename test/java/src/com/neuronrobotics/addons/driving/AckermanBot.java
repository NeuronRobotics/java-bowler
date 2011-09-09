package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;

public class AckermanBot extends AbstractDrivingRobot {
	private final AckermanConfiguration config = new AckermanConfiguration();
	public AckermanBot(ServoChannel steering,PIDChannel drive) {
		
	}

	@Override
	public void DriveStraight(double cm, double seconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		// TODO Auto-generated method stub
		
	}
}
