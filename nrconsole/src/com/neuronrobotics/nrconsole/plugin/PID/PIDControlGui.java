package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.nrconsole.plugin.DyIO.DyIORegestry;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOPowerEvent;
import com.neuronrobotics.sdk.dyio.IDyIOEvent;
import com.neuronrobotics.sdk.dyio.IDyIOEventListener;

import com.neuronrobotics.sdk.pid.IPIDControl;

public class PIDControlGui extends JPanel {
	private static final long serialVersionUID = 1L;
	//private DyIO dyio=null;
	private IPIDControl pid=null;
	private boolean DyPID=false;
	
	private ArrayList<PIDControlWidget> widgits = new ArrayList<PIDControlWidget> ();
	private JComboBox groupSelector = new JComboBox();
	private JButton stopAll = new JButton("Stop All PID");
	private PIDControlWidget selected;
	
	public PIDControlGui() {
		Log.info("Connecteing DyPID panel");
		setPidDevice(DyIORegestry.get());
		setDyPID(true);
		//dyio.addDyIOEventListener(this);
		init();
	}
	
	public PIDControlGui(IPIDControl d) {
		Log.info("Connecteing PID panel");
		setPidDevice(d);
		setDyPID(false);
		init();
	}
	
	private void init() {
		
		setName("P.I.D.");
		Log.info("Begining PID Control Gui");
		setLayout(new MigLayout());
		
		int [] initVals = {};
		try{
			initVals = getPidDevice().GetAllPIDPosition();
			
		}catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "DyIO Firmware is out of date", "DyPID ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		for(int i=0;i<initVals.length;i++) {
			try{
				widgits.add(new PIDControlWidget(i, initVals[i], this));
			}catch (Exception e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to create a PID widget", "DyPID ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			groupSelector.addItem(widgits.get(i));
		}
		groupSelector.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				remove(selected);
				selected =(PIDControlWidget) groupSelector.getSelectedItem();
				selected.setVisible(true);
				add(selected);
				setVisible(true);
				repaint();
				selected.repaint();
				selected.revalidate();
				revalidate();
			}
		});
		stopAll.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				for(PIDControlWidget w:widgits) {
					w.stopPID();
				}
			}
		});
		JPanel groups = new JPanel(new MigLayout());
		groups.add(groupSelector,"wrap");
		groups.add(stopAll,"wrap");
		selected = widgits.get(0);
		add(groups);
		add(selected);
		Log.info("Started PID Control Gui");
	}
	
	private void setDyPID(boolean hadDyPID) {
		this.DyPID = hadDyPID;
	}
	
	public boolean isDyPID() {
		return DyPID;
	}
	
	public DyIO getDyio() {
		return DyIORegestry.get();
	}
	
	public void setPidDevice(IPIDControl pid) {
		this.pid = pid;
	}
	
	public IPIDControl getPidDevice() {
		return pid;
	}


}
