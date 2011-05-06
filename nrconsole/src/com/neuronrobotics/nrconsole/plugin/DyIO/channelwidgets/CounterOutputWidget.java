package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.ICounterInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.ICounterOutputListener;


public class CounterOutputWidget extends ControlWidget implements ActionListener,ICounterOutputListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CounterOutputChannel outChannel;
	private JTextField field = new JTextField();
	private JButton set = new JButton("Set");
	private JButton refresh = new JButton("Refresh");

	
	public CounterOutputWidget(ChannelManager c, DyIOChannelMode mode){
		super(c);
		setRecordable(true);
		outChannel = new CounterOutputChannel(getChannel());
		field.setColumns(10);
		add(field);
		add(set);
		//add(async);
		field.setEnabled(true);
		set.addActionListener(this);
		//async.addActionListener(this);
		setValue(outChannel.getValue());
		outChannel.addCounterOutputListener(this);
	}
	private void setValue(int value) {
		field.setText(new Integer(value).toString());
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == field) { 
			
		}else if(e.getSource()==set){
			try{
				int out = new Integer(field.getText()).intValue();
				//System.out.println("Setting "+ out);
				outChannel.SetPosition(out);
			}catch(Exception e1){
				field.setText("0");
			}
		}else if(e.getSource() == refresh) {
			setValue(outChannel.getValue());
		}
//		else if(e.getSource() == async) {
//			if(!async.isSelected()) {
//				outChannel.setAsync(false);
//				outChannel.removeCounterOutputListener(this);
//			} else {
//				outChannel.setAsync(true);
//				outChannel.addCounterOutputListener(this);
//			}
//		}
		
	}
	
	public void pollValue() {
		
	}
	
	public void onCounterValueChange(CounterOutputChannel source, int value) {
		//System.out.println("Counter output event: "+value);
		setValue(value);
		//async.setSelected(true);
	}
	
	public DyIOAbstractPeripheral getPerphera() {
		// TODO Auto-generated method stub
		return outChannel;
	}
}
