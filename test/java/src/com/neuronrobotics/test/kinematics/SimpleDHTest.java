package com.neuronrobotics.test.kinematics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;
import com.neuronrobotics.sdk.addons.kinematics.gui.TrobotViewer;

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
