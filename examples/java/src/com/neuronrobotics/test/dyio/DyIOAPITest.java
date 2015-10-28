package com.neuronrobotics.test.dyio;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class DyIOAPITest.
 */
public class DyIOAPITest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		
		int num = dyio.getDyIOChannelCount();

		System.out.println("Number of channels = "+num);
		
		for(int i=0;i<num;i++){
			 System.out.println("Channel # "+i);
			 ArrayList<DyIOChannelMode>  modes = dyio.getAvailibleChannelModes(i);
			 
			 for(DyIOChannelMode m:modes){
				 System.out.println("\tHas "+m);
			 }
		
		}
		System.exit(0);
	}

}
