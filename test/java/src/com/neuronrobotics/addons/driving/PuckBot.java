package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class PuckBot extends AbstractDrivingRobot{
	private final PuckBotConfiguration config = new PuckBotConfiguration();
	
	public PuckBot(){
		
	}
	
	public PuckBot(PIDChannel driveRight,PIDChannel driveLeft) {
		
	}
	
	

	@Override
	public void DriveStraight(double cm, double seconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPIDEvent(PIDEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPIDReset(int group, int currentValue) {
		// TODO Auto-generated method stub
		
	}

}
