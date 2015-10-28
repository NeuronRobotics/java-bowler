package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class TrobotGuiMain.
 */
public class TrobotGuiMain {
	
	/** The start vect. */
	double [] startVect = new double [] { 0,0,0,0,0,0};
	
	/** The model. */
	DHParameterKinematics model;
	
	/**
	 * Instantiates a new trobot gui main.
	 */
	private TrobotGuiMain(){
		
		try{
			final SampleGuiNR gui = new SampleGuiNR();
			final JFrame frame = new JFrame();
			final JTabbedPane tabs = new JTabbedPane();
			JPanel starter = new JPanel(new MigLayout());
			JButton connectReal = new JButton("Connect Robot");
			JButton connectVirtual = new JButton("Connect Virtual");
			connectReal.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					/**
					 * First create the Bowler device connection
					 */
					BowlerAbstractConnection connection = ConnectionDialog.promptConnection();
					DyIO.disableFWCheck();
					DyIO mcon = new DyIO(connection);
					if(!mcon.connect()){
						throw new RuntimeException("Not a bowler Device on connection: "+connection);
					}
					mcon.killAllPidGroups();
					model = new DHParameterKinematics(mcon,"TrobotMaster.xml");
					gui.setKinematicsModel(model);
					try{
						tabs.add("Display",new DHKinematicsViewer(model));
					}catch(Error ex){
						JPanel error = new JPanel(new MigLayout());
						error.add(new JLabel("Error while loading Java3d library:"),"wrap");
						error.add(new JLabel(ex.getMessage()),"wrap");
						tabs.add("Display [ERROR]",error);
						ex.printStackTrace();
					}
					frame.pack();
					frame.setLocationRelativeTo(null);
					zero();
				}
			});
			connectVirtual.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					/**
					 * First create the Bowler device connection
					 */

					model = new DHParameterKinematics();
					gui.setKinematicsModel(model);
					try{
						tabs.add("Display",new DHKinematicsViewer(model));
					}catch(Error ex){
						JPanel error = new JPanel(new MigLayout());
						error.add(new JLabel("Error while loading Java3d library:"),"wrap");
						error.add(new JLabel(ex.getMessage()),"wrap");
						tabs.add("Display [ERROR]",error);
						ex.printStackTrace();
					}
					frame.pack();
					frame.setLocationRelativeTo(null);
					zero();
				}
			});
			starter.add(connectReal);
			starter.add(connectVirtual);
			
			gui.add(starter);
			tabs.add("Control",gui);
			//Add scroller here
			frame.getContentPane().add(tabs);
			frame.setSize(640, 480);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		
	}
	
	/**
	 * Zero.
	 */
	private void zero(){
		try {
			model.setDesiredJointSpaceVector(startVect, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new TrobotGuiMain();
	}
}
