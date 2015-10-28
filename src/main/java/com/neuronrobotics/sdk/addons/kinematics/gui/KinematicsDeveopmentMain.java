package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class KinematicsDeveopmentMain.
 */
public class KinematicsDeveopmentMain implements ITaskSpaceUpdateListenerNR {
	
	/** The start vect. */
	double [] startVect = new double [] { 0,0,0,0,0,0};
	
	/** The master. */
	private DHParameterKinematics master;
	
	/** The slave. */
	DHParameterKinematics slave = new DHParameterKinematics(); 
	
	/**
	 * Instantiates a new kinematics deveopment main.
	 */
	private KinematicsDeveopmentMain(){
		
		try{
			final SampleGuiNR gui = new SampleGuiNR();
			final JFrame frame = new JFrame();
			final JTabbedPane tabs = new JTabbedPane();
			JPanel starter = new JPanel(new MigLayout());
			JButton connectReal = new JButton("Connect Robot");
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
					setMaster(new DHParameterKinematics(mcon,"TrobotMaster.xml"));
					gui.setKinematicsModel(getMaster());
					try {
						slave.setDesiredJointSpaceVector(new double [] {0,0,0,0,0,0},0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try{
						tabs.add("Master",new DHKinematicsViewer(getMaster()));
						tabs.add("Slave",new DHKinematicsViewer(slave));
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
			getMaster().setDesiredJointSpaceVector(startVect, 2);
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
		new KinematicsDeveopmentMain();
	}

	/**
	 * Gets the master.
	 *
	 * @return the master
	 */
	public DHParameterKinematics getMaster() {
		return master;
	}

	/**
	 * Sets the master.
	 *
	 * @param master the new master
	 */
	public void setMaster(DHParameterKinematics master) {
		this.master = master;
		master.addPoseUpdateListener(this);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		try {
			slave.setDesiredTaskSpaceTransform(pose, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTargetTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,TransformNR pose) {
		// TODO Auto-generated method stub
		
	}
}
