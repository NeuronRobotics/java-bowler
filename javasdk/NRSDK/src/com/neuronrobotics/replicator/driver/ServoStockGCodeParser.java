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
	private ArrayList<PrinterStatusListener> listeners = new ArrayList<PrinterStatusListener>();
	private GCodeInterpreter interp;
	NRPrinter device;

	public ServoStockGCodeParser(NRPrinter nrPrinter) {
		// TODO Auto-generated constructor stub
		this.device=nrPrinter;
	}

	public boolean print(InputStream gcode) throws Exception {
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

	void addHandlers(GCodeInterpreter interp) {
		// Temperature control
		interp.addMHandler(104, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				double d[]=new double[1];
				d[0]=next.getWord('S');
				device.setExtrusionTempreture(d);
			}
		});
		// TODO this code should wait until up to tempreture
		interp.addMHandler(109, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				double d[]=new double[1];
				d[0]=next.getWord('S');
				device.setExtrusionTempreture(d);
			}
		});
		interp.addMHandler(73, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				for(PrinterStatusListener l : listeners) {
					l.printStatus(new PrinterStatus(new TransformNR(),0,0,(int)next.getWord('P'),PrinterState.PRINTING));
				}
			}
		});
		
		// sets extruder to absolute mode
		interp.addMHandler(82, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				//TODo unimplemented, but the default
			}
		});
		interp.addMHandler(107, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				// no fans
			}
		});
		
		interp.addGHandler(6, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.setGHandler(0, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				TransformNR t=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());

				device.setDesiredPrintLocetion(t, next.getWord('A'), 0);// zero seconds is a rapid
			}
		});
		interp.setGHandler(28, new CodeHandler() {
			//Move to origin
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				TransformNR t=new TransformNR(0,0,0,new RotationNR());
				device.setDesiredPrintLocetion(t, next.getWord('A'), 0);// zero seconds is a rapid
			}
		});
		interp.setGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				TransformNR t=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());
				TransformNR prevT=new TransformNR(prev.getWord('X'),prev.getWord('Y'),prev.getWord('Z'),new RotationNR());
				double seconds=(t.getOffsetVectorMagnitude(prevT)/next.getWord('F'))*60.0;
				
				while(device!=null && device.getNumberOfSpacesInBuffer()==0) {
					Thread.sleep(500);//Wait for at least 2 spaces in the buffer
					Log.debug("Waiting for space..." +device.getNumberOfSpacesInBuffer());
				}
				int iter=0;
				while(iter++<1000) {
					try {
						device.setDesiredPrintLocetion(t, next.getWord('A'), seconds);
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
	
	private void fireStatus(PrinterStatus p) {
		for(PrinterStatusListener l: listeners) {
			l.printStatus(p);
		}
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
