package com.neuronrobotics.sdk.util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

// TODO: Auto-generated Javadoc
/**
 * The Class ProcessMonitor.
 */
public class ProcessMonitor implements ActionListener {
	
	/** The timer. */
	private Timer timer = new Timer(100, this);
	
	/** The process. */
	private IMonitorable process;
	
	/** The listeners. */
	private ArrayList<IProgressMonitorListener> listeners = new ArrayList<IProgressMonitorListener>();
	
	/**
	 * Instantiates a new process monitor.
	 *
	 * @param process the process
	 */
	public ProcessMonitor(IMonitorable process) {
		this.process = process;
	}

	/**
	 * Adds the process monitor listener.
	 *
	 * @param listener the listener
	 */
	public void addProcessMonitorListener(IProgressMonitorListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Start.
	 */
	public void start() {
		timer.start();
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
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