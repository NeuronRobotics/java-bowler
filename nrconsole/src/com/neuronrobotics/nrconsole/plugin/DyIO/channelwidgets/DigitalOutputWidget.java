package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;

public class DigitalOutputWidget extends ControlWidget implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private boolean state;
	
	private JButton button = new JButton();
	
	private DigitalOutputChannel doc;
	
	public DigitalOutputWidget(ChannelManager channel) {
		super(channel);
		setRecordable(true);
		
		doc = new DigitalOutputChannel(getChannel());
		
		add(button);
		
		button.addActionListener(this);
		
		setValue(doc.isHigh());
	}
	
	private void setValue(boolean value) {
		state=value;
		if(value) {
			button.setText("High");
			recordValue(255);
		} else {
			button.setText("Low");
			recordValue(0);
		}
	}

	
	public void actionPerformed(ActionEvent e) { 
		if(doc.setHigh(!state)) {
			setValue(!state);
		}
	}

	
	public void pollValue() {
		setValue(doc.isHigh());
	}

	
	public DyIOAbstractPeripheral getPerphera() {
		// TODO Auto-generated method stub
		return null;
	}
}
