package com.neuronrobotics.nrconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;
@SuppressWarnings("unused")
public class NRConsole implements ActionListener {
	private NRConsoleWindow nrcWindow = null;

	private PluginManager manager=new PluginManager();
	private MenuBar nrcMenubar = new MenuBar(manager);
	private showManager shower = new showManager ();
	
	public static void main(String [] args) {
		try {
			if(args.length != 0)
				new NRConsole(true);
			else
				new NRConsole(false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public NRConsole(boolean debug) {
		nrcWindow = new NRConsoleWindow();
		nrcWindow.setJMenuBar(nrcMenubar);
		nrcMenubar.setMenues(null);
		nrcMenubar.addActionListener(this);
		
		shower.start();
		if(debug)
			Log.enableDebugPrint(true);
		
		while(!nrcWindow.isShowing()){
			ThreadUtil.wait(100);
		}
		
		while(nrcWindow.isShowing()){
			nrcWindow.repaint();
			ThreadUtil.wait(500);
		}
		manager.disconnect();
		System.out.println("Exit clean");
		System.exit(0);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("set-connection")) {
			System.out.println("Do something with the aciton command.");
		}
	}
	private class showManager extends Thread{
		public void run(){
			while(true){	
				if(nrcMenubar.isReady()){
					nrcMenubar.setMenues(manager.getMenueItems());
					nrcWindow.setDeviceManager(manager);
					nrcWindow.setVisible(true);
					while(nrcMenubar.isReady()){
						ThreadUtil.wait(1000);
						if(!manager.ping())
							nrcMenubar.disconnect();
					}
				}else{
					nrcMenubar.setMenues(null);
					nrcWindow.displayLogo();
					nrcWindow.setVisible(true);
					nrcMenubar.connect();
					while(!nrcMenubar.isReady()){
						ThreadUtil.wait(50);
					}
				}
			}
		}
	}
}
