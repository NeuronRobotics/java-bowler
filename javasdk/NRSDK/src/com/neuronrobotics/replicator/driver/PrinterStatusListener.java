package com.neuronrobotics.replicator.driver;



public interface PrinterStatusListener {
	
	public void sliceStatus(SliceStatusData ssd);
	
	public void printStatus(PrinterStatus psl);
	
}
