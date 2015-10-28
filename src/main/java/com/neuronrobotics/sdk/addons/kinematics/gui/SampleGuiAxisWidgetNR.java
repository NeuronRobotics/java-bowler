package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.JointLimit;

// TODO: Auto-generated Javadoc
/**
 * The Class SampleGuiAxisWidgetNR.
 */
public class SampleGuiAxisWidgetNR extends JPanel implements IJointSpaceUpdateListenerNR{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1025603501934236961L;
	
	/** The axis. */
	private int axis;
	
	/** The model. */
	private AbstractKinematicsNR model;
	
	/** The my joint value. */
	private double myJointValue = 0;
	
	/** The value. */
	private JTextField value = new JTextField(7);
	
	/** The new target. */
	private JTextField newTarget = new JTextField(7);
	
	/** The target. */
	private JTextField target = new JTextField(7);
	
	/** The set. */
	private JButton set = new JButton("Set Target");
	
	/** The jogp. */
	private JButton jogp = new JButton("Jog+");
	
	/** The jogm. */
	private JButton jogm = new JButton("Jog-");
	
	/** The home. */
	private JButton home = new JButton("Home");
	
	/** The jog inc. */
	private JTextField jogInc = new JTextField(7);
	
	/**
	 * Instantiates a new sample gui axis widget nr.
	 *
	 * @param i which axis this is to represent
	 * @param m the m
	 */
	public SampleGuiAxisWidgetNR(int i, AbstractKinematicsNR m) {
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		setAxis(i);
		model=m;
		model.addJointSpaceListener(this);
		add(new JLabel("#"+getAxis()+" \""+model.getLinkConfiguration(i).getName()+"\" "+model.getLinkConfiguration(i).getType()));
		
		value.setEnabled(false);
		target.setEnabled(false);
		target.setText("0.0");
		newTarget.setEnabled(true);
		newTarget.setText("0.0");
		jogInc.setText("5.0");
		set.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					getModel().setDesiredJointAxisValue(getAxis(), getNewTarget(), 3);																			
				} catch (Exception e1) {			
					e1.printStackTrace();
				}
			}
		});
		
		home.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new Thread(){
						public void run(){
							setName("Bowler Platform Homing thread");
							getModel().homeLink(getAxis());
							
							setButtonEnabled(true);
						}
					}.start();
					home.setText("Homing...");
					
					setButtonEnabled(false);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		jogp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					getModel().setDesiredJointAxisValue(getAxis(),getCurrentTarget() +  getJogIncrement(), 1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		jogm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					getModel().setDesiredJointAxisValue(getAxis(),getCurrentTarget() -  getJogIncrement(), 1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JPanel posPanel = new JPanel(new MigLayout());
		posPanel.add(new JLabel("Actual"));
		posPanel.add(value);
		posPanel.add(new JLabel(" mm"));
		posPanel.add(new JLabel("|Target"));
		posPanel.add(target);
		posPanel.add(new JLabel("mm"));
		posPanel.add(new JLabel("|New Target"));
		posPanel.add(newTarget);
		posPanel.add(new JLabel("mm"));
		posPanel.add(set);
		
		//jog
		posPanel.add(jogp);
		posPanel.add(jogInc);
		posPanel.add(jogm);
		posPanel.add(home);
		add(posPanel,"pos 0 20");
		myJointValue = model.getCurrentJointSpaceVector()[getAxis()];
		updateAxisUI();
	}
	
	/**
	 * Gets the new target.
	 *
	 * @return the new target
	 */
	private double getNewTarget(){
		try{
			return Double.parseDouble(newTarget.getText());
		}catch (Exception e){
			return 0;
		}
	}
	
	/**
	 * Gets the current target.
	 *
	 * @return the current target
	 */
	private double getCurrentTarget(){
		try{
			return Double.parseDouble(target.getText());
		}catch (Exception e){
			return 0;
		}
	}
	
	/**
	 * Gets the jog increment.
	 *
	 * @return the jog increment
	 */
	private double getJogIncrement(){
		try{
			return Double.parseDouble(jogInc.getText());
		}catch (Exception e){
			return 0;
		}
	}
	
	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	private AbstractKinematicsNR getModel() {
		return model;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, double[])
	 */
	@Override
	public void onJointSpaceUpdate(AbstractKinematicsNR source, double[] joints) {
		myJointValue = joints[getAxis()];
		updateAxisUI();
	}

	/**
	 * Update axis ui.
	 */
	private void updateAxisUI() {
		value.setText(new DecimalFormat("000.000").format(myJointValue));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceTargetUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, double[])
	 */
	@Override
	public void onJointSpaceTargetUpdate(AbstractKinematicsNR source,double[] joints) {
		String txt = new DecimalFormat("000.000").format(joints[getAxis()]);
		newTarget.setText(txt);
		target.setText(txt);
	}
	
	/**
	 * Sets the button enabled.
	 *
	 * @param b the new button enabled
	 */
	public void setButtonEnabled(boolean b){
		if(b){
			home.setText("Homed");
		}else{
			home.setText("Homing...");
		}
		home.setEnabled(b);
		set.setEnabled(b);
		jogp.setEnabled(b);
		jogm.setEnabled(b);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceLimit(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, int, com.neuronrobotics.sdk.addons.kinematics.JointLimit)
	 */
	@Override
	public void onJointSpaceLimit(AbstractKinematicsNR source, int axis,
			JointLimit event) {		
	}
	
	/**
	 * Sets the axis.
	 *
	 * @param axis the new axis
	 */
	public void setAxis(int axis) {
		this.axis = axis;
	}
	
	/**
	 * Gets the axis.
	 *
	 * @return the axis
	 */
	public int getAxis() {
		return axis;
	}
	
}
