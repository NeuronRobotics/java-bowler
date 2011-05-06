package com.neuronrobotics.nrconsole.plugin;

import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JPanel;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public interface INRConsoleTabedPanelPlugin {
	public JPanel getTabPane();
	public ArrayList<JMenu> getMenueItems();
	public boolean isMyNamespace( ArrayList<String> names);
	public boolean isAcvive();
	public boolean setConnection(BowlerAbstractConnection connection);
}
