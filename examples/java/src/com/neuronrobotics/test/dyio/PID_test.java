package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PDVelocityConfiguration;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class PID_test implements IPIDEventListener{
	
	public PID_test(){
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		Log.enableSystemPrint(false);
		System.out.println("Availible PID channels = "+dyio.getPIDChannelCount());
		PDVelocityConfiguration conf = dyio.getPDVelocityConfiguration(2);
		System.out.println("VPDa = "+conf);
		
		conf.setKP(.2);
		dyio.ConfigurePDVelovityController(conf);
		
		System.out.println("VPDb = "+dyio.getPDVelocityConfiguration(2));
		
		
		dyio.addPIDEventListener(this);
		DyPIDConfiguration dypid = new DyPIDConfiguration(	0,//PID group 0
															23,//Input channel number
															DyIOChannelMode.COUNT_IN_INT,//Input mode
															11,//Output Channel
															DyIOChannelMode.SERVO_OUT);//Output mode
		PIDConfiguration pid =new PIDConfiguration (	0,//PID group
															true,//enabled
															true,//inverted
															true,//Async
															1,// Kp
															0,// Ki
															0,//Kd
															//Latch values are only used with the Counter since analog is absolute and can not change its value
															37,//Value to load to the controller if the index pin is used. This value can be anything
															true,//Use the auto-load of a latched in value when using the index pin
															true);//Set the setpoint to the current location when index it reached
		
		//Setup the controller with the 2 configurations
		dyio.ConfigureDynamicPIDChannels(dypid);
		dyio.ConfigurePIDController(pid);
		
		//Set a single setpoint to the controler
		dyio.SetPIDSetPoint(	0,//Group 0
					500,//Tell the controller to go to position 500
					2.5);//Take 2.5 secoinds to get there
		ThreadUtil.wait(2500);//Wait for the controller to reach its destination
		//Now we will set up the channel wrapping object
		//No further configuration is needed since it was configured above
		PIDChannel chan0 = dyio.getPIDChannel(0);
		//Set a value to be cached by the channel and sent later
		chan0.setCachedTargetValue(-500);
		//Do something else
		ThreadUtil.wait(1000);
		//Now we can flush the entire PID controller
		//NOTE any other cached values will be sent to the device at the same time
		//This is a way of using co-ordinated motion for the PID system
		dyio.flushPIDChannels(2500);
		ThreadUtil.wait(2500);//Wait for the controller to reach its destination
		//This disables all of the PID loops running on the device at once
		dyio.killAllPidGroups();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			new PID_test();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Failed out!");
			System.exit(-1);
		}

	}
	@Override
	public void onPIDEvent(PIDEvent e) {
		//System.out.println(e);
	}
	@Override
	public void onPIDReset(int group, int currentValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		// TODO Auto-generated method stub
		
	}

}
