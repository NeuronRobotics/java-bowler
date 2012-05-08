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
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.TrobotKinematics;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class KinematicsDeveopmentMain implements ITaskSpaceUpdateListenerNR {
	double [] startVect = new double [] { 0,0,0,0,0,0};
	private TrobotKinematics master;
	TrobotKinematics slave = new TrobotKinematics(); 
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
					setMaster(new TrobotKinematics(mcon,"TrobotMaster.xml"));
					gui.setKinematicsModel(getMaster());
					try {
						slave.setDesiredJointSpaceVector(new double [] {0,0,0,0,0,0},0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try{
						tabs.add("Master",new TrobotViewer(getMaster()));
						tabs.add("Slave",new TrobotViewer(slave));
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
	
	private void zero(){
		try {
			getMaster().setDesiredJointSpaceVector(startVect, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new KinematicsDeveopmentMain();
	}

	public TrobotKinematics getMaster() {
		return master;
	}

	public void setMaster(TrobotKinematics master) {
		this.master = master;
		master.addPoseUpdateListener(this);
	}

	@Override
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		try {
			slave.setDesiredTaskSpaceTransform(pose, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,TransformNR pose) {
		// TODO Auto-generated method stub
		
	}
}
