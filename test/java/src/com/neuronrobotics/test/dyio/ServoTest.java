package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class ServoTest implements IServoPositionUpdateListener{
	
	private ServoTest(){
		DyIO.disableFWCheck();
		DyIO dyio=new DyIO();
		//dyio.enableDebug();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		
		//If your DyIO is using a lower voltage power source, you need to disable the brownout detect
		dyio.setServoPowerSafeMode(false);
		

		
		ServoChannel srv = new ServoChannel (dyio.getChannel(0));
		srv.addIServoPositionUpdateListener(this);
                //Loop 10 times setting the position of the servo 
                //the time the loop waits will be the time it takes for the servo to arrive
		srv.SetPosition(0);
		float time = 2;
		
		System.out.println("Moving with time");
		for(int i = 0; i < 10; i++) {
			// Set the value high every other time, exit if unsuccessful
			int pos = ((i%2==0)?255:0);
                        //This will move the servo from the position it is currentlly in
			srv.SetPosition(pos, time);
			
			// pause between cycles so that the changes are visible
			try {
				Thread.sleep((long) (time*1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

        dyio.disconnect();
        System.exit(0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new ServoTest();

	}

	@Override
	public void onServoPositionUpdate(ServoChannel srv, int position,double time) {
		System.out.println("Servo Position update = "+ position+ " time= "+time );
	}

}
