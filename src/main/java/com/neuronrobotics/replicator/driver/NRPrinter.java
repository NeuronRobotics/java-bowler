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

// TODO: Auto-generated Javadoc
/**
 * The Class NRPrinter.
 */
public class NRPrinter extends CartesianNamespacePidKinematics implements PrinterStatusListener{
	
	/** The parser. */
	private ServoStockGCodeParser parser;
	
	/** The slicer. */
	private Slic3r slicer;
	
	/** The delta device. */
	private BowlerBoardDevice deltaDevice;
	
	/** The extrusion cached value. */
	//Configuration hard coded
	private  double extrusionCachedValue = 0;
	
	/** The current temp. */
	private double currentTemp =0;
	
	/** The extruder. */
	private AbstractLink extruder;
	
	/** The hot end. */
	private AbstractLink hotEnd;
	
	/** The temp. */
	private double temp = 0;

	//private boolean printRunning=false;
	
	/**
	 * Instantiates a new NR printer.
	 *
	 * @param d the d
	 */
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
	 * Slice.
	 *
	 * @param stl the input stream
	 * @param gcode the gcode to be written to
	 * @return true, if successful
	 */
	public boolean slice(File stl,File gcode) {
		return getSlicer().slice(stl, gcode);
	}
	
	/**
	 * Prints the.
	 *
	 * @param gcode the gcode to be sent to the printer
	 * @return true, if successful
	 * @throws Exception the exception
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
	
	/**
	 * Cancel print.
	 *
	 * @return true, if successful
	 */
	public boolean cancelPrint() {
		Log.warning("Canceling print");
		cancelRunningPrint();
		return getParser().cancel();
	}
	
	/**
	 * Checks if is ready.
	 *
	 * @return true, if is ready
	 */
	public boolean isReady() {
		// TODO Auto-generated method stub
		return getParser().isReady();
	}
	
	/**
	 * Adds the printer status listener.
	 *
	 * @param l the l
	 */
	public void addPrinterStatusListener(PrinterStatusListener l) {
		getParser().addPrinterStatusListener(l);
		getSlicer().addPrinterStatusListener(l);
		deltaDevice.addPrinterStatusListener(l);
	}
	
	/**
	 * Removes the printer status listener.
	 *
	 * @param l the l
	 */
	public void removePrinterStatusListener(PrinterStatusListener l) {
		getParser().removePrinterStatusListener(l);
		getSlicer().removePrinterStatusListener(l);
		deltaDevice.removePrinterStatusListener(l);
	}
	
	/**
	 * Sets the slicer.
	 *
	 * @param slicer the new slicer
	 */
	private void setSlicer(Slic3r slicer) {
		this.slicer = slicer;
		deltaDevice.setSlic3rConfiguration(slicer);
	}
	
	/**
	 * Gets the slicer.
	 *
	 * @return the slicer
	 */
	public Slic3r getSlicer() {
		return slicer;
	}
	
	/**
	 * Sets the parser.
	 *
	 * @param parser the new parser
	 */
	private void setParser(ServoStockGCodeParser parser) {
		this.parser = parser;
	}
	
	/**
	 * Gets the parser.
	 *
	 * @return the parser
	 */
	public ServoStockGCodeParser getParser() {
		return parser;
	}

	/**
	 * Gets the delta device.
	 *
	 * @return the delta device
	 */
	public BowlerBoardDevice getDeltaDevice() {
		return deltaDevice;
	}

	/**
	 * Sets the delta device.
	 *
	 * @param d the new delta device
	 */
	public void setDeltaDevice(BowlerBoardDevice d) {
		this.deltaDevice = d;
		d.getConnection().setSynchronusPacketTimeoutTime(5000);
	}

	/**
	 * Gets the tempreture.
	 *
	 * @return the tempreture
	 */
	private double getTempreture() {
		return temp;
	}
	
	/**
	 * Sets the tempreture.
	 *
	 * @param temp the new tempreture
	 */
	private void setTempreture(double temp) {
		this.temp = temp;
	}
	
	
	/**
	 * Sets the extrusion tempreture.
	 *
	 * @param extTemp the new extrusion tempreture
	 */
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
	
	/**
	 * Sets the bed tempreture.
	 *
	 * @param bedTemp the new bed tempreture
	 */
	public void setBedTempreture(double bedTemp) {
		
	}
	
	/**
	 * Sets the desired print locetion.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @param extrusionLegnth the extrusion legnth
	 * @param seconds the seconds
	 * @return the int
	 * @throws Exception the exception
	 */
	public int setDesiredPrintLocetion(TransformNR taskSpaceTransform,double extrusionLegnth, double seconds) throws Exception{
		//System.out.println("Telling printer to go to extrusion len "+extrusionLegnth);
		return getDeltaDevice().sendLinearSection(taskSpaceTransform, extrusionLegnth, (int) (seconds*1000));
	}
	
	/**
	 * Gets the extrusion cached value.
	 *
	 * @return the extrusion cached value
	 */
	public double getExtrusionCachedValue() {
		return extrusionCachedValue;
	}

	/**
	 * Sets the extrusion cached value.
	 *
	 * @param extrusionCachedValue the new extrusion cached value
	 */
	public void setExtrusionCachedValue(double extrusionCachedValue) {
		this.extrusionCachedValue = extrusionCachedValue;
	}
	
	/**
	 * Sets the extrusion point.
	 *
	 * @param materialNumber the material number
	 * @param setPoint the set point
	 */
	public void setExtrusionPoint(int materialNumber, double setPoint) {
		//TODO another method to set material
		extruder.setTargetEngineeringUnits(setPoint);
		setExtrusionCachedValue(setPoint);
	}
	
	/**
	 * Gets the number of packets waiting.
	 *
	 * @return the number of packets waiting
	 */
	public int getNumberOfPacketsWaiting() {
		return getDeltaDevice().getNumberOfPacketsWaiting();
	}
	
	
	/**
	 * Gets the number of spaces in buffer.
	 *
	 * @return the number of spaces in buffer
	 */
	public int getNumberOfSpacesInBuffer() {
		return getDeltaDevice().getNumberOfSpacesInBuffer();
	}
	
	/**
	 * Cancel running print.
	 */
	private void cancelRunningPrint() {
		
		getDeltaDevice().cancelRunningPrint();
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.replicator.driver.PrinterStatusListener#sliceStatus(com.neuronrobotics.replicator.driver.SliceStatusData)
	 */
	@Override
	public void sliceStatus(SliceStatusData ssd) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.replicator.driver.PrinterStatusListener#printStatus(com.neuronrobotics.replicator.driver.PrinterStatus)
	 */
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
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#firePoseUpdate()
	 */
	@Override
	protected void firePoseUpdate(){
		//Log.error("Pose update non execution. Use firePoseTransform(forwardOffset(psl.getHeadLocation()))");
		double[] vect = getCurrentJointSpaceVector();
		
		for(int i=0;i<jointSpaceUpdateListeners.size();i++){
			IJointSpaceUpdateListenerNR p=jointSpaceUpdateListeners.get(i);
			p.onJointSpaceUpdate(this, vect);
		}
	}
	
	
	/**
	 * Gets the state based controller configuration.
	 *
	 * @return the state based controller configuration
	 */
	public StateBasedControllerConfiguration getStateBasedControllerConfiguration(){
		return  getDeltaDevice().getStateBasedControllerConfiguration();
	}
	
	/**
	 * Sets the state based controller configuration.
	 *
	 * @param conf the new state based controller configuration
	 */
	public void setStateBasedControllerConfiguration(StateBasedControllerConfiguration conf){
		 getDeltaDevice().setStateBasedControllerConfiguration(conf);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#homeAllLinks()
	 */
	@Override
	/**
	 * This method uses the latch values to home all of the robot links
	 */
	public void homeAllLinks() {
		 getDeltaDevice().homeRobot();
	} 

	/**
	 * Sets the pause print state.
	 *
	 * @param pause the new pause print state
	 */
	public void setPausePrintState(boolean pause){
		getDeltaDevice().setPausePrintState(pause);
	}
	
	/**
	 * Gets the pause print state.
	 *
	 * @param pause the pause
	 * @return the pause print state
	 */
	boolean getPausePrintState(boolean pause){
		return getDeltaDevice().getPausePrintState();
	}
	
	/**
	 * Zero extrusion.
	 *
	 * @param extrusionPosition the extrusion position
	 */
	public void zeroExtrusion(double extrusionPosition){
		//extruder.
		//System.out.println("Extrusion was: "+extruder.getCurrentEngineeringUnits());
		getDeltaDevice().ResetPIDChannel(extruder.getLinkConfiguration().getHardwareIndex(), (int) extrusionPosition);
		///System.out.println("Extrusion now: "+extruder.getCurrentEngineeringUnits());
	}

	/**
	 * Reload slic3r settings.
	 */
	public void reloadSlic3rSettings() {
		// TODO Auto-generated method stub
		setSlicer(deltaDevice.getSlic3rConfiguration());
	}
	
}
