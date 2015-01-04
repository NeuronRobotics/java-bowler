package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.j3d.TransformGroup;

import com.neuronrobotics.sdk.addons.kinematics.DHChain;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public class DHViewer extends SimpleTransformViewer{

	private ArrayList<TransformGroup> links = new ArrayList<TransformGroup>();
    /**
	 * 
	 */
	private static final long serialVersionUID = -7066991305201979906L;
	
	public DHViewer (DHChain tk,double[] jointSpaceVector){
		
    	ArrayList<TransformNR> chain = tk.getChain(jointSpaceVector);
    	int i=0;
    	for(TransformNR t:chain){
    		Color c;
    		switch(i){
    		case 0:
    			c= Color.red;
    			break;
    		case 1:
    			c= Color.yellow;
    			break;
    		case 2:
    			c= Color.green;
    			break;
    		case 3:
    			c= Color.blue;
    			break;
    		case 4:
    			c= Color.cyan;
    			break;
    		case 5:
    			c= Color.magenta;
    			break;
    		default:
    			c=Color.white;
    		}
    		TransformGroup tmp = addTransform(t, "Link #"+i++,c);
    		links.add(tmp);
    	}
	}
	public void updatePoseDisplay(ArrayList<TransformNR> current) {
		//System.out.println("Updating Display");
		//ArrayList<Transform> current = robot.getDhChain().getChain(robot.getCurrentJointSpaceVector());
		for(int i=0;i<links.size();i++){
    		links.get(i).setTransform(TransformFactory.getTransform(current.get(i)));
    	}
	}

}
