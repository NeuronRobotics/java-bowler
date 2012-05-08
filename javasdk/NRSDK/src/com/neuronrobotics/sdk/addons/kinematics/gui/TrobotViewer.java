package com.neuronrobotics.sdk.addons.kinematics.gui;


import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.JointLimit;
import com.neuronrobotics.sdk.addons.kinematics.TrobotKinematics;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import com.sun.j3d.utils.applet.MainFrame; 
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;

import javax.media.j3d.*;
import javax.vecmath.*;

import net.miginfocom.swing.MigLayout;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;


public class TrobotViewer  extends JPanel implements IJointSpaceUpdateListenerNR{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4624867202513493512L;
	TrobotKinematics robot;
	DHViewer dh;
	private double[] joints;
	
	
	public TrobotViewer(TrobotKinematics bot){
		robot = bot;
		
		JPanel controls = new JPanel(new MigLayout());
		
        setLayout(new BorderLayout());
        JButton resetViewButton = new JButton("Reset View");
        resetViewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dh.resetView();
			}
		});
        
        controls.add(resetViewButton);
 
        dh = new DHViewer(robot.getDhChain(), robot.getCurrentJointSpaceVector());
        add("North", controls);
        add("Center", dh);
        joints = robot.getCurrentJointSpaceVector();
        robot.addJointSpaceListener(this);
        new updater().start();
	}


	@Override
	public void onJointSpaceUpdate(AbstractKinematicsNR source, double[] joints) {
		
		for(int i=0;i<joints.length;i++){
			this.joints[i]=joints[i];
			
		}
		
		
	}

	@Override
	public void onJointSpaceTargetUpdate(AbstractKinematicsNR source,double[] joints) {
		//dh.updatePoseDisplay(robot.getDhChain().getChain(joints));
	}

	@Override
	public void onJointSpaceLimit(AbstractKinematicsNR source, int axis,JointLimit event) {
		
	}
	private class updater extends Thread{
		public void run(){
			while(robot.getFactory().isConnected()){
				ThreadUtil.wait(50);
				Log.enableSystemPrint(false);
				double[] tmp = new double[joints.length];
				//System.out.print("\nDisplay update: [");
				for(int i=0;i<joints.length;i++){
					tmp[i]=joints[i];
					//System.out.print(tmp[i]+" ");
				}
				//System.out.print("]");
				try{
					dh.updatePoseDisplay(robot.getDhChain().getChain(tmp));
				}catch(Exception e){
					e.printStackTrace();
				}
				//System.out.println("Display Update");
			}
		}
	}
}
