package com.neuronrobotics.replicator.driver;
import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.replicator.driver.interpreter.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.vecmath.Point3f;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;

public class ServoStockGCodeParser {
	
	private GCodeInterpreter interp;
	NRPrinter device;
	
	// intrepretur status data
	private ArrayList<PrinterStatusListener> listeners = new ArrayList<PrinterStatusListener>();
	private int currentLine=0;
	private double currentTempreture = 0;
	private double extrusion=0;
	private TransformNR currentTransform= new TransformNR();
	public ServoStockGCodeParser(NRPrinter nrPrinter) {
		// TODO Auto-generated constructor stub
		this.device=nrPrinter;
	}

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
	
	private void firePrinterStatusUpdate(PrinterStatus status){
		currentLine=status.getPrintProgress();
		
		for(PrinterStatusListener l : listeners) {
			Log.warning("Firint print status event: "+status+" to "+l.getClass().getName());
			l.printStatus(status);
		}
	}
	
	public void firePrinterStatusUpdate(PrinterState state) {
		// TODO Auto-generated method stub
		firePrinterStatusUpdate(new PrinterStatus(currentTransform,extrusion,currentTempreture,currentLine,state));

	}

	void addHandlers(GCodeInterpreter interp) {
		
		interp.setErrorHandler(new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				firePrinterStatusUpdate(new PrinterStatus(currentTransform,
						extrusion,
						currentTempreture,
						(int)next.getWord('P'),PrinterState.ERROR,next+" unhandled"));
			}
		});
		// Temperature control
		interp.addMHandler(104, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				currentTempreture=next.getWord('S');
				device.setExtrusionTempreture(new  double[]{currentTempreture});
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		// TODO this code should wait until up to tempreture
		interp.addMHandler(109, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				double d[]=new double[1];
				d[0]=next.getWord('S');
				device.setExtrusionTempreture(d);
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});

		interp.setGHandler(0, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				currentTransform=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());
				extrusion = next.getWord('A');
				device.setDesiredPrintLocetion(currentTransform, extrusion, 0);// zero seconds is a rapid
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		interp.setGHandler(28, new CodeHandler() {
			//Move to origin
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				currentTransform=new TransformNR(0,0,0,new RotationNR());
				device.setDesiredPrintLocetion(currentTransform, extrusion, 0);// zero seconds is a rapid
				currentLine = (int)next.getWord('P');
				firePrinterStatusUpdate(PrinterState.PRINTING);
			}
		});
		interp.setGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				currentTransform=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());
				TransformNR prevT=new TransformNR(prev.getWord('X'),prev.getWord('Y'),prev.getWord('Z'),new RotationNR());
				double seconds=(currentTransform.getOffsetVectorMagnitude(prevT)/next.getWord('F'))*60.0;
				extrusion =next.getWord('A');
				while(device!=null && device.getNumberOfSpacesInBuffer()==0) {
					Thread.sleep(500);//Wait for at least 2 spaces in the buffer
					Log.debug("Waiting for space..." +device.getNumberOfSpacesInBuffer());
				}
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

	public boolean cancel() {
		if(interp!=null) {
			return interp.cancel();
		}
		return false;
	}

	public void addPrinterStatusListener(PrinterStatusListener l) {
		if(!listeners.contains(l))
			listeners.add(l);
	}

	public void removePrinterStatusListener(PrinterStatusListener l) {
		if(listeners.contains(l))
			listeners.remove(l);
	}

	public boolean isReady() {
		// TODO Auto-generated method stub
//		return false;
		return true;
	}


	
}
