package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.IPPMReaderListener;
import com.neuronrobotics.sdk.dyio.peripherals.PPMReaderChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PPMReaderTest implements IPPMReaderListener{
	public PPMReaderTest() throws InterruptedException{
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		PPMReaderChannel ppm = new PPMReaderChannel(dyio.getChannel(23));
		ppm.addPPMReaderListener(this);
		while (true){
			Thread.sleep(100);
		}
	}
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) {
	
		try{
			new PPMReaderTest();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onPPMPacket(int[] values) {
		String s="PPM event: [";
		for(int i=0;i<values.length;i++){
			s+=values[i]+" ";
		}
		System.out.println(s+"]");
		
	}

}
