package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.util.ThreadUtil;

public class LineTrack implements IRobotDriveEventListener,ISensorListener{

	int l=0,r=0;

	public void runTrack(AbstractRobotDrive mainRobot,AbstractSensor line) {
		mainRobot.addIRobotDriveEventListener(this);
		line.addSensorListener(this);
		while(mainRobot.isAvailable()) {
			ThreadUtil.wait(50);
			double diff = (double)(l-r);
			///System.out.println("Steer value ="+diff);
			if(diff<100 && diff>-100) {
				//System.out.println("Drive straight");
				mainRobot.DriveVelocityStraight(5);
			}else {
				//System.out.println("turn");
				mainRobot.DriveArc(1/(diff/100), 20, .1);
			}
		}
	}
	@Override
	public void onRangeSensorEvent(AbstractSensor source,ArrayList<DataPoint> data, long timeStamp) {
		// Never gets called...
	}
	@Override
	public void onLineSensorEvent(AbstractSensor source, Integer left,Integer middle, Integer right, long timeStamp) {
		//System.out.println("Sensor Event left="+left+" middle="+middle+" right="+right);
		l=left;
		r=right;
	}
	@Override
	public void onDriveEvent(AbstractRobotDrive source, double x, double y,double orentation) {
		//System.out.println("Drive Event: x="+x+" y="+y);
		
	}
}
