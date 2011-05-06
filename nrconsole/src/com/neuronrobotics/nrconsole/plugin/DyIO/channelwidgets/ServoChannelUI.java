package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.DCMotorOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.PWMOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoChannelUI extends ControlWidget implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	private JSlider sliderUI = new JSlider();
	private JLabel valueUI = new JLabel();
	private JCheckBox liveUpdate = new JCheckBox("Live");
	private JButton save = new JButton("Set Default");
	private int saveValue = 256;
	private DyIOAbstractPeripheral dap;
	
	public ServoChannelUI(ChannelManager channel, DyIOChannelMode mode) {
		super(channel);
		setRecordable(true);
		
		if(mode == DyIOChannelMode.SERVO_OUT) {
			try{
				//System.out.println("Making Servo");
				dap = new ServoChannel(getChannel());
			}catch (Exception e){
				dap = new DigitalInputChannel(getChannel());
			}
		} else if(mode == DyIOChannelMode.PWM_OUT){
			//System.out.println("Making PWM");
			dap = new PWMOutputChannel(getChannel());
		}else if((mode == DyIOChannelMode.DC_MOTOR_VEL)||(mode == DyIOChannelMode.DC_MOTOR_DIR)){
			//System.out.println("Making PWM");
			dap = new DCMotorOutputChannel(getChannel());
		}
		
		setLayout(new MigLayout());

		sliderUI.setMaximum(0);
		sliderUI.setMaximum(255);
		sliderUI.setMajorTickSpacing(15);
		sliderUI.setPaintTicks(true);
		sliderUI.addChangeListener(this);
		
		add(sliderUI);
		add(valueUI);
		add(liveUpdate, "wrap");
		add(save);
		save.addActionListener(this);
		setValue(getChannel().getValue());
	}
	
	private String formatValue(int value) {
		return String.format("%03d", value);
	}

	private void setValue(int value) {
		if(value < 0) {
			value = 0;
		}
		
		if(value > 255) {
			value = 255;
		}
		
		recordValue(value);
		
		sliderUI.setValue(value);
		valueUI.setText(formatValue(value));
	}

	
	public void stateChanged(ChangeEvent e) {
		valueUI.setText(formatValue(sliderUI.getValue()));
		
		if(!liveUpdate.isSelected() && sliderUI.getValueIsAdjusting()) {
			return;
		}
		if(sliderUI.getValue() !=saveValue )
			save.setEnabled(true);
		else
			save.setEnabled(false);
		dap.setValue(sliderUI.getValue());
		
		pollValue();
	}

	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save){
			saveValue  = sliderUI.getValue();
			dap.SavePosition(saveValue);
			save.setEnabled(false);
		}
	}
	
	
	public void pollValue() {
		recordValue(dap.getValue());
	}

	
	public DyIOAbstractPeripheral getPerphera() {
		// TODO Auto-generated method stub
		return null;
	}
}
