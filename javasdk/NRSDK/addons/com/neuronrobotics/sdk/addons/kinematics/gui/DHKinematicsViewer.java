package com.neuronrobotics.sdk.addons.kinematics.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.JointLimit;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;


public class DHKinematicsViewer  extends JPanel implements IJointSpaceUpdateListenerNR{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4624867202513493512L;
	DHParameterKinematics robot;
	DHViewer dh;
	private double[] joints;
	private JFrame jf;
	
	public DHKinematicsViewer(DHParameterKinematics bot){
		robot = bot;
		
		JPanel controls = new JPanel(new MigLayout());
		

        JButton resetViewButton = new JButton("Reset View");
        resetViewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dh.resetView();
			}
		});
        
        controls.add(resetViewButton);
 
        dh = new DHViewer(robot.getDhChain(), robot.getCurrentJointSpaceVector());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add("North", controls);
        panel.add("Center", dh);
        
        jf = new JFrame();
        jf.setSize(1024, 768);
        jf.add(panel);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        joints = robot.getCurrentJointSpaceVector();
        robot.addJointSpaceListener(this);
        new updater().start();
        setSize(1024, 768);
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
			setName("Bowler Platform D-H kinematics updater");
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
	public void addTransform(TransformNR pose, String label) {
		dh.addTransform(pose, label, Color.yellow);
	}


	public JFrame getFrame() {
		// TODO Auto-generated method stub
		return jf;
	}
}
