package com.neuronrobotics.addons.driving;

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
		AbstractDrivingRobot ackerman;
		if(virtual) {
			
			VirtualWorld w = new VirtualWorld();
			VirtualAckermanBot a = new VirtualAckermanBot(w);
			
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
		
		double driveTime=40;
		ackerman.DriveStraight(10, driveTime);
		ThreadUtil.wait((int) (driveTime*1000));
		//System.exit(0);
	}

}
