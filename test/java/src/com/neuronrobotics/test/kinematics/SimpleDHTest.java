package com.neuronrobotics.test.kinematics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;
import com.neuronrobotics.sdk.addons.kinematics.gui.TrobotViewer;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class SimpleDHTest {
	DHParameterKinematics model = new DHParameterKinematics(null,
			SimpleDHTest.class.getResourceAsStream("SimpleDH.xml"),
			SimpleDHTest.class.getResourceAsStream("SimpleDH.xml"));
	double [] startVect = new double [] { 0,0};
	public SimpleDHTest(){
		final SampleGuiNR gui = new SampleGuiNR();
		final JFrame frame = new JFrame();
		final JTabbedPane tabs = new JTabbedPane();
		gui.setKinematicsModel(model);
		try{
			tabs.add("Display",new TrobotViewer(model));
		}catch(Error ex){
			JPanel error = new JPanel(new MigLayout());
			error.add(new JLabel("Error while loading Java3d library:"),"wrap");
			error.add(new JLabel(ex.getMessage()),"wrap");
			tabs.add("Display [ERROR]",error);
			ex.printStackTrace();
		}
		
		frame.setLocationRelativeTo(null);
		zero();
		tabs.add("Control",gui);
		//Add scroller here
		frame.getContentPane().add(tabs);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		
		Log.enableDebugPrint(false);
		
		try {
			model.setDesiredJointAxisValue(0, 45, 1);
			model.setDesiredJointAxisValue(1, 45, 1);
			ThreadUtil.wait(2000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Matrix m = model.getJacobian();
		System.out.println("Jacobian = "+TransformNR.getMatrixString(m));
		
	}
	
	private void zero(){
		try {
			model.setDesiredJointSpaceVector(startVect, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SimpleDHTest();

	}

}
