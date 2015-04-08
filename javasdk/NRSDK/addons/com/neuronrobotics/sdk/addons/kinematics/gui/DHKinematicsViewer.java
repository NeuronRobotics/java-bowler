package com.neuronrobotics.sdk.addons.kinematics.gui;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DHKinematicsViewer extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4624867202513493512L;
	DHParameterKinematics robot;


	public DHKinematicsViewer(final DHParameterKinematics bot) {
		robot = bot;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				Jfx3dManager viewer = new Jfx3dManager();
				viewer.attachArm(bot);
				
				setScene(viewer.getScene());
			}
		});

	}
	
    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        JFrame frame  = new JFrame();
        
        
		DyIO.disableFWCheck();
		//Log.enableInfoPrint();
		//Create the references for my known DyIOs
		DyIO master = new DyIO(ConnectionDialog.promptConnection());

		master.connect();
		if(master.isAvailable()){
			DHParameterKinematics model = new DHParameterKinematics(master,"TrobotMaster.xml");
			frame.setContentPane( new DHKinematicsViewer(model));
			
			frame.setSize(1024, 1024);
	        frame.setVisible(true);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
        
        
    }
}
