package com.neuronrobotics.replicator.driver;

import java.util.ArrayList;

import javax.vecmath.*;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public class PrinterStatus {
	
	/**
	 * This enum should be used to indicate current printer status
	 * NOT_READY indicates that the printer is not ready to print
	 * READY indicates the printer is idle and ready to receive a print job
	 * PRINTING indicates the printer is currently working
	 * ERROR indicates that an error has occurred and the print has been terminated
	 * WARNING_PRINTING indicates that something may be wrong but the print has not been terminated
	 * WARNING_DONE indicates that something may be wrong and the print has finished
	 * SUCCESS indicates the print has finished with no new warnings
	 * SUCCESS should not be taken to mean there were no warnings during the print
	 * just that there are no new warnings at the end of the print
	 */
	public enum PrinterState {
		NOT_READY, READY, PRINTING, ERROR, WARNING_PRINTING,WARNING_DONE, SUCCESS;
		@Override 
		public String toString(){
			switch(this){
			case ERROR:
				return "ERROR";
			case NOT_READY:
				return "NOT_READY";
			case PRINTING:
				return "PRINTING";
			case READY:
				return "READY";
			case SUCCESS:
				return "SUCCESS";
			case WARNING_DONE:
				return "WARNING_DONE";
			case WARNING_PRINTING:
				return "WARNING_PRINTING";		
			}
			return "";
		}
	}
	
	private PrinterState thePrinterState;
	
	private TransformNR headLocation;
	
	private String message;
	
	private int printProgress;
	
	private double extrusion;
	private double tempreture;
		
	public PrinterStatus(TransformNR headLocation, double extrusion, double temp,int printProgress, PrinterState thePrinterState){
		this.headLocation = headLocation;
		this.printProgress = printProgress;
		this.thePrinterState = thePrinterState;
		this.message = "";
		this.setExtrusion(extrusion);
		this.setTempreture(temp);
	}
	
	public PrinterStatus(TransformNR headLocation,double extrusion, double temp, int printProgress, PrinterState thePrinterState, String stateMessage){
		this.headLocation = headLocation;
		this.printProgress = printProgress;
		this.thePrinterState = thePrinterState;
		this.message = stateMessage;
		this.setExtrusion(extrusion);
		this.setTempreture(temp);
	}
	
	public PrinterState getDriverState(){
		return thePrinterState;
	}
	
	public TransformNR getHeadLocation(){
		return headLocation;
	}
	
	public String getMessage(){
		return message;
	}
	
	public int getPrintProgress(){
		return printProgress;
	}

	public double getExtrusion() {
		return extrusion;
	}

	public void setExtrusion(double extrusion) {
		this.extrusion = extrusion;
	}

	public double getTempreture() {
		return tempreture;
	}

	public void setTempreture(double tempreture) {
		this.tempreture = tempreture;
	}
	
	@Override
	public String toString(){
		String s="Print Status:"+thePrinterState+"\nLocation="+headLocation+"\nextrusion="+extrusion+"\ntempreture="+tempreture+"\n"+message;
		
		return s;
	}
	
}
