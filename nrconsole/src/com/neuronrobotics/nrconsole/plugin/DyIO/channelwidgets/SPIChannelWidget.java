package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.SPIChannel;

public class SPIChannelWidget extends ControlWidget {
	/**
	 * long 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox ss = new JComboBox();
	private ByteList dataStream = new ByteList();
	private JTextArea  rx = new JTextArea(5, 15);
	private JTextArea  tx = new JTextArea(5, 15);
	private JTextField addByte = new JTextField(3);
	private JButton send = new JButton("Send");
	private JButton clear = new JButton("Clear");
	private JButton addByteButton = new JButton("Add Byte");
	private SPIChannel spi;
	public SPIChannelWidget(ChannelManager c) {
		super(c);
		spi = new SPIChannel(getChannel().getDevice());
		
		ss.addItem(null);
		for(int i = 3; i < 24; i++) {
			ss.addItem(new Integer(i));
		}
		
		addByteButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				try {
					int toAdd = Integer.parseInt(addByte.getText());
					addByte.setText("");
					if (toAdd>255 || toAdd<0) {
						return;
					}
					dataStream.add(toAdd);
					tx.setText(dataStream.toString());
				}catch (NumberFormatException e) {
					
				}
				
			}	
		});
		clear.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				dataStream.clear();
				tx.setText("");
			}	
		});
		
		send.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				Integer s = (Integer)ss.getSelectedItem();
				if(s == null) {
					JOptionPane.showMessageDialog(null, "Please select  Chip Select pin", "Chip Select error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//Log.enableDebugPrint(true);
				byte [] back = spi.write(s.intValue(), dataStream.getBytes());
				//Log.enableDebugPrint(false);
				rx.setText(new ByteList(back).toString());
			}	
		});
		
		add(new JLabel("Tx: "));
		add(tx);
		add(send);
		add(clear,"wrap");
		add(new JLabel("Rx: "));
		add(rx,"wrap");
		add(new JLabel("Chip Select: "));
		add(ss);
		add(new JLabel("Add a byte: "));
		add(addByte);
		add(addByteButton);
		
	}
	
	public DyIOAbstractPeripheral getPerphera() {
		// TODO Auto-generated method stub
		return null;
	}

}
