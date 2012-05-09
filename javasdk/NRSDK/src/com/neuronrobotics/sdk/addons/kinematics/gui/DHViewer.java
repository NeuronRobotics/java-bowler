package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.neuronrobotics.sdk.addons.kinematics.DHChain;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;


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
    		TransformGroup tmp = addTransform(t, "Link #"+i++,new Color(i*40));
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
