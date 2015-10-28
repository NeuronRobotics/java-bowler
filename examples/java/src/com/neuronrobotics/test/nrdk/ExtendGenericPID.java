package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class ExtendGenericPID.
 */
public class ExtendGenericPID {
	
	/**
	 * Instantiates a new extend generic pid.
	 */
	private ExtendGenericPID(){
		Log.enableDebugPrint();
		ExtendedPID pid = new ExtendedPID();
		if (!ConnectionDialog.getBowlerDevice(pid)){
			System.exit(1);
		}
		try {
			System.out.println("Extended get position: "+pid.getExtendedValue(0));
			pid.GetAllPIDPosition();
			pid.GetPIDPosition(2);
			pid.disconnect();
			System.out.println("All OK!");
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pid.disconnect();
			System.exit(1);
		}
	}

	/**
	 * The Class ExtendedPID.
	 */
	private class ExtendedPID extends GenericPIDDevice{
		
		/**
		 * Gets the extended value.
		 *
		 * @param group the group
		 * @return the extended value
		 */
		public int getExtendedValue(int group){
			BowlerDatagram bd = send(new ExtendPIDCommand(group));
			if(bd != null){
				return ByteList.convertToInt(bd.getData().getBytes(1, 4),true);
			}
			return 0;
		}
	}
	
	/**
	 * The Class ExtendPIDCommand.
	 */
	private class ExtendPIDCommand extends BowlerAbstractCommand{
		
		/**
		 * Instantiates a new extend pid command.
		 *
		 * @param group the group
		 */
		public ExtendPIDCommand(int group) {
			setOpCode("_pid");
			setMethod(BowlerMethod.GET);
			getCallingDataStorage().add(group);
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new ExtendGenericPID();
	}
}
