package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AckermanBot;
import com.neuronrobotics.addons.driving.AckermanConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class VirtualAckermanBot extends AckermanBot {
	private VirtualWorld world;
	private final AckermanConfiguration config = new AckermanConfiguration();
	VirtualRobot drive;

	public VirtualAckermanBot(VirtualWorld w){
		world=w;
		world.addRobot(this);
		drive = new VirtualRobot(0,this,config.getMaxTicksPerSeconds());
		drive.start();
		setLineSensor(new VirtualLineSensor(this,w));
		setRangeSensor(new VirtualRangeSensor(this,w));
	}
	@Override
	protected void SetDriveDistance(int ticks, double seconds){
		drive.SetPIDSetPoint(ticks, seconds);
	}
	@Override
	protected void ResetDrivePosition(){
		onPIDReset(0, 0);
		ThreadUtil.wait(200);
	}

	@Override
	public void onPIDEvent(PIDEvent e)  {
		super.onPIDEvent(e);
		world.updateMap();
	}
	@Override
	public void onPIDReset(int group, int currentValue){
		System.out.println("##Virtual Robot Reset called");
		if(group==0){
			drive.ZeroEncoder();
			super.onPIDReset(group, currentValue);
		}
	}

	public void setSteeringAngle(double steeringAngle) {
		System.out.println("Setting steering angle to: " + Math.toDegrees(steeringAngle));
		this.steeringAngle = steeringAngle;
	}
	
}
