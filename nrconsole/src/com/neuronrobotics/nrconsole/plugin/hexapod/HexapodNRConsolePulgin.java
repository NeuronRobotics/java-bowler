package com.neuronrobotics.nrconsole.plugin.hexapod;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class HexapodNRConsolePulgin extends AbstractNRConsoleTabedPanelPlugin {
	public static final String[] myNames ={"neuronrobotics.dyio.*"};
	private HexapodConfigPanel hex;// = new HexapodConfigPanel();
	public HexapodNRConsolePulgin(){
		super(myNames);
	}
	
	public JPanel getTabPane() {
		return hex;
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		return hex.setConnection(connection);
	}

}
