package com.neuronrobotics.test.kinematics;
import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.gui.DHKinematicsViewer;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
//import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;

import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;
public class TrobotDisplayOnly implements ITaskSpaceUpdateListenerNR {
	DHParameterKinematics model;

	public TrobotDisplayOnly() {
		DyIO.disableFWCheck();
		//Log.enableInfoPrint();
		//Create the references for my known DyIOs
		DyIO master = new DyIO(ConnectionDialog.promptConnection());

		master.connect();
		model = new DHParameterKinematics(master,"TrobotMaster.xml");

		try{

			new DHKinematicsViewer(model);
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		model.addPoseUpdateListener(this);

		while ( true) {
			Log.enableInfoPrint();
			ThreadUtil.wait(0,1);
	
		}
	}
	
	public static void main(String[] args) {
		new TrobotDisplayOnly();
	}
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		//System.err.println("Got:"+pose);
	}
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,TransformNR pose) {}

}
