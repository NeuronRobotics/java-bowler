package com.neuronrobotics.nrconsole.plugin;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.BowlerCam.NRConsoleBowlerCameraPlugin;
import com.neuronrobotics.nrconsole.plugin.DyIO.NRConsoleDyIOPlugin;
import com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler.NRConsoleSchedulerPlugin;
import com.neuronrobotics.nrconsole.plugin.PID.NRConsolePIDPlugin;
import com.neuronrobotics.nrconsole.plugin.bootloader.NRConsoleBootloaderPlugin;
import com.neuronrobotics.nrconsole.plugin.hexapod.HexapodNRConsolePulgin;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class PluginManager {
	private static ArrayList<INRConsoleTabedPanelPlugin> plugins = new ArrayList<INRConsoleTabedPanelPlugin>();
	private GenericDevice gen;
	private BowlerAbstractConnection connection;
	public PluginManager(){
		update();
	}
	/**
	 * This static method is for plugins to add themselves to the list of tabed-paned plugins
	 * @param p a plugin instance to add to the list
	 */
	public static void addNRConsoleTabedPanelPlugin(INRConsoleTabedPanelPlugin p){
		if (!plugins.contains(p)){
			plugins.add(p);
		}
	}
	public boolean disconnect(){
		if(gen != null) {
			gen.disconnect();
		}
		return true;
	}
	public boolean connect() throws Exception{
		disconnect();
		update();
		try {
			connection = ConnectionDialog.promptConnection();
			if(connection == null) {
				return false;
			}
			gen = new GenericDevice(connection);
			if(!gen.connect()) {
				throw new InvalidConnectionException("Connection is invalid");
			}
			if(gen.ping()==null){
				connection = null;
				throw new InvalidConnectionException("Communication failed");
			}
		} catch(Exception e) {
			throw e;
		}
		ArrayList<String >names = gen.getNamespaces();
		for (INRConsoleTabedPanelPlugin p:plugins){
			if(p.isMyNamespace(names)){
				p.setConnection(connection);
			}
		}
		return true;
	}
	public ArrayList<JMenu> getMenueItems(){
		ArrayList<JMenu> items = new ArrayList<JMenu>() ;
		for (INRConsoleTabedPanelPlugin plugs:plugins){
			if(plugs.isAcvive()){
				ArrayList<JMenu> m = plugs.getMenueItems();
				if(m != null){
					for (JMenu i: m){
						items.add(i);
					}
				}
			}
		}
		return items;
	}
	public ArrayList<JMenu> getMenueItems(JPanel panel){
		ArrayList<JMenu> items = new ArrayList<JMenu>() ;
		for (INRConsoleTabedPanelPlugin plugs:plugins){
			if(plugs.getTabPane() == panel && plugs.isAcvive()){
				ArrayList<JMenu> m = plugs.getMenueItems();
				if(m != null){
					for (JMenu i: m){
						i.setVisible(true);
						items.add(i);
					}
				}
			}
		}
		return items;
	}
	public ArrayList<JPanel> getPanels(){
		 ArrayList<JPanel> back =  new ArrayList<JPanel>();
		 for (INRConsoleTabedPanelPlugin p:plugins){
			 if(p.isAcvive()){
				 back.add(p.getTabPane());
			 }
		 }
		 return back;	
	}
	public boolean ping() {
		try {
			return gen.isAvailable();
		}catch(Exception e) {
			return false;
		}
	}
	/**
	 * Update the plugin state data
	 */
	public void update() {
		//System.out.println("Clearing tab list");
		plugins = new ArrayList<INRConsoleTabedPanelPlugin>();
		// HACK this should load using OSGI
		// Once instantiated they add themselves to the static list of plugins
		new NRConsoleDyIOPlugin();
		new NRConsolePIDPlugin();
		new NRConsoleBowlerCameraPlugin();
		new NRConsoleBootloaderPlugin();
		
		//new NRConsoleSchedulerPlugin();
		//END HACK
	}
}
