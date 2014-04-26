package com.neuronrobotics.replicator.driver;
import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.replicator.driver.interpreter.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.vecmath.Point3f;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class GCodeParser {
	private ArrayList<PrinterStatusListener> listeners = new ArrayList<PrinterStatusListener>();
	private GCodeInterpreter interp;
	NRPrinter device;

	public GCodeParser(NRPrinter nrPrinter) {
		// TODO Auto-generated constructor stub
		this.device=nrPrinter;
	}

	public boolean print(InputStream gcode) {
		//this should be a thread that takes the gcode and sends it to the printer

		interp=new GCodeInterpreter(); // Could reuse.
		addHandlers(interp);
		System.out.println("Reached print.");
		try {
			interp.tryInterpretStream(gcode);
			System.out.println("End of print.");
			return true;
		} catch (Exception e) { 
			// um... this is bad. Ideally, the kinematics methods probably shouldn't through Exception, but we'll just catch it here for now.
			System.err.println(e);
			e.printStackTrace();
			System.out.println("Abnormal end of print");
			return false;
		}
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
		interp.addMHandler(73, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				for(PrinterStatusListener l : listeners) {
					l.printStatus(new PrinterStatus(new Point3f(),(int)next.getWord('P'),PrinterState.PRINTING));
				}
			}
		});
		interp.addGHandler(6, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.setGHandler(0, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				TransformNR t=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());
				TransformNR prevT=new TransformNR(prev.getWord('X'),prev.getWord('Y'),prev.getWord('Z'),new RotationNR());

				device.setDesiredPrintLocetion(t, next.getWord('A'), 0);// zero seconds is a rapid
			}
		});
		interp.setGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				TransformNR t=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),new RotationNR());
				TransformNR prevT=new TransformNR(prev.getWord('X'),prev.getWord('Y'),prev.getWord('Z'),new RotationNR());
				double seconds=(t.getOffsetVectorMagnitude(prevT)/next.getWord('F'))*60.0;
				while(device!=null && device.getNumberOfSpacesInBuffer()<2) 
					Thread.sleep(100);//Wait for at least 2 spaces in the buffer
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
