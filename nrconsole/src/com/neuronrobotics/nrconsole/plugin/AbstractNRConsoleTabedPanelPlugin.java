package com.neuronrobotics.nrconsole.plugin;

import java.util.ArrayList;

import javax.swing.JMenu;


public abstract class AbstractNRConsoleTabedPanelPlugin implements INRConsoleTabedPanelPlugin{
	private boolean active = false;
	ArrayList<String> myNames = new ArrayList<String> ();
	public AbstractNRConsoleTabedPanelPlugin(String myNamespaces[]){
		for(int i=0;i<myNamespaces.length;i++){
			myNames.add(myNamespaces[i]);
		}
		PluginManager.addNRConsoleTabedPanelPlugin(this);
	}
	
	
	public ArrayList<JMenu> getMenueItems() {
		return null;
	}

	
	public boolean isMyNamespace(ArrayList<String> names) {
		for(String s:names){
			for(String m:myNames){
				if(s.contains(m)){
					setActive(true);
				}
			}
		}
		return isAcvive();
	}

	private void setActive(boolean a){
		active=a;
	}
	
	public boolean isAcvive() {
		return active;
	}

}
