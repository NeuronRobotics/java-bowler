package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.DyIORegestry;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIOPowerEvent;
import com.neuronrobotics.sdk.dyio.IDyIOEvent;
import com.neuronrobotics.sdk.dyio.IDyIOEventListener;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;

public class DyPIDControlWidget extends JPanel implements IDyIOEventListener{
	private JButton  DypidSet = new JButton("Set DyIO PID");
	private JComboBox inChan = new JComboBox();
	private JComboBox outChan = new JComboBox();
	private JComboBox inMode = new JComboBox();
	private JComboBox outMode = new JComboBox();
	/**
	 * long 
	 */
	private static final long serialVersionUID = 1L;
	//DyIO dyio;
	PIDControlWidget widgit;
	public DyPIDControlWidget(PIDControlWidget widg){
		setLayout(new MigLayout());
		widgit=widg;
		DyIORegestry.get().addDyIOEventListener(this);
		initDyPID();

		setOpaque(false);
		add(new JLabel("Input"));
		add(inChan,"wrap");
		add(new JLabel("Mode"));
		add(inMode,"wrap");
		add(new JLabel("Output"));
		add(outChan,"wrap");
		add(new JLabel("Mode"));
		add(outMode,"wrap");
		add(DypidSet, "wrap");
		
		populateDyPID();
	}
	private void populateDyPID() {
		DyPIDConfiguration conf = DyIORegestry.get().getDyPIDConfiguration(widgit.getGroup());
		
		for(int i=0;i<inChan.getItemCount();i++){
			Integer selected = (Integer)( inChan.getItemAt(i));
			if(selected != null){
				if(selected.intValue() == conf.getInputChannel()){
					inChan.setSelectedItem(inChan.getItemAt(i));
				}
			}
		}

		for(int i=0;i<outChan.getItemCount();i++){
			Integer selected = (Integer) outChan.getItemAt(i);
			if(selected != null){
				if(selected.intValue() == conf.getOutputChannel()){
					outChan.setSelectedItem(outChan.getItemAt(i));
				}
			}
		}
		
		if(conf.getOutputChannel() != conf.getInputChannel()){
			for(int i=0;i<inMode.getItemCount();i++){
				DyIOChannelMode selected = (DyIOChannelMode)(inMode.getItemAt(i));
				if(selected != null){
					if(selected == conf.getInputMode()){
						inMode.setSelectedItem(inMode.getItemAt(i));
					}
				}
			}
			for(int i=0;i<outMode.getItemCount();i++){
				DyIOChannelMode selected = (DyIOChannelMode)(outMode.getItemAt(i));
				if(selected != null){
					if(selected == conf.getOutputMode()){
						outMode.setSelectedItem(outMode.getItemAt(i));
					}
				}
			}
			widgit.pidSet.setEnabled(true);
			if(conf.getInputMode() == DyIOChannelMode.ANALOG_IN){
				widgit.setSetpoint(512);
			}
		}
		
	}
	private void updateInChan(){
		inMode.removeAllItems();
		if(inChan.getSelectedItem()==null)
			return;
		//System.out.println("Input channel set to "+inChan.getSelectedItem() );
		int chan = Integer.parseInt( inChan.getSelectedItem().toString());
		Collection<DyIOChannelMode> m = getAvailableInputModes(DyIORegestry.get().getChannel( chan ).getAvailableModes());
		for(DyIOChannelMode mode :m) {
			inMode.addItem(mode);
		}
		inMode.invalidate();
		inMode.repaint();
	}
	private void updateOutChan(){
		outMode.removeAllItems();
		if(outChan.getSelectedItem()==null)
			return;
		//System.out.println("Output channel set to "+outChan.getSelectedItem() );
		int chan = Integer.parseInt( outChan.getSelectedItem().toString());
		Collection<DyIOChannelMode> m = getAvailableOutputModes(DyIORegestry.get().getChannel(chan ).getAvailableModes());
		for(DyIOChannelMode mode : m) {
			outMode.addItem(mode);
		}
	}
	private void initDyPID() {
		for(int i=0;i<24;i++) {
			inChan.addItem(new Integer(i));
			outChan.addItem(new Integer(i));
		}
		inChan.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				updateInChan();
			}
		});
		outChan.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				updateOutChan();
			}
		});
		

		
		widgit.pidStop.setEnabled(false);
		widgit.pidSet.setEnabled(false);
		DypidSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if(inChan.getSelectedItem()!=null && outChan.getSelectedItem()!=null && inMode.getSelectedItem()!=null && outMode.getSelectedItem()!=null) {
					widgit.pidSet.setEnabled(true);
					DyPIDConfiguration config = new DyPIDConfiguration(widgit.getGroup(), Integer.parseInt( inChan.getSelectedItem().toString()), (DyIOChannelMode)inMode.getSelectedItem(), Integer.parseInt( outChan.getSelectedItem().toString()),(DyIOChannelMode) outMode.getSelectedItem());
					DyIORegestry.get().ConfigureDynamicPIDChannels(config);
					widgit.stopPID();
				}else {
					JOptionPane.showMessageDialog(null, "DyIO Channel/Modes are not all set", "DyPID ERROR", JOptionPane.ERROR_MESSAGE);
					widgit.stopPID();
					widgit.pidStop.setEnabled(false);
					widgit.pidSet.setEnabled(false);
				}
			}
		});
		
		
	}
	
	public void setInMode(JComboBox inMode) {
		this.inMode = inMode;
	}
	public JComboBox getInMode() {
		return inMode;
	}
	public void setOutMode(JComboBox outMode) {
		this.outMode = outMode;
	}
	public JComboBox getOutMode() {
		return outMode;
	}
	public Collection<DyIOChannelMode> getAvailableInputModes(Collection<DyIOChannelMode> m ){
		Collection<DyIOChannelMode> back = new ArrayList<DyIOChannelMode>();
		for(DyIOChannelMode mode: m) {
			switch(mode) {
			case ANALOG_IN:
			case COUNT_IN_INT:
				back.add(mode);
				break;
			default:
				break;
			}
		}
		return back;
	}
	public Collection<DyIOChannelMode> getAvailableOutputModes(Collection<DyIOChannelMode> m ){
		Collection<DyIOChannelMode> back = new ArrayList<DyIOChannelMode>();
		for(DyIOChannelMode mode: m) {
			switch(mode) {
			case SERVO_OUT:
			case DC_MOTOR_DIR:
				back.add(mode);
				break;
			default:
				break;
			}
		}
		return back;
	}
	
	public void onDyIOEvent(IDyIOEvent e) {
		if(e.getClass() == DyIOPowerEvent.class){
			updateInChan();
			updateOutChan();
		}
		
	}
}
