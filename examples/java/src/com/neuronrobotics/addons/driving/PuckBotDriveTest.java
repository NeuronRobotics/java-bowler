package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class PuckBotDriveTest.
 */
@SuppressWarnings("unused")
public class PuckBotDriveTest implements IRobotDriveEventListener {
	
	/**
	 * Instantiates a new puck bot drive test.
	 */
	public PuckBotDriveTest(){
		
		PuckBot bot = new Rbe3002Robot();	
		
//		VirtualWorld w = new VirtualWorld();
//		PuckBot bot = new VirtualPuckBot(w);
		
		bot.setPuckBotKinematics(new PuckBotDefaultKinematics());
		bot.addIRobotDriveEventListener(this);
		

		bot.DriveStraight(50, 6);
		
		ThreadUtil.wait(6000);
		
		bot.DriveArc(20, 90, 5);
		
		ThreadUtil.wait(6000);
		
		bot.DriveStraight(-50, 6);
		
		//bot.DriveStraight(-5, 1);
		
		ThreadUtil.wait(6000);
		System.exit(0);
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new PuckBotDriveTest();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IRobotDriveEventListener#onDriveEvent(com.neuronrobotics.addons.driving.AbstractRobotDrive, double, double, double)
	 */
	@Override
	public void onDriveEvent(AbstractRobotDrive source, double x, double y,double orentation) {
		//System.out.println("Drive Event x="+x+" y="+y+" orentation="+Math.toDegrees(orentation));
	}
}
