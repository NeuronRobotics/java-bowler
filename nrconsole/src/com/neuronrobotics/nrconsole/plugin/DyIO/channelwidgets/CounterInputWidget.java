package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.CounterInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.ICounterInputListener;

public class CounterInputWidget extends ControlWidget implements ICounterInputListener,ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CounterInputChannel ci;
	private JTextField field = new JTextField();
	private JButton refresh = new JButton("Refresh");
	private JCheckBox async = new JCheckBox("Async");
	
	public CounterInputWidget(ChannelManager c, DyIOChannelMode mode) {
		super(c);
		setRecordable(true);
		ci = new CounterInputChannel(getChannel());
		field.setColumns(10);
		field.setEnabled(false);

		add(field);
		add(refresh);
		//add(async);
		
		ci.addCounterInputListener(this);
		refresh.addActionListener(this);
		async.addActionListener(this);
		
		setValue(0);
		ci.addCounterInputListener(this);
	}
	private void setValue(int value) {
		field.setText(new Integer(value).toString());
	}
	
	public void onCounterValueChange(CounterInputChannel source, int value) {
		setValue(value);
		async.setSelected(true);
	}

	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == field) { 
			
		}else if(e.getSource() == refresh) {
			setValue(ci.getValue());
		}else if(e.getSource() == async) {
			if(!async.isSelected()) {
				ci.setAsync(false);
				ci.removeCounterInputListener(this);
			} else {
				ci.addCounterInputListener(this);
				ci.setAsync(true);
			}
		}
		
	}
	
	public DyIOAbstractPeripheral getPerphera() {
		// TODO Auto-generated method stub
		return ci;
	}

}
