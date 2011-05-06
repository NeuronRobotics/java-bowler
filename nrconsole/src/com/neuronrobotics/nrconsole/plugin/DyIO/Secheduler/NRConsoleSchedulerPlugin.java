package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleSchedulerPlugin extends AbstractNRConsoleTabedPanelPlugin{
	public static final String[] myNames ={"neuronrobotics.dyio.*"};
	private SchedulerGui gui = new SchedulerGui();
	public NRConsoleSchedulerPlugin() {
		super(myNames);

	}

	
	public JPanel getTabPane() {
		return gui;
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		return gui.setConnection(connection);
	}

}
