package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class BluetoothConector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		try{
			if (!ConnectionDialog.getBowlerDevice(dyio)){
				System.exit(1);
				System.err.println("No port!");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		if(dyio.ping() != null){
			System.out.println("Ping OK!!");
			for (int i=0;i<100;i++){
				try{
					System.out.println("Value: "+dyio.getValue(0));
				}catch (Exception e){
					e.printStackTrace();
					System.err.println("Failed after: "+i+" Pings");
					//break;
				}
	
			}
		}
		System.out.println("Finished with all communication OK!");
		dyio.disconnect();
		System.exit(0);
	}

}
