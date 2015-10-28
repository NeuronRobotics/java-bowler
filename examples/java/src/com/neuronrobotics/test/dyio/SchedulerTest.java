package com.neuronrobotics.test.dyio;


import java.io.File;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.sequencer.CoreScheduler;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class SchedulerTest.
 */
public class SchedulerTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try{
			DyIO dyio = new DyIO(ConnectionDialog.promptConnection());
			dyio.connect();
			CoreScheduler cs = new CoreScheduler(dyio, new File("SparkParty.xml"));
			cs.play();
			while(cs.isPlaying()){
				 try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
	}

}
