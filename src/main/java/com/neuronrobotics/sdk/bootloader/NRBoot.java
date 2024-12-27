package com.neuronrobotics.sdk.bootloader;


import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.serial.SerialConnection;

//  Auto-generated Javadoc
/**
 * The Class NRBoot.
 */
public class NRBoot {
	
	/** The boot. */
	private NRBootLoader boot;
	
	/** The loader. */
	private CoreLoader loader;
	
	/** The progress max. */
	private int progressMax=0;
	
	/** The progress value. */
	private int progressValue=0;
	
	/**
	 * Instantiates a new NR boot.
	 *
	 * @param pm the pm
	 */
	public NRBoot(BowlerAbstractDevice pm){
		try {
			boot=(NRBootLoader)pm;
		}catch(RuntimeException e) {
			//e.printStackTrace();
			String message = "Not a bootloader device";
    		//JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
    		throw e;
		}
		//com.neuronrobotics.sdk.common.Log.error("Connection to bowler device ready");
	}
	
	/**
	 * Instantiates a new NR boot.
	 *
	 * @param serialPort the serial port
	 */
	public NRBoot(String serialPort){
		this.boot=new NRBootLoader(new SerialConnection(serialPort));
		boot.connect();
		if (boot.ping()){
			//com.neuronrobotics.sdk.common.Log.error("Connection to bowler device ready");
			return;
		}
		//com.neuronrobotics.sdk.common.Log.error("Not a Bowler Device");
		boot.disconnect();
		boot=null;
	}
	
	/**
	 * Load.
	 *
	 * @param core the core
	 * @return true, if successful
	 */
	public boolean load(Core core) {
		
		String id = getDevice().getBootloaderID();
		if (id==null){
			com.neuronrobotics.sdk.common.Log.error("Device is not a bootloader");
			return false;
		}else if (id.contains(core.getType().getReadableName())) {
			//com.neuronrobotics.sdk.common.Log.error("Bootloader ID:"+core.getType().getReadableName());
		}else{
			com.neuronrobotics.sdk.common.Log.error("##core is Invalid##\nExpected:"+core.getType().getReadableName()+" got: "+id);
			return false;
		}
		
		IntelHexParser parse = getParser(core);
		if(parse==null)
			return false;
		send(parse,core.getIndex());
		return true;
	}
	
	/**
	 * Gets the parser.
	 *
	 * @param core the core
	 * @return the parser
	 */
	private IntelHexParser getParser(Core core) {
		try {
			return new IntelHexParser(core.getLines(),core.getType());
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Send.
	 *
	 * @param parse the parse
	 * @param core the core
	 */
	private void send(IntelHexParser parse,int core){
		boot.erase(core);
		//com.neuronrobotics.sdk.common.Log.error("Writing to flash");
		int printLine=0;
		ByteData line = parse.getNext();
		while (line != null){
	
			if(!boot.write(core, line)){
				//com.neuronrobotics.sdk.common.Log.error("Failed to write, is the device in bootloader mode?");
				return;
			}

			line = parse.getNext();
			//System.out.print(".");
			progressValue++;
			printLine++;
			if (printLine>100){
				printLine=0;
				//System.out.print("\n");
			}
		}
		//System.out.print("\n");
	}
	
	/**
	 * Reset.
	 */
	public void reset(){
		try{
			boot.reset();
		}catch(Exception e){
			boot.disconnect();
		}
	}

	/**
	 * Gets the device.
	 *
	 * @return the device
	 */
	public NRBootLoader getDevice() {
		return boot;
	}
	
	/**
	 * Load cores.
	 *
	 * @param cores the cores
	 */
	public void loadCores(ArrayList<Core> cores) {
		loader = new CoreLoader(cores);
		loader.start();
	}
	
	/**
	 * Checks if is load done.
	 *
	 * @return true, if is load done
	 */
	public boolean isLoadDone() {
		return loader.isDone;
	}
	
	/**
	 * The Class CoreLoader.
	 */
	private class CoreLoader extends Thread{
		
		/** The cores. */
		ArrayList<Core> cores;
		
		/** The is done. */
		public boolean isDone=false;
		
		/** The val. */
		public int val=0;
		
		/**
		 * Instantiates a new core loader.
		 *
		 * @param cores the cores
		 */
		public CoreLoader(ArrayList<Core> cores){
			this.cores=cores;
			progressMax=0;
			for (Core b:cores){
				progressMax += getParser(b).size();
			}
			progressValue=0;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				for (Core b:cores){
					val++;
					load(b);
				}
				reset();
			}catch(Exception e) {
				e.printStackTrace();
				String message = "This device is not a bootloader";
        		JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
			}
			progressValue=0;
			isDone=true;
		}
	}

	/**
	 * Gets the progress max.
	 *
	 * @return the progress max
	 */
	public int getProgressMax() {
		// Auto-generated method stub
		return progressMax;
	}
	
	/**
	 * Gets the progress value.
	 *
	 * @return the progress value
	 */
	public int getProgressValue() {
		return progressValue;
	}

}
