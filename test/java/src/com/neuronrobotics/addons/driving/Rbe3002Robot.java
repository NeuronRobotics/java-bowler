package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class Rbe3002Robot extends PuckBot {
	
	private final double KP = 1;
	private final double KI = 0;
	private final double KD = 0;
	
	public Rbe3002Robot() {
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
														KP,// Kp
														KI,// Ki
														KD);//Kd
		
		
		DyPIDConfiguration rdypid = new DyPIDConfiguration(	2,//PID group 2
															19,//Input channel number
															DyIOChannelMode.COUNT_IN_INT,//Input mode
															11,//Output Channel
															DyIOChannelMode.SERVO_OUT);//Output mode
		PIDConfiguration rpid =new PIDConfiguration (	2,//PID group
														true,//enabled
														true,//inverted
														true,//Async
														KP,// Kp
														KI,// Ki
														KD);//Kd
		dyio.ConfigureDynamicPIDChannels(ldypid);
		dyio.ConfigurePIDController(lpid);
		dyio.ConfigureDynamicPIDChannels(rdypid);
		dyio.ConfigurePIDController(rpid);
		
		PIDChannel left = dyio.getPIDChannel(1);
		PIDChannel right = dyio.getPIDChannel(2);
		setPIDChanels(left, right);
		
	}

}
