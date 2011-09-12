package com.neuronrobotics.addons.driving;

import java.awt.Color;
import java.util.ArrayList;

import com.neuronrobotics.addons.driving.virtual.ObsticleType;
import com.neuronrobotics.addons.driving.virtual.VirtualAckermanBot;
import com.neuronrobotics.addons.driving.virtual.VirtualPuckBot;
import com.neuronrobotics.addons.driving.virtual.VirtualWorld;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class DrivingTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean virtual = true;
		//virtual = false;
		AbstractRobot ackerman;
		if(virtual) {
			
			VirtualWorld w = new VirtualWorld();
			VirtualAckermanBot a = new VirtualAckermanBot(w);
			VirtualAckermanBot b = new VirtualAckermanBot(w,200 ,300);
			ackerman = a;
		}else {
			DyIO dyio=new DyIO();
			if (!ConnectionDialog.getBowlerDevice(dyio)){
				System.exit(1);
			}
			DyPIDConfiguration dypid = new DyPIDConfiguration(	0,//PID group 0
																23,//Input channel number
																DyIOChannelMode.COUNT_IN_INT,//Input mode
																11,//Output Channel
																DyIOChannelMode.SERVO_OUT);//Output mode
			PIDConfiguration pid =new PIDConfiguration (	0,//PID group
															true,//enabled
															false,//inverted
															true,//Async
															1,// Kp
															0,// Ki
															0);//Kd
			dyio.ConfigureDynamicPIDChannels(dypid);
			dyio.ConfigurePIDController(pid);
			
			PIDChannel drive = dyio.getPIDChannel(0);
			AckermanBot a = new AckermanBot(new ServoChannel(dyio.getChannel(10)), drive );
			
			a.setLineSensor(null);
			a.setRangeSensor(null);
			
			ackerman = a;
		}
		
		double driveTime=5;
//		ackerman.addIRobotDriveEventListener(new IRobotDriveEventListener() {
//			
//			@Override
//			public void onDriveEvent(double x, double y, double orentation) {
//				System.out.println("Drive Event x="+x+" y="+y+" orentation="+Math.toDegrees(orentation));
//			}
//		});
		ackerman.addSensorListener(new ISensorListener() {
			
			@Override
			public void onRangeSensorEvent(ArrayList<DataPoint> data, long timeStamp) {
				System.out.println("Range Sensor Event "+data);
			}

			@Override
			public void onLineSensorEvent(Integer left, Integer middle,Integer right, long timeStamp) {
				System.out.println("Line Sensor Event left="+left+" middle="+middle+" right="+right);
			}
		});
//		ackerman.DriveArc(20, 90, driveTime);
//		ThreadUtil.wait((int) (driveTime*1000));
//
//		ackerman.DriveStraight(10, driveTime);
//		ThreadUtil.wait((int) (driveTime*1000));
		ackerman.StartSweep(-45, 0, 1);
		//System.exit(0);
	}

}
