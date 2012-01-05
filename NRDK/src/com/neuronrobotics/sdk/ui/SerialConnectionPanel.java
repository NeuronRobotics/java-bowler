/*******************************************************************************
 * Copyright 2010 Neuron Robotics, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.neuronrobotics.sdk.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MissingNativeLibraryException;
import com.neuronrobotics.sdk.serial.SerialConnection;

/**
 * 
 */
public class SerialConnectionPanel extends AbstractConnectionPanel {

	private static final long serialVersionUID = 1L;
	
	private JTextField baudrateTxt = new JTextField(8);
	private JComboBox connectionCbo = null;
	private JButton refresh;
	
	private SerialConnection connection = null;
	
	/**
	 * 
	 */
	public SerialConnectionPanel() {
		super("Serial", ConnectionImageIconFactory.getIcon("images/usb-icon.png"));
		
		baudrateTxt.setText("115200");

		connectionCbo = new JComboBox();
		connectionCbo.setEditable(true);
		
		refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		
		setLayout(new MigLayout("",	// Layout Constraints
				                "[right][left]", // Column constraints with default align
				                "[center][center]"	// Row constraints with default align
				               ));

		add(new JLabel("Connection:"), "cell 0 0");
		add(connectionCbo);
		add(refresh);
		
		add(new JLabel("Baudrate:"), "cell 0 1");
		add(baudrateTxt, "cell 1 1");
		
		refresh();
	}

	
	public BowlerAbstractConnection getConnection() {
		try {
			int baud = Integer.parseInt(baudrateTxt.getText());
			if(baud < 0) {
				throw new NumberFormatException();
			}
			String port =connectionCbo.getSelectedItem().toString();
			connection = new SerialConnection(port, baud);
			Log.info("Using port:"+port+"\n");
		} catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid baudrate given. Please review the list of valid baudrates.", "Invalid Baudrate", JOptionPane.ERROR_MESSAGE);
		} catch(Exception e) {
		} finally {
			setVisible(false);
		}
		return connection;
	}

	
	public void refresh() {		
		connectionCbo.removeAllItems();
		String m = "NRSDK not installed properly, native library not found\n\n" +
		"librxtxSerial.so       in Linux\n" +
		"librxtxSerial.jnilib   in OSX\n" +
		"rxtxSerial.dll           in Windows\n\n"+
		"This must be in your JVM or system library path. See:\n"+
		"http://neuronrobotics.com/wiki/Installing_The_Native_Serial_Library";
		try {
			List<String> prts= SerialConnection.getAvailableSerialPorts();
 			for(int i=0;i<prts.size();i++) {
 				String s = prts.get(i);
 				if(s.contains("DyIO")||s.contains("Bootloader"))
 					connectionCbo.addItem(prts.remove(i));
			}
 			for(String s:prts){
 				if(!(s.contains("ttyS")  || s.equals("COM1") || s.equals("COM2") || s.contains("ttyACM")))
 					connectionCbo.addItem(s);
 				else{
 					// TODO maybe add the others if you can change the color?
 				}
 			}
 			if(connectionCbo.getItemCount()==0){
 				connectionCbo.addItem(null);
 				for(String s:prts){
 	 				if((s.contains("ttyS")  || s.equals("COM1") || s.equals("COM2") || s.contains("ttyACM")))
 	 					connectionCbo.addItem(s);
 	 			}
 			}
		} catch(MissingNativeLibraryException e) {
			JOptionPane.showMessageDialog(this, m,"NRSDK not installed properly", JOptionPane.ERROR_MESSAGE);
			throw new MissingNativeLibraryException(m);
		}catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, m,"NRSDK not installed properly", JOptionPane.ERROR_MESSAGE);
			throw new MissingNativeLibraryException(m);		
		}catch (Error e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, m,"NRSDK not installed properly", JOptionPane.ERROR_MESSAGE);
			throw new MissingNativeLibraryException(m);		
		}
	}
}
