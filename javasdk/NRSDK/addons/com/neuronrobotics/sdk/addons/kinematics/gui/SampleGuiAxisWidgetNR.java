package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.JointLimit;

import net.miginfocom.swing.MigLayout;

public class SampleGuiAxisWidgetNR extends JPanel implements IJointSpaceUpdateListenerNR{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1025603501934236961L;
	private int axis;
	private AbstractKinematicsNR model;
	private double myJointValue = 0;
	private JTextField value = new JTextField(7);
	private JTextField newTarget = new JTextField(7);
	private JTextField target = new JTextField(7);
	private JButton set = new JButton("Set Target");
	private JButton jogp = new JButton("Jog+");
	private JButton jogm = new JButton("Jog-");
	private JButton home = new JButton("Home");
	private JTextField jogInc = new JTextField(7);
	/**
	 * 
	 * @param i which axis this is to represent
	 * @param model
	 */
	public SampleGuiAxisWidgetNR(int i, AbstractKinematicsNR m) {
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		setAxis(i);
		model=m;
		model.addJointSpaceListener(this);
		add(new JLabel("Axis #"+getAxis()+" Name=\""+model.getLinkConfiguration(i).getName()+"\""));
		
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
		add(posPanel,"pos 150 20");
		myJointValue = model.getCurrentJointSpaceVector()[getAxis()];
		updateAxisUI();
	}
	private double getNewTarget(){
		try{
			return Double.parseDouble(newTarget.getText());
		}catch (Exception e){
			return 0;
		}
	}
	private double getCurrentTarget(){
		try{
			return Double.parseDouble(target.getText());
		}catch (Exception e){
			return 0;
		}
	}
	private double getJogIncrement(){
		try{
			return Double.parseDouble(jogInc.getText());
		}catch (Exception e){
			return 0;
		}
	}
	private AbstractKinematicsNR getModel() {
		return model;
	}
	@Override
	public void onJointSpaceUpdate(AbstractKinematicsNR source, double[] joints) {
		myJointValue = joints[getAxis()];
		updateAxisUI();
	}

	private void updateAxisUI() {
		value.setText(new DecimalFormat("000.000").format(myJointValue));
	}
	@Override
	public void onJointSpaceTargetUpdate(AbstractKinematicsNR source,double[] joints) {
		String txt = new DecimalFormat("000.000").format(joints[getAxis()]);
		newTarget.setText(txt);
		target.setText(txt);
	}
	
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
	@Override
	public void onJointSpaceLimit(AbstractKinematicsNR source, int axis,
			JointLimit event) {		
	}
	public void setAxis(int axis) {
		this.axis = axis;
	}
	public int getAxis() {
		return axis;
	}
	
}
