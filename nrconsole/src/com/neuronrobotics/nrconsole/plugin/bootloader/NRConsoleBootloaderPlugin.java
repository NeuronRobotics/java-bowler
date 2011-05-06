package com.neuronrobotics.nrconsole.plugin.bootloader;

import javax.swing.JPanel;
import javax.swing.UIManager;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleBootloaderPlugin extends AbstractNRConsoleTabedPanelPlugin {
	private  BootloaderPanel bcp = new  BootloaderPanel();
	public static final String[] myNames ={"neuronrobotics.bootloader.*"};

	public NRConsoleBootloaderPlugin(){
		super(myNames);
	}
	
	
	public JPanel getTabPane() {
		return bcp;
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		return bcp.setConnection(connection);
	}
}
