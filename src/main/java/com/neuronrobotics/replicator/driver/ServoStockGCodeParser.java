package com.neuronrobotics.replicator.driver;
import java.io.InputStream;
import java.util.ArrayList;

import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.replicator.driver.interpreter.CodeHandler;
import com.neuronrobotics.replicator.driver.interpreter.GCodeInterpreter;
import com.neuronrobotics.replicator.driver.interpreter.GCodeLineData;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class ServoStockGCodeParser.
 */
public class ServoStockGCodeParser {
	
	/** The interp. */
	private GCodeInterpreter interp;
	
	/** The device. */
	NRPrinter device;
	
	/** The listeners. */
	// intrepretur status data
	private ArrayList<PrinterStatusListener> listeners = new ArrayList<PrinterStatusListener>();
	
	/** The current line. */
	private int currentLine=0;
	
	/** The current tempreture. */
	private double currentTempreture = 0;
	
	/** The extrusion. */
	private double extrusion=0;
	
	/** The current transform. */
	private TransformNR currentTransform= new TransformNR();
	
	/**
	 * Instantiates a new servo stock g code parser.
	 *
	 * @param nrPrinter the nr printer
	 */
	public ServoStockGCodeParser(NRPrinter nrPrinter) {
		// TODO Auto-generated constructor stub
		this.device=nrPrinter;
	}

	/**
	 * Prints the.
	 *
	 * @param gcode the gcode
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean print(InputStream gcode) throws Exception {
		currentLine=0;
		//this should be a thread that takes the gcode and sends it to the printer
		if(interp == null){
			interp=new GCodeInterpreter(); // Could reuse.
			addHandlers(interp);
		}
		Log.debug("Reached print.");
		
		interp.tryInterpretStream(gcode);
		Log.debug("End of print.");
		
		return true;

	}
	
	/**
	 * Fire printer status update.
	 *
	 * @param status the status
	 */
	private void firePrinterStatusUpdate(PrinterStatus status){
		currentLine=status.getPrintProgress();
		
		for(PrinterStatusListener l : listeners) {
			Log.info("Firing print status event: "+status+" to "+l.getClass().getName());
			l.printStatus(status);
		}
	}
	
	/**
	 * Fire printer status update.
	 *
	 * @param state the state
	 */
	public void firePrinterStatusUpdate(PrinterState state) {
		// TODO Auto-generated method stub
		firePrinterStatusUpdate(new PrinterStatus(currentTransform,extrusion,currentTempreture,currentLine,state));

	}

	/**
	 * Adds the handlers.
	 *
	 * @param interp the interp
	 */
	void addHandlers(GCodeInterpreter interp) {
		
		interp.setErrorHandler(new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				firePrinterStatusUpdate(new PrinterStatus(currentTransform,
						extrusion,
						currentTempreture,
						(int)next.getWord('P'),PrinterState.ERROR,next+" unhandled exception"));
			}
		});
		// Temperature control
		interp.addMHandler(104, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				waitForClearToPrint();
				currentTempreture=next.getWord('S');
				device.setExtrusionTempreture(currentTempreture);
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		// TODO this code should wait until up to tempreture
		interp.addMHandler(109, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				waitForClearToPrint();
				currentTempreture = next.getWord('S');
				device.setExtrusionTempreture(currentTempreture);
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});

		interp.setGHandler(0, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				waitForClearToPrint();
				currentTransform=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());
				extrusion = next.getWord('E');
				device.setDesiredPrintLocetion(currentTransform, extrusion, 0);// zero seconds is a rapid
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		interp.setGHandler(28, new CodeHandler() {
			//Move to origin
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				waitForClearToPrint();
				currentTransform=new TransformNR(0,0,0,new RotationNR());
				device.setDesiredPrintLocetion(currentTransform, extrusion, 0);// zero seconds is a rapid
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		interp.setGHandler(92, new CodeHandler() {
			//Move to origin
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				// clear the print queue before zeroing out extruder
				waitForEmptyPrintQueue();
				extrusion =next.getWord('E');
				device.zeroExtrusion(extrusion);
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		
		// set units to millimeters
		interp.setGHandler(21, new CodeHandler() {
			//Move to origin
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				waitForClearToPrint();
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		
		// use absolute coordinates
		interp.setGHandler(90, new CodeHandler() {
			//Move to origin
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				waitForClearToPrint();
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		interp.setGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				waitForClearToPrint();
				currentTransform=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());
				TransformNR prevT=new TransformNR(prev.getWord('X'),prev.getWord('Y'),prev.getWord('Z'),new RotationNR());
				double seconds=(currentTransform.getOffsetVectorMagnitude(prevT)/next.getWord('F'))*60.0;
				extrusion =next.getWord('E');
				int iter=0;
				while(iter++<1000) {
					try {
						device.setDesiredPrintLocetion(currentTransform, extrusion, seconds);
						currentLine = (int)next.getWord('P');
						firePrinterStatusUpdate(PrinterState.PRINTING);
						return;
					}catch (RuntimeException ex) {
						//keep trying
						Thread.sleep(100);
					}
				}
			}
		});
		
	}
	
	/**
	 * Wait for clear to print.
	 */
	private void waitForClearToPrint(){
		while(device!=null && device.getNumberOfSpacesInBuffer()==0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//Wait for at least 2 spaces in the buffer
			Log.info("Waiting for space..." +device.getNumberOfSpacesInBuffer());
		}
	}
	
	/**
	 * Wait for empty print queue.
	 */
	private void waitForEmptyPrintQueue(){
		while(device!=null && device.getNumberOfPacketsWaiting() != 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//Wait for at least 2 spaces in the buffer
			Log.info("Waiting for clear packet buffer..." +device.getNumberOfPacketsWaiting() );
		}
	}

	/**
	 * Cancel.
	 *
	 * @return true, if successful
	 */
	public boolean cancel() {
		if(interp!=null) {
			return interp.cancel();
		}
		return false;
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

	/**
	 * Checks if is ready.
	 *
	 * @return true, if is ready
	 */
	public boolean isReady() {
		// TODO Auto-generated method stub
//		return false;
		return true;
	}


	
}
