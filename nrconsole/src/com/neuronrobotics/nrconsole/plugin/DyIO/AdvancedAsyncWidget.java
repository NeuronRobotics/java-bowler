package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.ControlWidget;
import com.neuronrobotics.sdk.commands.bcs.io.AsyncMode;
import com.neuronrobotics.sdk.commands.bcs.io.AsyncThreshholdEdgeType;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;

public class AdvancedAsyncWidget extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3130294816414645000L;
	private ControlPanel controlPanel;
	private JCheckBox async = new JCheckBox("Async");
	private JPanel advanced = new JPanel(new MigLayout());
	private JComboBox type = new JComboBox();
	private JComboBox edge = new JComboBox();
	private JTextField time =  new JTextField(4);
	private JTextField dvalue =  new JTextField(4);
	private JTextField tvalue =  new JTextField(4);
	private JButton update = new JButton("Set Async");
	private JPanel perm = new JPanel(new MigLayout());
	private JPanel tmp=new JPanel(new MigLayout());
	public AdvancedAsyncWidget(){
		setLayout(new MigLayout());
		add(async);
		
		
		async.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				updateAsync();
			}
		});
		type.addItem(AsyncMode.NOTEQUAL);
		type.addItem(AsyncMode.DEADBAND);
		type.addItem(AsyncMode.THRESHHOLD);
		type.addItem(AsyncMode.AUTOSAMP);
		type.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateAsyncPanel();
			}
		});
		
		edge.addItem(AsyncThreshholdEdgeType.BOTH);		
		edge.addItem(AsyncThreshholdEdgeType.RISING);	
		edge.addItem(AsyncThreshholdEdgeType.FALLING);	
		edge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateAsyncPanel();
			}
		});
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateDevice();
			}
		});
		perm.add(update);
		perm.add(type);
		perm.add(time);
		perm.add(new JLabel("ms"));
		advanced.add(perm,"wrap");
		advanced.add(tmp);
	}
	private void updateAsync(){
		DyIOAbstractPeripheral p =getPerpheral();
		if(p!=null){
			if(!async.isSelected()) {
				p.setAsync(false);
				remove(advanced);
			} else {
				p.setAsync(true);
				add(advanced);
			}
		}
	}
	private void updateDevice(){
		int sampTime ;
		int dv;
		int tv;
		try{
			sampTime = new Integer(time.getText());
		}catch(NumberFormatException e){
			sampTime=100;
			time.setText(new Integer(sampTime).toString());
		}
		try{
			dv = new Integer(dvalue.getText());
		}catch(NumberFormatException e){
			dv=10;
			dvalue.setText(new Integer(dv).toString());
		}
		try{
			tv = new Integer(tvalue.getText());
		}catch(NumberFormatException e){
			tv=0;
			tvalue.setText(new Integer(tv).toString());
		}
		switch(getSelectedMode()){
		case AUTOSAMP:
			getPerpheral().configAdvancedAsyncAutoSample(sampTime);
			break;
		case DEADBAND:
			getPerpheral().configAdvancedAsyncDeadBand(sampTime, dv);
			break;
		case NOTEQUAL:
			getPerpheral().configAdvancedAsyncNotEqual(sampTime);
			break;
		case THRESHHOLD:
			getPerpheral().configAdvancedAsyncTreshhold(sampTime, tv, (AsyncThreshholdEdgeType)edge.getSelectedItem());
			break;
		}
		perm.setVisible(true);
		tmp.setVisible(true);
		advanced.setVisible(true);
		setVisible(true);
		invalidate();
		repaint();
		revalidate();
		//System.out.println("Updated device");
	}
	private void updateAsyncPanel(){
		tmp.removeAll();
		switch(getSelectedMode()){
		case AUTOSAMP:
			break;
		case DEADBAND:
			tmp.add(dvalue);
			tmp.add(new JLabel("Deadband Range"));
			break;
		case NOTEQUAL:
			break;
		case THRESHHOLD:
			tmp.add(tvalue);
			tmp.add(new JLabel("Value"));
			tmp.add(edge);
			break;
		}
		updateDevice();
	}
	private void setType(AsyncMode m){
		for (int i=0;i<type.getItemCount();i++){
			AsyncMode tmp =(AsyncMode) type.getItemAt(i);
			if(tmp == m){
				type.setSelectedIndex(i);
			}
		}
		updateAsyncPanel();
	}
	private AsyncMode getSelectedMode(){
		return (AsyncMode) type.getSelectedItem();
	}

	public DyIOAbstractPeripheral getPerpheral(){
		return controlPanel.getPerpheral();
	}

	public void setControlPanel(ControlPanel controlPanel) {
		this.controlPanel=controlPanel;	
		time.setText(new Integer(100).toString());
		switch(getPerpheral().getMode()){
		case ANALOG_IN:
			setType(AsyncMode.DEADBAND);
			break;
		case DIGITAL_IN:
			time.setText(new Integer(5).toString());
		case COUNT_IN_INT:
		case COUNT_OUT_INT:	
			setType(AsyncMode.NOTEQUAL);
			break;
		}
		updateAsync();
	}
}
