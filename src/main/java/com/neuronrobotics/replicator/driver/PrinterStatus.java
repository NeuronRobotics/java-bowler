package com.neuronrobotics.replicator.driver;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


// TODO: Auto-generated Javadoc
/**
 * The Class PrinterStatus.
 */
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
	 * just that there are no new warnings at the end of the print.
	 */
	public enum PrinterState {
		
		/** The not ready. */
		NOT_READY, 
 /** The ready. */
 READY, 
 /** The printing. */
 PRINTING,
/** The moving. */
MOVING, 
 /** The error. */
 ERROR, 
 /** The warning printing. */
 WARNING_PRINTING,
/** The warning done. */
WARNING_DONE, 
 /** The success. */
 SUCCESS;
		
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
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
			case MOVING:
				return "MOVING";
			default:
				break;		
			}
			return "";
		}
	}
	
	/** The printer state. */
	private PrinterState thePrinterState;
	
	/** The head location. */
	private TransformNR headLocation;
	
	/** The message. */
	private String message;
	
	/** The print progress. */
	private int printProgress;
	
	/** The extrusion. */
	private double extrusion;
	
	/** The tempreture. */
	private double tempreture;
		
	/**
	 * Instantiates a new printer status.
	 *
	 * @param headLocation the head location
	 * @param extrusion the extrusion
	 * @param temp the temp
	 * @param printProgress the print progress
	 * @param thePrinterState the the printer state
	 */
	public PrinterStatus(TransformNR headLocation, double extrusion, double temp,int printProgress, PrinterState thePrinterState){
		this.headLocation = headLocation;
		this.printProgress = printProgress;
		this.thePrinterState = thePrinterState;
		this.message = "";
		this.setExtrusion(extrusion);
		this.setTempreture(temp);
	}
	
	/**
	 * Instantiates a new printer status.
	 *
	 * @param headLocation the head location
	 * @param extrusion the extrusion
	 * @param temp the temp
	 * @param printProgress the print progress
	 * @param thePrinterState the the printer state
	 * @param stateMessage the state message
	 */
	public PrinterStatus(TransformNR headLocation,double extrusion, double temp, int printProgress, PrinterState thePrinterState, String stateMessage){
		this.headLocation = headLocation;
		this.printProgress = printProgress;
		this.thePrinterState = thePrinterState;
		this.message = stateMessage;
		this.setExtrusion(extrusion);
		this.setTempreture(temp);
	}
	
	/**
	 * Gets the driver state.
	 *
	 * @return the driver state
	 */
	public PrinterState getDriverState(){
		return thePrinterState;
	}
	
	/**
	 * Gets the head location.
	 *
	 * @return the head location
	 */
	public TransformNR getHeadLocation(){
		return headLocation;
	}
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage(){
		return message;
	}
	
	/**
	 * Gets the prints the progress.
	 *
	 * @return the prints the progress
	 */
	public int getPrintProgress(){
		return printProgress;
	}

	/**
	 * Gets the extrusion.
	 *
	 * @return the extrusion
	 */
	public double getExtrusion() {
		return extrusion;
	}

	/**
	 * Sets the extrusion.
	 *
	 * @param extrusion the new extrusion
	 */
	public void setExtrusion(double extrusion) {
		this.extrusion = extrusion;
	}

	/**
	 * Gets the tempreture.
	 *
	 * @return the tempreture
	 */
	public double getTempreture() {
		return tempreture;
	}

	/**
	 * Sets the tempreture.
	 *
	 * @param tempreture the new tempreture
	 */
	public void setTempreture(double tempreture) {
		this.tempreture = tempreture;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s="Print Status:"+thePrinterState+" X="+headLocation.getX()+
				" Y="+headLocation.getY()+
				" Z="+headLocation.getZ()				
				+" extrusion="+extrusion+" tempreture="+tempreture+" "+message;
		
		return s;
	}
	
}
