package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.util.ArrayList;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;
// TODO: Auto-generated Javadoc
//
//import eu.mihosoft.vrl.v3d.CSG;
//import eu.mihosoft.vrl.v3d.Cube;

/**
 * The Class DHKinematicsViewer.
 */
public class DHKinematicsViewer extends JFXPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4624867202513493512L;
	
	/** The robot. */
	DHParameterKinematics robot;


	/**
	 * Instantiates a new DH kinematics viewer.
	 *
	 * @param bot the bot
	 */
	public DHKinematicsViewer(final DHParameterKinematics bot) {
		robot = bot;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				Jfx3dManager viewer = new Jfx3dManager();
				viewer.attachArm(bot);
				ArrayList<Object> links = new ArrayList<Object>();
//				for(DHLink dh:bot.getDhChain().getLinks()){
//					System.out.println("Link D-H values = "+dh);
//					// Create an axis to represent the link
//					CSG cube = new Cube(20).toCSG();
//					//add listner to axis
//					cube.setManipulator(dh.getListener());
//					// add ax to list of objects to be returned
//					links.add(cube);
//				}
				
				setScene(viewer.getScene());
			}
		});

	}
	
    /**
     * The main method.
     *
     * @param args the arguments
     */
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
		Log.enableSystemPrint(false);
        while(true){
        	ThreadUtil.wait(1);
        	master.getAllChannelValues();
        }
        
    }
}
