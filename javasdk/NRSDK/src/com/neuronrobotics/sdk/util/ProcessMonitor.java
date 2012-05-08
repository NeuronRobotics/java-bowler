package com.neuronrobotics.sdk.util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class ProcessMonitor implements ActionListener {
	private Timer timer = new Timer(100, this);
	private IMonitorable process;
	private ArrayList<IProgressMonitorListener> listeners = new ArrayList<IProgressMonitorListener>();
	
	public ProcessMonitor(IMonitorable process) {
		this.process = process;
	}

	public void addProcessMonitorListener(IProgressMonitorListener listener) {
		listeners.add(listener);
	}
	
	public void start() {
		timer.start();
	}
	
	
	public void actionPerformed(ActionEvent arg0) {
		double value = process.getPercentage();
		boolean isComplete = process.isComplete();
		
		if(isComplete) {
			timer.stop();
		}
		
		for(IProgressMonitorListener l : listeners) {
			l.onUpdate(value);
			if(isComplete) {
				l.onComplete();
			}
		}
	}
}