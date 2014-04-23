package com.neuronrobotics.replicator.driver;
import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.replicator.driver.interpreter.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.vecmath.Point3f;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class GCodeParser {
	private ArrayList<PrinterStatusListener> listeners = new ArrayList<PrinterStatusListener>();
	private GenericKinematicsGCodeInterpreter interp;
	NRPrinter device;

	public GCodeParser(NRPrinter nrPrinter) {
		// TODO Auto-generated constructor stub
		this.device=nrPrinter;
	}

	public boolean print(InputStream gcode) {
		//this should be a thread that takes the gcode and sends it to the printer

		interp=new GenericKinematicsGCodeInterpreter(device); // Could reuse.
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
				TransformNR t=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),1,0,0,0);
				TransformNR prevT=new TransformNR(prev.getWord('X'),prev.getWord('Y'),prev.getWord('Z'),1,0,0,0);
				double seconds=(t.getOffsetVectorMagnitude(prevT));
				device.setDesiredPrintLocetion(t, next.getWord('A'), seconds);
			}
		});
		interp.setGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				TransformNR t=new TransformNR(next.getWord('X'),next.getWord('Y'),next.getWord('Z'),1,0,0,0);
				TransformNR prevT=new TransformNR(prev.getWord('X'),prev.getWord('Y'),prev.getWord('Z'),1,0,0,0);
				double seconds=(t.getOffsetVectorMagnitude(prevT)/next.getWord('F'))*60.0;
				while(device!=null && device.getNumberOfSpacesInBuffer()<2) Thread.sleep(100);//Wait for at least 2 spaces in the buffer
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
		
		// Add the 
		/*interp.addGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				// uh... set the new setpoint.
				device.setExtrusionPoint((int)next.getWord('T'), next.getWord('A'));
			}
		});*/
		// Linearize.
		/*interp.setGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				//Bah. This will be better in the firmware.
				//This is the /WRONG/ but straightforward way to do this.
				double a=next.getWord('A')-prev.getWord('A');
				double x=next.getWord('X')-prev.getWord('X');
				double y=next.getWord('Y')-prev.getWord('Y');
				double z=next.getWord('Z')-prev.getWord('Z');
				double overallLength=Math.sqrt(x*x+y*y+z*z);
				double ua=a/overallLength;
				double ux=x/overallLength;
				double uy=y/overallLength;
				double uz=z/overallLength;
				GCodeLineData psub=prev;
				for(double n=0;n<(overallLength-0.1);n+=0.1) {
					GCodeLineData sub=new GCodeLineData(next);
					sub.storeWord('A',prev.getWord('A')+ua*n);
					sub.storeWord('X',prev.getWord('X')+ux*n);
					sub.storeWord('Y',prev.getWord('Y')+uy*n);
					sub.storeWord('Z',prev.getWord('Z')+uz*n);
					callSubMethods(psub, sub);
					psub=sub;
				}
				callSubMethods(psub, next);
			}
		});*/
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
