/**
 * 
 */
package com.neuronrobotics.sdk.bootloader;

import java.io.IOException;

import com.neuronrobotics.sdk.commands.neuronrobotics.bootloader.BootloaderIDCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.bootloader.EraseFlashCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.bootloader.ProgramSectionCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.bootloader.ResetChipCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class NRBootLoader.
 *
 * @author hephaestus
 */
public class NRBootLoader extends BowlerAbstractDevice {
	
	/**
	 * Instantiates a new NR boot loader.
	 *
	 * @param serialConnection the serial connection
	 */
	public NRBootLoader(BowlerAbstractConnection serialConnection) {
		setConnection(serialConnection);
		
		if(!connect()) {
			throw new RuntimeException("Failed to connect bootloader");
		}
		//Log.enableDebugPrint(true);
		//Log.enableSystemPrint(true);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#connect()
	 */
	@Override
	public boolean connect() {
		if(super.connect()) {
			//System.out.println("Connect OK");
			try {
				getBootloaderID();
			}catch (Exception e) {
				//System.out.println("Failed bootloader test");
				disconnect();
			}
		}
		getConnection().setSynchronusPacketTimeoutTime(3000);
		return isAvailable();
		//Log.enableDebugPrint(true);
		//Log.enableSystemPrint(true);
	}
	
	/**
	 * Gets the bootloader id.
	 *
	 * @return the bootloader id
	 */
	public String getBootloaderID(){
		BowlerDatagram back = send(new BootloaderIDCommand());
		if (back==null)
			return null;
		String s = new String();
		for (Byte b : back.getData()){
			s+=(char)b.byteValue();
		}	
		return s;
	}
	
	/**
	 * Write.
	 *
	 * @param core the core
	 * @param flashData the flash data
	 * @return true, if successful
	 */
	public boolean write(int core, ByteData flashData){
		BowlerDatagram b=null;
		for (int i=0;i<10;i++){
			try{
				b = send(new ProgramSectionCommand(core,(int) flashData.getStartAddress(),new ByteList(flashData.getData())));
			}catch (Exception e){
				e.printStackTrace();
				b=null;
			}
			if (b!=null){
				if(!b.getRPC().contains("_err"))
					return true;
			}
		}
		System.err.println("\nFailed to send 10 times!\n");
		return false;
	}
	
	/**
	 * Erase.
	 *
	 * @param core the core
	 * @return true, if successful
	 */
	public boolean erase(int core){
		return send(new EraseFlashCommand(core)) != null;
	}
	
	/**
	 * Reset.
	 */
	public void reset(){
		//We expect this to fail the connection.
		//No response is expected
		//Disconnect afterwards
		BowlerDatagram bd =BowlerDatagramFactory.build(getAddress(), new ResetChipCommand());
		try {
			getConnection().sendAsync(bd);
			getConnection().getDataOuts().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		disconnect();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAllResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		////System.out.println(data);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}
}
