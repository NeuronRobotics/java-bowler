package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class LineTrack implements IRobotDriveEventListener,ISensorListener{
	DyIO dyio;
	int l=0,r=0;
	public LineTrack(DyIO d) {
		dyio=d;
		new Thread() {
			public void run() {
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
							1,// Ki
							.5);//Kd
				dyio.ConfigureDynamicPIDChannels(dypid);
				dyio.ConfigurePIDController(pid);
				
				PIDChannel drive = dyio.getPIDChannel(0);
				AbstractRobotDrive mainRobot  = new AckermanBot(new ServoChannel(dyio.getChannel(10)), drive );

				AbstractSensor line = new LineSensor(	new AnalogInputChannel(dyio.getChannel(14),true),
										null,
										new AnalogInputChannel(dyio.getChannel(13),true));
				
				runTrack();
			}
		}.start();

	}
	private void runTrack(AbstractRobotDrive mainRobot,AbstractSensor line) {
		mainRobot.addIRobotDriveEventListener(this);
		line.addSensorListener(this);
		while(dyio.isAvailable()) {
			ThreadUtil.wait(50);
			double diff = (double)(l-r);
			System.out.println("Steer value ="+diff);
			if(diff<100 && diff>-100) {
				mainRobot.DriveStraight(20, .1);
			}else {
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
	public static void main(String [] a) {
		DyIO d = new DyIO();
		ConnectionDialog.getBowlerDevice(d);
		if(d.isAvailable()) {
			new LineTrack(d);
		}else{
			System.out.println("Failed");
			d.disconnect();
		}
	}
}
