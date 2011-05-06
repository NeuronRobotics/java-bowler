package com.neuronrobotics.nrconsole.plugin.PID;

import java.util.ArrayList;

import javax.swing.JMenu;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.INRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.DyIO.DyIORegestry;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;

public class NRConsolePIDPlugin implements INRConsoleTabedPanelPlugin {
	private boolean active = false;
	private boolean dypid = false;
	//private DyIO dyio;
	private GenericPIDDevice pid;
	private PIDControlGui gui;
	public NRConsolePIDPlugin(){
		PluginManager.addNRConsoleTabedPanelPlugin(this);
	}
	
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		return gui;
	}


	
	public boolean isMyNamespace(ArrayList<String> names) {
		for(String s:names){
			if(s.contains("neuronrobotics.dyio.*")){
				dypid = true;
			}
			if(s.contains("bcs.pid.*")){
				active= true;
			}
		}
		return isAcvive();
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		if(!dypid){
			pid = new  GenericPIDDevice(connection);
			pid.connect();
			gui = new PIDControlGui(pid);
		}else{
			DyIORegestry.setConnection(connection);
			gui = new PIDControlGui();
		}
		
		return true;
	}

	
	public boolean isAcvive() {
		// TODO Auto-generated method stub
		return active;
	}

	
	public ArrayList<JMenu> getMenueItems() {
		// TODO Auto-generated method stub
		return null;
	}

}
