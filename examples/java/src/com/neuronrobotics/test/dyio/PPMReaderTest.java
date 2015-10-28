package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.IPPMReaderListener;
import com.neuronrobotics.sdk.dyio.peripherals.PPMReaderChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class PPMReaderTest.
 */
public class PPMReaderTest implements IPPMReaderListener{
	
	/**
	 * Instantiates a new PPM reader test.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public PPMReaderTest() throws InterruptedException{
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		
		PPMReaderChannel ppm = new PPMReaderChannel(dyio.getChannel(23));
		ppm.addPPMReaderListener(this);
		
		int servoChan = 4;
		
		new ServoChannel(dyio.getChannel(servoChan));//Sets up the output channel for PPM cross link
		ppm.stopAllCrossLinks();
		int [] cross = ppm.getCrossLink();
		//cross[0]=PPMReaderChannel.NO_CROSSLINK;//shut off the cross link for a channel
		cross[0]=servoChan;//link ppm signal 0 to DyIO channel servoChan 
		ppm.setCrossLink(cross);
		
		while (true){
			Thread.sleep(100);
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	
		try{
			new PPMReaderTest();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.IPPMReaderListener#onPPMPacket(int[])
	 */
	@Override
	public void onPPMPacket(int[] values) {
		String s="PPM event: [";
		for(int i=0;i<values.length;i++){
			s+=values[i]+" ";
		}
		System.out.println(s+"]");
		
	}

}
