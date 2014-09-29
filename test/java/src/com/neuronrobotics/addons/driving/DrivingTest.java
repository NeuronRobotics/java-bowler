package com.neuronrobotics.addons.driving;

import gnu.io.NRSerialPort;

import java.util.ArrayList;

import com.neuronrobotics.addons.driving.virtual.VirtualAckermanBot;
import com.neuronrobotics.addons.driving.virtual.VirtualFlameSensor;
import com.neuronrobotics.addons.driving.virtual.VirtualLineSensor;
import com.neuronrobotics.addons.driving.virtual.VirtualRangeSensor;
import com.neuronrobotics.addons.driving.virtual.VirtualWorld;
import com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.util.ThreadUtil;

@SuppressWarnings("unused")
public class DrivingTest implements IRobotDriveEventListener,ISensorListener{
	AbstractRobotDrive mainRobot;
	AbstractSensor line=null;
	AbstractSensor range=null;
	AbstractSensor flame=null;
	private DrivingTest(){
		setupVirtualRobot();
		//setupRealRobot();
		
		runDriveSample();
	}
	
	private void runDriveSample() {
		double driveTime=5;
		mainRobot.addIRobotDriveEventListener(this);
		if(line != null){
			line.addSensorListener(this);
		}
		if(range !=null){
			range.addSensorListener(this);
		}
		if(flame != null){
			flame.addSensorListener(this);
		}
		
		mainRobot.DriveArc(20, 90, driveTime);
		ThreadUtil.wait((int) (driveTime*1000));

		mainRobot.DriveStraight(10, driveTime);
		ThreadUtil.wait((int) (driveTime*1000));
		if (range != null)
			range.StartSweep(-90, 90, 10);
	}
	@Override
	public void onRangeSensorEvent(AbstractSensor source,ArrayList<DataPoint> data, long timeStamp) {
		if(source == range){
			System.out.println("Range Sensor Event "+data);
		}
		if(source == flame){
			System.out.println("Flame sensor "+data);
		}
	}

	@Override
	public void onLineSensorEvent(AbstractSensor source, Integer left,Integer middle, Integer right, long timeStamp) {
		if(source==line){
			System.out.println("Line Sensor Event left="+left+" middle="+middle+" right="+right);
		}
	}

	@Override
	public void onDriveEvent(AbstractRobotDrive source, double x, double y,double orentation) {
		if(source==mainRobot){
			System.out.println("Drive Event x="+x+" y="+y+" orentation="+Math.toDegrees(orentation));
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new DrivingTest();
	}
	
	
	private void setupRealRobot(DyIO dyio) {
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
														.5,//Kd
														0,//Value to latch on index pulse
														false,//Use the latch system
														false);//Stop PID controller on index latch event
		dyio.ConfigureDynamicPIDChannels(dypid);
		dyio.ConfigurePIDController(pid);
		
		PIDChannel drive = dyio.getPIDChannel(0);
		ServoChannel srv = new ServoChannel(dyio.getChannel(10));
		AckermanBot a =  new AckermanBot(	new ServoRotoryLink(srv, 98, 51, 143, 1), 
				drive );
		

		ServoChannel sweeper = new ServoChannel(dyio.getChannel(9));
		range = new LaserRangeSensor(new NRSerialPort("/dev/ttyACM0", 115200));
//		range = new LinearRangeSensor(	sweeper,
//										new AnalogInputChannel(dyio.getChannel(12)));
		line = new LineSensor(	new AnalogInputChannel(dyio.getChannel(13)),
				 				null,
				 				new AnalogInputChannel(dyio.getChannel(14)));
		//This flame sensor uses the same servo as the rangefinder
		flame = new LinearRangeSensor(	sweeper,
										new AnalogInputChannel(dyio.getChannel(15)));
		
		mainRobot = a;
	}
	private void setupVirtualRobot() {
		VirtualWorld w = new VirtualWorld();
		VirtualAckermanBot a = new VirtualAckermanBot(w); 
		VirtualAckermanBot b = new VirtualAckermanBot(w,300,200); 
		line = new VirtualLineSensor(a,w);
		range = new VirtualRangeSensor(a,w);
		//range = new LaserRangeSensor(new NRSerialPort("/dev/ttyACM0", 115200));
		flame = new VirtualFlameSensor(a, w);
		mainRobot = a;
	}




}
