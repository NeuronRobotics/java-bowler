package com.neuronrobotics.sdk.dyio;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.dyio.DyIO;


public class DyIORegestry {
	private static DyIO dyio = null;
	private static ArrayList<IConnectionEventListener> disconnectListeners = new ArrayList<IConnectionEventListener> ();
	public static boolean setConnection(BowlerAbstractConnection c){
		try{
			get().setConnection(c);
			get().connect();
			c.addConnectionEventListener(new IConnectionEventListener() {
				public void onDisconnect() {
					DyIORegestry.disconnect();
				}
				public void onConnect() {}
			});
			return get().isAvailable();
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	public static DyIO get(){
		if(dyio == null) {
			dyio = new DyIO();
			for(IConnectionEventListener i:disconnectListeners) {
				dyio.addConnectionEventListener(i);
			}
		}
		return dyio;
	}

	public static void addConnectionEventListener(IConnectionEventListener l ) {
		if(!disconnectListeners.contains(l)) {
			disconnectListeners.add(l);
			get().addConnectionEventListener(l);
		}
	}
	public static void removeConnectionEventListener(IConnectionEventListener l ) {
		if(disconnectListeners.contains(l)) {
			disconnectListeners.remove(l);
			get().removeConnectionEventListener(l);
		}
	}
	
	public static void disconnect() {
		try {
			get().disconnect();
		}catch (Exception ex) {
			
		}
		dyio=null;
	}
}
