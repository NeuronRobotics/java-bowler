package com.neuronrobotics.nrconsole.plugin.DyIO;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;

public class DyIORegestry {
	private static DyIO dyio = null;
	
	public static boolean setConnection(BowlerAbstractConnection c){
		try{
			get().disconnect();
			get().setConnection(c);
			get().connect();
			return get().isAvailable();
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	public static DyIO get(){
		if(dyio == null)
			dyio = new DyIO();
		return dyio;
	}

}
