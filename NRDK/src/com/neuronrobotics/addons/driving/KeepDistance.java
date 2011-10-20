package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class KeepDistance implements IRobotDriveEventListener,IAnalogInputListener{
	private AckermanBot ack;
	private int anaVal=175;
	public KeepDistance(final DyIO dyio){
		System.out.println("Starting Keep Distance application");
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
		ack = new AckermanBot(new ServoChannel(dyio.getChannel(10)), drive );
		
		AnalogInputChannel IR = new AnalogInputChannel(dyio.getChannel(12),true);
		IR.configAdvancedAsyncNotEqual(100);
		
		IR.addAnalogInputListener(this);
		ack.addIRobotDriveEventListener(this);
		new Thread() {
			public void run() {
				System.out.println("Starting distance thread");
				double distance = .2;
				while(dyio.isAvailable()) {
					ThreadUtil.wait(500);
					if(anaVal>200) {
						ack.DriveStraight(-1*distance, 1);
						//System.out.println("Move back="+anaVal);
					}
					else if(anaVal<150) {
						ack.DriveStraight(distance, 1);
						//System.out.println("Move forward="+anaVal);
					}
					else {
						//System.out.println("Move nowhere="+anaVal);
					}
					
				}
				System.out.println("DyIO is not connected, exiting");
			}
		}.start();
		
	}

	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		anaVal=(int) value;
		//System.out.println("Analog value="+value);
	}

	@Override
	public void onDriveEvent(AbstractRobotDrive source, double x, double y,double orentation) {
		//System.out.println("Robot pos: x="+x+" y="+y);
	}
	public static void main(String [] a) {
		DyIO d = new DyIO();
		ConnectionDialog.getBowlerDevice(d);
		if(d.isAvailable()) {
			new KeepDistance(d);
		}else{
			System.out.println("Failed");
			d.disconnect();
		}
	}
}
