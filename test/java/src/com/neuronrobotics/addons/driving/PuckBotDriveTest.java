package com.neuronrobotics.addons.driving;

import com.neuronrobotics.addons.driving.virtual.VirtualPuckBot;
import com.neuronrobotics.addons.driving.virtual.VirtualWorld;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PuckBotDriveTest implements IRobotDriveEventListener {
	public PuckBotDriveTest(){
		
		DyIO dyio = new DyIO();
		if(!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		
		DyPIDConfiguration ldypid = new DyPIDConfiguration(	1,//PID group 1
															21,//Input channel number
															DyIOChannelMode.COUNT_IN_INT,//Input mode
															10,//Output Channel
															DyIOChannelMode.SERVO_OUT);//Output mode
		PIDConfiguration lpid =new PIDConfiguration (	1,//PID group
														true,//enabled
														false,//inverted
														true,//Async
														1,// Kp
														1,// Ki
														.5);//Kd
		DyPIDConfiguration rdypid = new DyPIDConfiguration(	2,//PID group 2
															19,//Input channel number
															DyIOChannelMode.COUNT_IN_INT,//Input mode
															11,//Output Channel
															DyIOChannelMode.SERVO_OUT);//Output mode
		PIDConfiguration rpid =new PIDConfiguration (	2,//PID group
														true,//enabled
														false,//inverted
														true,//Async
														1,// Kp
														1,// Ki
														.5);//Kd
		dyio.ConfigureDynamicPIDChannels(ldypid);
		dyio.ConfigurePIDController(lpid);
		dyio.ConfigureDynamicPIDChannels(rdypid);
		dyio.ConfigurePIDController(rpid);
		
		PIDChannel left = dyio.getPIDChannel(1);
		PIDChannel right = dyio.getPIDChannel(2);
		
		PuckBot bot = new PuckBot(left, right);
//		VirtualWorld w = new VirtualWorld();
//		PuckBot bot = new VirtualPuckBot(w);
		
		bot.setPuckBotKinematics(new PuckBotDefaultKinematics());
		bot.addIRobotDriveEventListener(this);
		

		bot.DriveStraight(100, 3);
		
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PuckBotDriveTest();
	}

	@Override
	public void onDriveEvent(AbstractRobotDrive source, double x, double y,double orentation) {
		System.out.println("Drive Event x="+x+" y="+y+" orentation="+Math.toDegrees(orentation));
	}
}
