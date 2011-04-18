package com.neuronrobotics.test.nrdk;

import java.io.IOException;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
import com.neuronrobotics.sdk.network.BowlerTCPClient;

public class ExtendGenericPID {
	private ExtendGenericPID(){
		Log.enableDebugPrint(true);
		ExtendedPID pid = new ExtendedPID();
		//if (!ConnectionDialog.getBowlerDevice(pid)){
		//	System.exit(1);
		//}
		try {
			pid.setConnection(new BowlerTCPClient("cortex.wpi.edu", 1965));
			//pid.setConnection(new BowlerTCPClient("192.168.0.134", 1965));
			pid.GetAllPIDPosition();
			pid.GetPIDPosition(2);
			pid.disconnect();
			System.out.println("All OK!");
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pid.disconnect();
			System.exit(1);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExtendGenericPID();
	}
	private class ExtendedPID extends GenericPIDDevice{
		public int getExtendedValue(int group){
			BowlerDatagram bd = send(new ExtendPIDCommand(group));
			if(bd != null){
				return ByteList.convertToInt(bd.getData().getBytes(1, 4),true);
			}
			return 0;
		}
	}
	private class ExtendPIDCommand extends BowlerAbstractCommand{
		public ExtendPIDCommand(int group) {
			setOpCode("_pid");
			setMethod(BowlerMethod.GET);
			getCallingDataStorage().add(group);
		}
	}
}
