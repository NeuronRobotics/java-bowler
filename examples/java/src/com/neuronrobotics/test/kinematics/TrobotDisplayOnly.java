package com.neuronrobotics.test.kinematics;
import javax.swing.JFrame;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.gui.DHKinematicsViewer;
import com.neuronrobotics.sdk.addons.kinematics.gui.Jfx3dManager;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
//import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;
// TODO: Auto-generated Javadoc

/**
 * The Class TrobotDisplayOnly.
 */
public class TrobotDisplayOnly implements ITaskSpaceUpdateListenerNR {
	
	/** The model. */
	DHParameterKinematics model;

	/**
	 * Instantiates a new trobot display only.
	 */
	public TrobotDisplayOnly() {
		DyIO.disableFWCheck();
		//Log.enableInfoPrint();
		//Create the references for my known DyIOs
		DyIO master = new DyIO(ConnectionDialog.promptConnection());

		master.connect();
		model = new DHParameterKinematics(master,"TrobotMaster.xml");

		try{
			JFrame frame  = new JFrame();
			frame.setContentPane(new DHKinematicsViewer(model));
			
			frame.setSize(1024, 1024);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		//model.addPoseUpdateListener(this);

		while ( true) {
			//Log.enableInfoPrint();
			ThreadUtil.wait(0,1);
	
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new TrobotDisplayOnly();
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		//System.err.println("Got:"+pose);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTargetTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,TransformNR pose) {}

}
