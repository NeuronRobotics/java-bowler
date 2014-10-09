package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class RealLineTrack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final DyIO d = new DyIO();
		ConnectionDialog.getBowlerDevice(d);
		if(d.isAvailable()) {
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
								.5,0,false,false);//Kd
					d.ConfigureDynamicPIDChannels(dypid);
					d.ConfigurePIDController(pid);
					
					PIDChannel drive = d.getPIDChannel(0);
					ServoChannel srv = new ServoChannel(d.getChannel(10));
					AbstractRobotDrive mainRobot  =  new AckermanBot(	new ServoRotoryLink(srv, 98, 51, 143, 1), 
							drive );

					AbstractSensor line = new LineSensor(	new AnalogInputChannel(d.getChannel(14),true),
											null,
											new AnalogInputChannel(d.getChannel(13),true));
					
					//new LineTrack().runTrack(mainRobot,line);
				}
			}.start();
		}else{
			System.out.println("Failed");
			d.disconnect();
		}
		
	}

}
