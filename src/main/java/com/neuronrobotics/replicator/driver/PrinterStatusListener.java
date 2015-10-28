package com.neuronrobotics.replicator.driver;



// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving printerStatus events.
 * The class that is interested in processing a printerStatus
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addPrinterStatusListener  method. When
 * the printerStatus event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface PrinterStatusListener {
	
	/**
	 * Slice status.
	 *
	 * @param ssd the ssd
	 */
	public void sliceStatus(SliceStatusData ssd);
	
	/**
	 * Prints the status.
	 *
	 * @param psl the psl
	 */
	public void printStatus(PrinterStatus psl);
	
}
