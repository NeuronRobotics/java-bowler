package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class RealTimeLineTrackWithPID implements IPIDEventListener {
	
	int lVal=0;
	int rVal=0;
	private RealTimeLineTrackWithPID(){
		DyIO.disableFWCheck();
		DyIO dyio=new DyIO();
		//dyio.SetPrintModes(true, true);
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		
		dyio.addPIDEventListener(this);
		DyPIDConfiguration dypidR = new DyPIDConfiguration(	1,//PID group 0
															12,//Input channel number
															DyIOChannelMode.ANALOG_IN,//Input mode
															11,//Output Channel
															DyIOChannelMode.SERVO_OUT);//Output mode
		PIDConfiguration pidR =new PIDConfiguration (	1,//PID group
														true,//enabled
														false,//inverted
														true,//Async
														.1,// Kp
														0,// Ki
														0,//Kd
														//Latch values are only used with the Counter since analog is absolute and can not change its value
														0,//Value to load to the controller if the index pin is used. This value can be anything
														false,//Use the auto-load of a latched in value when using the index pin
														false);//Set the setpoint to the current location when index it reached

		DyPIDConfiguration dypidL = new DyPIDConfiguration(	0,//PID group 0
															13,//Input channel number
															DyIOChannelMode.ANALOG_IN,//Input mode
															10,//Output Channel
															DyIOChannelMode.SERVO_OUT);//Output mode
		PIDConfiguration pidL =new PIDConfiguration (	0,//PID group
															true,//enabled
															true,//inverted
															true,//Async
															.1,// Kp
															0,// Ki
															0,//Kd
															//Latch values are only used with the Counter since analog is absolute and can not change its value
															0,//Value to load to the controller if the index pin is used. This value can be anything
															false,//Use the auto-load of a latched in value when using the index pin
															false);//Set the setpoint to the current location when index it reached
		
		//Setup the controller with the configurations
		dyio.ConfigureDynamicPIDChannels(dypidR);
		dyio.ConfigurePIDController(pidR);
		dyio.ConfigureDynamicPIDChannels(dypidL);
		dyio.ConfigurePIDController(pidL);
		
		//Set a single setpoint to the controler
		dyio.SetPIDSetPoint(	0,//Group 0
								100,//Tell the controller to go to position 500
								0);//Take 0 secoinds to get there
		dyio.SetPIDSetPoint(	1,//Group 1
								300,//Tell the controller to go to position 500
								0);//Take 0 secoinds to get there
		
		while(true){
			ThreadUtil.wait(10);
			if(lVal >500 && rVal>500){
				System.out.println("Stop Condition!");
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			new RealTimeLineTrackWithPID();
		}catch (Exception ex){
			ex.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		if(e.getGroup()==0){
			lVal = e.getValue();
		}
		if(e.getGroup()==1){
			rVal = e.getValue();
		}

	}

	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPIDReset(int group, int currentValue) {
		// TODO Auto-generated method stub
		
	}

}
