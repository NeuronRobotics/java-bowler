package com.neuronrobotics.replicator.driver;

import gnu.io.NativeResource;

import java.io.File;
import java.io.InputStream;

import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.CartesianNamespacePidKinematics;
import com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.ILinkListener;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;
import com.neuronrobotics.sdk.utils.NativeResourceException;

public class NRPrinter extends CartesianNamespacePidKinematics implements PrinterStatusListener{
	private ServoStockGCodeParser parser;
	private Slic3r slicer;
	private BowlerBoardDevice deltaDevice;
	//Configuration hard coded
	private  double extrusionCachedValue = 0;
	private double currentTemp =0;
	private AbstractLink extruder;
	private AbstractLink hotEnd;
	private double temp = 0;

	//private boolean printRunning=false;
	
	public NRPrinter(BowlerBoardDevice d) {
		super(d,d);
		

		
		this.setDeltaDevice(d);
		
		extruder = getFactory().getLink("Extruder");
		hotEnd = getFactory().getLink("Heater");
		setTempreture(getTempreture());
		getFactory().addLinkListener(new ILinkListener() {
			@Override
			public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue) {
				if(source == hotEnd) {
					setTempreture(engineeringUnitsValue);
				}
				//Log.info("Link Position update "+source+" "+engineeringUnitsValue);
			}
			
			@Override
			public void onLinkLimit(AbstractLink source, PIDLimitEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//parse out the extruder configs
		//parse delta robot configs
		
		setExtrusionTempreture(getTempreture());

		setParser(new ServoStockGCodeParser(this));
			
		try{
			reloadSlic3rSettings();
		}catch(Exception e){e.printStackTrace();}
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
		ThreadUtil.wait(1000);
		Log.debug("Print Done, took "+((((double)(System.currentTimeMillis()-start))/1000.0)/60.0)+" minutes");
		getParser().firePrinterStatusUpdate(PrinterState.SUCCESS);
		return b;
	}
	
	public boolean cancelPrint() {
		Log.warning("Canceling print");
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
	private void setSlicer(Slic3r slicer) {
		this.slicer = slicer;
		deltaDevice.setSlic3rConfiguration(slicer);
	}
	public Slic3r getSlicer() {
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
		d.getConnection().setSynchronusPacketTimeoutTime(5000);
	}

	private double getTempreture() {
		return temp;
	}
	private void setTempreture(double temp) {
		this.temp = temp;
	}
	
	
	public void setExtrusionTempreture(double  extTemp) {
		if(extTemp == currentTemp) {
			Log.debug("Printer at tempreture "+currentTemp+" C");
			return;
		}else
			currentTemp=extTemp;
		setTempreture(hotEnd.getCurrentEngineeringUnits());
		hotEnd.setTargetEngineeringUnits(extTemp);
		hotEnd.flush(0);
		getTempreture();
		System.out.print("\r\nWaiting for Printer to come up to tempreture "+currentTemp+" C \n");
		Log.enableSystemPrint(false);
		int iter=0;
		while(temp>(extTemp+10) || temp< (extTemp-10)) {
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
		extruder.setTargetEngineeringUnits(setPoint);
		setExtrusionCachedValue(setPoint);
	}
	
	public int getNumberOfPacketsWaiting() {
		return getDeltaDevice().getNumberOfPacketsWaiting();
	}
	
	
	public int getNumberOfSpacesInBuffer() {
		return getDeltaDevice().getNumberOfSpacesInBuffer();
	}
	
	private void cancelRunningPrint() {
		
		getDeltaDevice().cancelRunningPrint();
		
	}

	@Override
	public void sliceStatus(SliceStatusData ssd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printStatus(PrinterStatus psl) {
		// TODO Auto-generated method stub
		if(psl.getDriverState() == PrinterState.MOVING)
			firePoseTransform(forwardOffset(psl.getHeadLocation()));	
		if(psl.getDriverState() == PrinterState.PRINTING){
			//Log.warning("Received a Print status update");
			TransformNR taskSpaceTransform=psl.getHeadLocation();
			fireTargetJointsUpdate(getCurrentJointSpaceVector(), taskSpaceTransform );
		}
		
	}
	
	@Override
	protected void firePoseUpdate(){
		//Log.error("Pose update non execution. Use firePoseTransform(forwardOffset(psl.getHeadLocation()))");
		double[] vect = getCurrentJointSpaceVector();
		
		for(int i=0;i<jointSpaceUpdateListeners.size();i++){
			IJointSpaceUpdateListenerNR p=jointSpaceUpdateListeners.get(i);
			p.onJointSpaceUpdate(this, vect);
		}
	}
	
	
	public StateBasedControllerConfiguration getStateBasedControllerConfiguration(){
		return  getDeltaDevice().getStateBasedControllerConfiguration();
	}
	
	public void setStateBasedControllerConfiguration(StateBasedControllerConfiguration conf){
		 getDeltaDevice().setStateBasedControllerConfiguration(conf);
	}

	@Override
	/**
	 * This method uses the latch values to home all of the robot links
	 */
	public void homeAllLinks() {
		 getDeltaDevice().homeRobot();
	} 

	public void setPausePrintState(boolean pause){
		getDeltaDevice().setPausePrintState(pause);
	}
	
	boolean getPausePrintState(boolean pause){
		return getDeltaDevice().getPausePrintState();
	}
	
	public void zeroExtrusion(double extrusionPosition){
		//extruder.
		System.out.println("Extrusion was: "+extruder.getCurrentEngineeringUnits());
		getDeltaDevice().ResetPIDChannel(extruder.getLinkConfiguration().getHardwareIndex(), (int) extrusionPosition);
		System.out.println("Extrusion now: "+extruder.getCurrentEngineeringUnits());
	}

	public void reloadSlic3rSettings() {
		// TODO Auto-generated method stub
		setSlicer(deltaDevice.getSlic3rConfiguration());
	}
	
}
