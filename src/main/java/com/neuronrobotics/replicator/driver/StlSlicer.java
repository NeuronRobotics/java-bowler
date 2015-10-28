package com.neuronrobotics.replicator.driver;

import java.io.File;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class StlSlicer.
 */
public class StlSlicer {
	
	/** The listeners. */
	private ArrayList<PrinterStatusListener> listeners = new ArrayList<PrinterStatusListener>();
	
	/**
	 * Instantiates a new stl slicer.
	 *
	 * @param materialData the material data
	 */
	public StlSlicer(MaterialData materialData) {
		//This is the stub class for the stl slicing system. 
	}

	/**
	 * Slice.
	 *
	 * @param stl the stl
	 * @param gcode the gcode
	 * @return true, if successful
	 */
	public boolean slice(File stl,File gcode) {
		
		return true;
	}
	
	/**
	 * Fire status.
	 *
	 * @param p the p
	 */
	protected void fireStatus(SliceStatusData p) {
		
		for(PrinterStatusListener l: listeners) {
			l.sliceStatus(p);
		}
	}

	/**
	 * Adds the printer status listener.
	 *
	 * @param l the l
	 */
	public void addPrinterStatusListener(PrinterStatusListener l) {
		if(!listeners.contains(l))
			listeners.add(l);
	}

	/**
	 * Removes the printer status listener.
	 *
	 * @param l the l
	 */
	public void removePrinterStatusListener(PrinterStatusListener l) {
		if(listeners.contains(l))
			listeners.remove(l);
	}
}
