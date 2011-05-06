package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class PIDControlWidget extends JPanel implements IPIDEventListener {
	private static final long serialVersionUID = 3L;
	private static final double defaultKp = 1;
	private static final double defaultKi = 0;
	private static final double defaultKd = 0;
	private JTextField kp=new JTextField(new Double(defaultKp).toString(),10);
	private JTextField ki=new JTextField(new Double(defaultKi).toString(),10);
	private JTextField kd=new JTextField(new Double(defaultKd).toString(),10);
	private JCheckBox  inverted =new JCheckBox("Invert control");
	JButton  pidSet = new JButton("Start");
	JButton  pidStop = new JButton("Stop");
	private JTextField setpoint=new JTextField(new Double(defaultKd).toString(),10);
	private JButton  setSetpoint = new JButton("Set Setpoint");
	private JButton  zero = new JButton("Reset PID");
	private JLabel   currentPos = new JLabel("0");
	private AdvancedPIDWidget advanced =null;
	
	private JPanel pidRunning = new JPanel(new MigLayout());
	
	private PIDGraph graph;
	
	private boolean set = false;
	
	private PIDControlGui tab;

	private int group;
	private PIDConfiguration pidconfig; 
	private int setpointValue;
	private int positionValue;
	public PIDControlWidget(int group, int startValue, PIDControlGui tab) {
		tab.getPidDevice().addPIDEventListener(this);
		currentPos.setText(new Integer(startValue).toString());
		setpointValue=startValue;
		setPositionValue(startValue);
		setLayout(new MigLayout());
		setGui(tab);
		setGroup(group);
		pidconfig=new PIDConfiguration(getGroup(), false, false, false, 1, 0, 0);
	    inverted.setSelected(true);
	    
		pidSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				double p=0,i=0,d=0;
				try{
					p=Double.parseDouble(kp.getText());
				}catch(Exception e){
					kp.setText(new Double(defaultKp).toString());
					JOptionPane.showMessageDialog(null, "Bad PID values, resetting.", "PID Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try{
					i=Double.parseDouble(ki.getText());
				}catch(Exception e){
					ki.setText(new Double(defaultKi).toString());
					JOptionPane.showMessageDialog(null, "Bad PID values, resetting.", "PID Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try{
					d=Double.parseDouble(kd.getText());
				}catch(Exception e){
					kd.setText(new Double(defaultKd).toString());
					JOptionPane.showMessageDialog(null, "Bad PID values, resetting.", "PID Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				setPID(p, i, d);
				int cur = GetPIDPosition();
				setSetpoint(cur);
				setPositionValue(cur);
				pidRunning.setVisible(true);
			}
		});

		pidStop.setEnabled(false);
		pidStop.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				stopPID();
			}
		});
		zero.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				ResetPIDChannel();
				int val = GetPIDPosition();
				setSetpoint(val);
				currentPos.setText(new Integer(val).toString());
			}
		});
		
		setpoint.setText(new Integer(startValue).toString());
		setpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					setSetpoint(getSetPoint());
				}catch(Exception e){
					setpoint.setText(new Integer(0).toString());
					return;
				}
			}
		});
		setSetpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					setSetpoint(getSetPoint());
				}catch(Exception e){
					setpoint.setText(new Integer(0).toString());
					return;
				}
			}
		});
		
		
		populatePID();
	    
		JPanel constants = new JPanel(new MigLayout());
	    constants.setMinimumSize(new Dimension(300, 50));
	    constants.add(new JLabel("PID Gain Constants"),"wrap");
		constants.add(new JLabel("proportional"));
	    constants.add(kp,"wrap");
	    constants.add(new JLabel("integral"));
	    constants.add(ki,"wrap");
	    constants.add(new JLabel("derivitive"));
	    constants.add(kd,"wrap");
	    constants.add(pidSet);
	    constants.add(inverted);
	    
	    pidRunning.add(new JLabel("PID pidRunning for group "+((int)getGroup())),"wrap");
	    //pidRunning.add(pidSet);
	    pidRunning.add(pidStop,"wrap");
	    //pidRunning.add(inverted);
	    pidRunning.add(zero,"wrap");
	    pidRunning.add(setSetpoint);
	    pidRunning.add(setpoint,"wrap");
	    pidRunning.add(new JLabel("Current Position = "));
	    pidRunning.add(currentPos,"wrap");
	    
	    pidRunning.add(advanced,"wrap");
	    
	    
	    JPanel uiPanel = new JPanel();
	    if(getGui().isDyPID()) {
	    	uiPanel.add(new DyPIDControlWidget(this));		
		}
	    uiPanel.add(constants);
		uiPanel.add(pidRunning,"wrap");
		
		graph = new PIDGraph(group);
		
		add(uiPanel,"wrap");
		add(graph,"wrap");
		
		repaint();
		Updater up = new Updater();
		up.start();
		pidRunning.setVisible(false);
	}
	
	private void populatePID() {
		advanced = new  AdvancedPIDWidget(this);
	    advanced.setEnabled(false);
		PIDConfiguration conf = getPIDConfiguration();
		kp.setText(new Double(conf.getKP()).toString());
		ki.setText(new Double(conf.getKI()).toString());
		kd.setText(new Double(conf.getKD()).toString());
		inverted.setSelected(conf.isInverted());
		if(conf.isEnabled()){
			pidStop.setEnabled(true);
			advanced.setEnabled(true);
		}
	}
	

	public void setGroup(int group) {
		this.group = group;
	}
	public char getGroup() {
		return (char)group;
	}
	public void setGui(PIDControlGui tab) {
		this.tab = tab;
	}
	public PIDControlGui getGui() {
		return tab;
	}
	void stopPID(){
		pidStop.setEnabled(false);
		pidconfig.setEnabled(false);
		ConfigurePIDController(pidconfig);
		advanced.setEnabled(false);
		pidRunning.setVisible(false);
	}
	private void setPID(double p,double i,double d){
		setSet(true);
		pidStop.setEnabled(true);
		pidconfig.setEnabled(true);
		pidconfig.setInverted(inverted.isSelected());
		pidconfig.setAsync(true);
		pidconfig.setKP(p);
		pidconfig.setKI(i);
		pidconfig.setKD(d);
		ConfigurePIDController(pidconfig);
		advanced.setEnabled(true);
	}
	public void setSet(boolean set) {
		this.set = set;
	}
	public boolean isReady() {
		return set;
	}
	
	
	public String toString() {
		return "GROUP # "+(int)getGroup();
	}

	
	public void onPIDEvent(PIDEvent e) {
		if(e.getGroup()==getGroup()){
			//System.out.println("From PID control widget: "+e);
			
			setPositionValue(e.getValue());
			
		}
	}
	public void setSetpoint(int setPoint){
		SetPIDSetPoint(setPoint,0);
		setpointValue=setPoint;
		setpoint.setText(new Integer(setPoint).toString());
		graphVals();
		pidStop.setEnabled(true);
	}
	private class Updater extends Thread{
		long lastSet;
		long lastPos;
		public void run() {
			while(true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				if(lastSet != setpointValue || lastPos !=getPositionValue() ) {
					graphVals();
					lastSet = setpointValue ;
					lastPos = getPositionValue();
				}
			}
		}
	}
	private void graphVals() {
		if(graph!=null)
			graph.addEvent(setpointValue,getPositionValue());
	}

	public void setPositionValue(int positionValue) {
		currentPos.setText(new Integer(positionValue).toString());
		this.positionValue = positionValue;
		graphVals();
	}
	public int getSetPoint() {
		return Integer.parseInt(setpoint.getText());
	}
	public int getPositionValue() {
		return positionValue;
	}
	
	private void ResetPIDChannel(){
		try{
			getGui().getPidDevice().ResetPIDChannel(getGroup());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Setpoint reset failed", "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return;
		}
	}
	private int GetPIDPosition(){
		try{
			return getGui().getPidDevice().GetPIDPosition(getGroup());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Setpoint get failed", "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return 0;
		}
	}
	private PIDConfiguration getPIDConfiguration(){
		try{
			return getGui().getPidDevice().getPIDConfiguration(getGroup());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Configuration get failed", "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return new PIDConfiguration();
		}
	}
	private void ConfigurePIDController(PIDConfiguration pidconfig){
		try{
			getGui().getPidDevice().ConfigurePIDController(pidconfig);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Configuration failed", "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return;
		}
	}
	private void SetPIDSetPoint(int setPoint,int velocity){
		try{
			getGui().getPidDevice().SetPIDSetPoint(getGroup(), setPoint,velocity);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Setpoint set failed", "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return;
		}
	}
	

	
	public void onPIDReset(int group, int currentValue) {
		// TODO Auto-generated method stub
		
	}

	
	public void onPIDLimitEvent(PIDLimitEvent e) {
		// TODO Auto-generated method stub
		
	}
}
