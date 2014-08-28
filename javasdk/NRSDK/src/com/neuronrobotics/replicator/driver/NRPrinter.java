package com.neuronrobotics.replicator.driver;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.CartesianNamespacePidKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ILinkListener;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class NRPrinter extends CartesianNamespacePidKinematics implements PrinterStatusListener{
	private ServoStockGCodeParser parser;
	private StlSlicer slicer;
	private BowlerBoardDevice deltaDevice;
	//Configuration hard coded
	private  double extrusionCachedValue = 0;
	private double currentTemp =0;
	//static InputStream s = XmlFactory.getDefaultConfigurationStream("DeltaPrototype.xml");
//	private AbstractLink extruder;
//	private AbstractLink hotEnd;
	private double temp = 0;

	
	public NRPrinter(BowlerBoardDevice d) {
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

		setParser(new ServoStockGCodeParser(this));
		Slic3r.setExecutableLocation("/home/hephaestus/bin/Slic3r/bin/slic3r");
		setSlicer(new Slic3r(	.4, 
								new double[]{0,0},
								1.75,
								1,
								190,
								0,
								.3,
								3,
								true,
								1.1,
								60,// travilSpeed,
								20,// perimeterSpeed,
								40,//bridgeSpeed,
								20,//gapFillSpeed,
								60,//infillSpeed,
								60,//supportMaterialSpeed,
								
								100,//smallPerimeterSpeedPercent,
								70,//externalPerimeterSpeedPercent,
								100,//solidInfillSpeedPercent,
								80,//topSolidInfillSpeedPercent,
								100,//supportMaterialInterfaceSpeedPercent,
								30//firstLayerSpeedPercent
							));
		addPrinterStatusListener(this);

		
	}

	/**
	 * 
	 * @param stl the input stream
	 * @param gcode the gcode to be written to
	 * @return
	 */
	public boolean slice(File stl,File gcode) {
		return getSlicer().slice(stl, gcode);
	}
	
	/**
	 * 
	 * @param gcode the gcode to be sent to the printer
	 * @return
	 * @throws Exception 
	 */
	public boolean print(InputStream gcode) throws Exception {
		Log.debug("Printing now.");
		//cancelPrint();
		//ThreadUtil.wait(5000);
		long start = System.currentTimeMillis();
		boolean b = getParser().print(gcode);
		Log.debug("Gcode loaded, waiting for printer to finish");
		while(deltaDevice.getNumberOfPacketsWaiting()>0){
			ThreadUtil.wait(1000);
			Log.debug(deltaDevice.getNumberOfPacketsWaiting()+" remaining");
		}
		ThreadUtil.wait(5000);
		Log.debug("Print Done, took "+((((double)(System.currentTimeMillis()-start))/1000.0)/60.0)+" minutes");
		//cancelPrint();
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
		deltaDevice.addPrinterStatusListener(l);
	}
	public void removePrinterStatusListener(PrinterStatusListener l) {
		getParser().removePrinterStatusListener(l);
		getSlicer().removePrinterStatusListener(l);
		deltaDevice.removePrinterStatusListener(l);
	}
	private void setSlicer(StlSlicer slicer) {
		this.slicer = slicer;
	}
	public StlSlicer getSlicer() {
		return slicer;
	}
	private void setParser(ServoStockGCodeParser parser) {
		this.parser = parser;
	}
	public ServoStockGCodeParser getParser() {
		return parser;
	}

	public BowlerBoardDevice getDeltaDevice() {
		return deltaDevice;
	}

	public void setDeltaDevice(BowlerBoardDevice d) {
		this.deltaDevice = d;
		
	}

	private double getTempreture() {
		return temp;
	}
	public void setTempreture(double temp) {
		this.temp = temp;
	}
	
	
	public void setExtrusionTempreture(double [] extTemp) {
		if(extTemp[0] == currentTemp) {
			Log.debug("Printer at tempreture "+currentTemp+" C");
			return;
		}else
			currentTemp=extTemp[0];
//		setTempreture(hotEnd.getCurrentEngineeringUnits());
//		hotEnd.setTargetEngineeringUnits(extTemp[0]);
//		hotEnd.flush(0);
		getTempreture();
		//System.out.print("\r\nWaiting for Printer to come up to tempreture "+currentTemp+" C \n");
		Log.enableSystemPrint(false);
		int iter=0;
		while(temp>(extTemp[0]+10) || temp< (extTemp[0]-10)) {
			getTempreture();
			//System.out.print(".");
			ThreadUtil.wait(100);
			iter++;
			if(iter==50) {
				//System.out.print("\r\n "+temp+" C");
				iter=0;
			}
		}
		Log.enableSystemPrint(true);
	}
	public void setBedTempreture(double bedTemp) {
		
	}
	public int setDesiredPrintLocetion(TransformNR taskSpaceTransform,double extrusionLegnth, double seconds) throws Exception{
		Log.debug("Telling printer to go to extrusion len "+extrusionLegnth);
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

	@Override
	public void sliceStatus(SliceStatusData ssd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printStatus(PrinterStatus psl) {
		// TODO Auto-generated method stub
		firePoseTransform(forwardOffset(psl.getHeadLocation()));	
		
	}


	
}
