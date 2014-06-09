package com.neuronrobotics.replicator.driver;

import java.io.InputStream;
import java.io.OutputStream;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.CartesianNamespacePidKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ILinkListener;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class NRPrinter extends CartesianNamespacePidKinematics{
	private GCodeParser parser;
	private StlSlicer slicer;
	private DeltaForgeDevice deltaDevice;
	//Configuration hard coded
	private  double extrusionCachedValue = 0;
	private double currentTemp =0;
	//static InputStream s = XmlFactory.getDefaultConfigurationStream("DeltaPrototype.xml");
//	private AbstractLink extruder;
//	private AbstractLink hotEnd;
	private double temp = 0;

	
	public NRPrinter(DeltaForgeDevice d) {
		super(d,d);
		this.setDeltaDevice(d);
		
//		extruder = getFactory().getLink("Extruder");
//		hotEnd = getFactory().getLink("Heater");
		setTempreture(getTempreture());
		getFactory().addLinkListener(new ILinkListener() {
			@Override
			public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue) {
//				if(source == hotEnd) {
//					setTempreture(engineeringUnitsValue);
//				}
			}
			
			@Override
			public void onLinkLimit(AbstractLink source, PIDLimitEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//parse out the extruder configs
		//parse delta robot configs
		
		setExtrusionTempreture(new double [] {getTempreture()});

		setParser(new GCodeParser(this));
//		setSlicer(new StlSlicer(getDevice().getMaterialData()));
		setSlicer(new MiracleGrue(getMaterialData()));
		
	}

	/**
	 * 
	 * @param stl the input stream
	 * @param gcode the gcode to be written to
	 * @return
	 */
	public boolean slice(InputStream stl,OutputStream gcode) {
		return getSlicer().slice(stl, gcode);
	}
	
	/**
	 * 
	 * @param gcode the gcode to be sent to the printer
	 * @return
	 */
	public boolean print(InputStream gcode) {
		System.out.println("Printing now.");
		cancelPrint();
		//ThreadUtil.wait(5000);
		long start = System.currentTimeMillis();
		boolean b = getParser().print(gcode);
		System.out.println("Gcode loaded, waiting for printer to finish");
		while(deltaDevice.getNumberOfPacketsWaiting()>0){
			ThreadUtil.wait(5000);
			System.out.println(deltaDevice.getNumberOfPacketsWaiting()+" remaining");
		}
		System.out.println("Print Done, took "+((((double)(System.currentTimeMillis()-start))/1000.0)/60.0)+" minutes");
		
		cancelPrint();
		return b;
	}
	
	public boolean cancelPrint() {
		cancelRunningPrint();
		return getParser().cancel();
	}
	public boolean isReady() {
		// TODO Auto-generated method stub
		return getParser().isReady();
	}
	
	public void addPrinterStatusListener(PrinterStatusListener l) {
		getParser().addPrinterStatusListener(l);
		getSlicer().addPrinterStatusListener(l);
	}
	public void removePrinterStatusListener(PrinterStatusListener l) {
		getParser().removePrinterStatusListener(l);
		getSlicer().removePrinterStatusListener(l);
	}
	private void setSlicer(StlSlicer slicer) {
		this.slicer = slicer;
	}
	public StlSlicer getSlicer() {
		return slicer;
	}
	private void setParser(GCodeParser parser) {
		this.parser = parser;
	}
	public GCodeParser getParser() {
		return parser;
	}

	public DeltaForgeDevice getDeltaDevice() {
		return deltaDevice;
	}

	public void setDeltaDevice(DeltaForgeDevice d) {
		this.deltaDevice = d;
	}
	
	private double getTempreture() {
		return temp;
	}
	public void setTempreture(double temp) {
		this.temp = temp;
	}
	
	public MaterialData getMaterialData() {
		return new MiracleGrueMaterialData();
	}
	
	public void setExtrusionTempreture(double [] extTemp) {
		if(extTemp[0] == currentTemp) {
			System.out.println("Printer at tempreture "+currentTemp+" C");
			return;
		}else
			currentTemp=extTemp[0];
//		setTempreture(hotEnd.getCurrentEngineeringUnits());
//		hotEnd.setTargetEngineeringUnits(extTemp[0]);
//		hotEnd.flush(0);
		getTempreture();
		System.out.print("\r\nWaiting for Printer to come up to tempreture "+currentTemp+" C \n");
		Log.enableSystemPrint(false);
		int iter=0;
		while(temp>(extTemp[0]+10) || temp< (extTemp[0]-10)) {
			getTempreture();
			System.out.print(".");
			ThreadUtil.wait(100);
			iter++;
			if(iter==50) {
				System.out.print("\r\n "+temp+" C");
				iter=0;
			}
		}
		Log.enableSystemPrint(true);
	}
	public void setBedTempreture(double bedTemp) {
		
	}
	public int setDesiredPrintLocetion(TransformNR taskSpaceTransform,double extrusionLegnth, double seconds) throws Exception{
		System.out.println("Telling printer to go to extrusion len "+extrusionLegnth);
		return getDeltaDevice().sendLinearSection(taskSpaceTransform, extrusionLegnth, (int) (seconds*1000));
	}
	
	public double getExtrusionCachedValue() {
		return extrusionCachedValue;
	}

	public void setExtrusionCachedValue(double extrusionCachedValue) {
		this.extrusionCachedValue = extrusionCachedValue;
	}
	
	public void setExtrusionPoint(int materialNumber, double setPoint) {
		//TODO another method to set material
//		extruder.setTargetEngineeringUnits(setPoint);
		setExtrusionCachedValue(setPoint);
	}
	
	public int getNumberOfSpacesInBuffer() {
		return getDeltaDevice().getNumberOfSpacesInBuffer();
	}
	
	public void cancelRunningPrint() {
		
		getDeltaDevice().cancelRunningPrint();
		
	}
	
}
