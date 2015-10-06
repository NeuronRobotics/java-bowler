package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.util.ThreadUtil;

@SuppressWarnings("unused")
public class PuckBotDriveTest implements IRobotDriveEventListener {
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
	 * @param args
	 */
	public static void main(String[] args) {
		new PuckBotDriveTest();
	}

	@Override
	public void onDriveEvent(AbstractRobotDrive source, double x, double y,double orentation) {
		//System.out.println("Drive Event x="+x+" y="+y+" orentation="+Math.toDegrees(orentation));
	}
}
