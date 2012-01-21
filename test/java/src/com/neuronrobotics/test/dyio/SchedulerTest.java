package com.neuronrobotics.test.dyio;


import java.io.File;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.sequencer.CoreScheduler;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class SchedulerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio = new DyIO(ConnectionDialog.promptConnection());
		dyio.connect();
		CoreScheduler cs = new CoreScheduler(dyio, new File("Test.xml"));
		cs.play();
		while(cs.isPlaying()){
			 try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 System.exit(0);
	}

}
