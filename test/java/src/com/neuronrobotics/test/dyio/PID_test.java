package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PID_test implements IPIDEventListener{
	
	public PID_test(){
		DyIO dyio=new DyIO();
		dyio.SetPrintModes(true, true);
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		
		dyio.addPIDEventListener(this);
		
		DyPIDConfiguration dypid = new DyPIDConfiguration(0,12,DyIOChannelMode.ANALOG_IN,11,DyIOChannelMode.SERVO_OUT);
		PIDConfiguration pid =new PIDConfiguration (0,true,true,true,1,0,0);
		
		dyio.ConfigureDynamicPIDChannels(dypid);
		dyio.ConfigurePIDController(pid);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0); 
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
		System.out.println(e);
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
