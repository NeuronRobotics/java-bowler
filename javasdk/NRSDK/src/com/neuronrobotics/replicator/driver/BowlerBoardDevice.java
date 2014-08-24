package com.neuronrobotics.replicator.driver;

import java.util.ArrayList;

import javax.print.PrintService;
import javax.vecmath.Point3f;

import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.commands.cartesian.CancelPrintCommand;
import com.neuronrobotics.sdk.commands.cartesian.LinearInterpolationCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.ILinkFactoryProvider;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class BowlerBoardDevice extends GenericPIDDevice implements ILinkFactoryProvider {
	
	private ArrayList<PrinterStatusListener> statusListeners = new ArrayList<PrinterStatusListener>();
	
	public void addPrinterStatusListener(PrinterStatusListener l){
		if(statusListeners.contains(l) || l==null)
			return;
		statusListeners.add(l);
	}
	public void removePrinterStatusListener(PrinterStatusListener l){
		if(statusListeners.contains(l))
			statusListeners.remove(l);
	}
	
	@Override
	public boolean connect(){
		super.connect();
//		int count = getPIDChannelCount();
//		for(int i=0;i<count ;i++){
//			SetPIDSetPoint(i, GetPIDPosition(i), 0);
//			PIDConfiguration conf = getPIDConfiguration(i);
//			conf.setAsync(false);
//			ConfigurePIDController(conf);
//		}
		return true;
	}
	
	private int numSpacesRemaining = 1;
	private int sizeOfBuffer = 1;
	/**
	 * This function will set up a multi-dimentional send for position and interpolation
	 * @param x new x position
	 * @param y new y position
	 * @param z new z position
	 * @param mmOfFiliment new target for mm of filiment
	 * @param ms time in MS
	 * @return number of spaces in the buffer
	 */
	public int sendLinearSection(TransformNR taskSpaceTransform, double mmOfFiliment, int ms) {
		return sendLinearSection(taskSpaceTransform, mmOfFiliment, ms, ms==0);
	}
	/**
	 * This function will set up a multi-dimentional send for position and interpolation
	 * @param x new x position
	 * @param y new y position
	 * @param z new z position
	 * @param mmOfFiliment new target for mm of filiment
	 * @param ms time in MS
	 * @return number of spaces in the buffer
	 */
	public int sendLinearSection(TransformNR taskSpaceTransform, double mmOfFiliment, int ms, boolean forceNoBuffer) {
		//Log.enableInfoPrint();
		RuntimeException e= new RuntimeException("There is no more room left");;
		if(numSpacesRemaining == 0 ) {
			throw e;
		}
		
		BowlerDatagram dg = send(new LinearInterpolationCommand(taskSpaceTransform, mmOfFiliment, ms,forceNoBuffer));
		if(dg.getRPC().equalsIgnoreCase("_err")) {
			throw e;
		}
		try{
			numSpacesRemaining = ByteList.convertToInt(dg.getData().getBytes(	0,//Starting index
																					4),//number of bytes
																					false);//True for signed data
			sizeOfBuffer = ByteList.convertToInt(dg.getData().getBytes(	4,//Starting index
																		4),//number of bytes
																		false);//True for signed data
			//System.out.println("Running line x="+taskSpaceTransform.getX()+" y="+taskSpaceTransform.getY()+" z="+taskSpaceTransform.getZ()+" num spaces="+numSpacesRemaining);
			//Log.enableSystemPrint(false);
			return numSpacesRemaining;
		}catch (RuntimeException ex){
			Log.error("Response failed: "+dg);
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public void cancelRunningPrint() {
		send(	"bcs.cartesian.*",
				BowlerMethod.POST,
				"pclr",
				new Object[]{}, 5);

	}
	
	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		super.onAsyncResponse(data);
		if(data.getRPC().equalsIgnoreCase("_sli")) {
			System.out.println(data);
			numSpacesRemaining = ByteList.convertToInt(data.getData().getBytes(	0,//Starting index
																				4),//number of bytes
																				false);//True for signed data
		}else if(data.getRPC().equalsIgnoreCase("cpos")) {
			//
			float status[] = new float [6];
			for (int i=0;i<6;i++){
					status[i] = (float)(ByteList.convertToInt(data.getData().getBytes(	i*4,//Starting index
																				4),//number of bytes
																				true)/1000.0);//True for signed data
			}
			PrinterStatus stat = new PrinterStatus(new TransformNR(status[0], status[1], status[2],new RotationNR()),status[3],status[4], (int) status[5], PrinterState.PRINTING);
			//numSpacesRemaining = stat.getPrintProgress();
			for(int i=0;i<statusListeners.size();i++ ){
				
				statusListeners.get(i).printStatus(stat);
			}
		}
		//System.out.println("Remaining = "+numSpacesRemaining);
	}
	
	public int getNumberOfPacketsWaiting() {
		return sizeOfBuffer-numSpacesRemaining-1;
	}

	public int getNumberOfSpacesInBuffer() {
		return numSpacesRemaining;
	}
	@Override
	public LinkConfiguration requestLinkConfiguration(int index) {
		Object [] args = send("bcs.cartesian.*",
								BowlerMethod.GET,
				"gcfg",
				new Object[]{index}, 5);
		
		return new LinkConfiguration(args);
	}
	
	public void runKinematicsEngine(boolean index) {
		 send("bcs.cartesian.*",
				BowlerMethod.POST,
				"runk",
				new Object[]{index}, 5);
		
		return;
	}
	
	@Override
	public double[] setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) {
		
		Object [] args = send(	"bcs.cartesian.*",
								BowlerMethod.POST,
								"sdtt",
								new Object[]{	taskSpaceTransform.getX(),
												taskSpaceTransform.getY(),
												taskSpaceTransform.getZ(),
												taskSpaceTransform.getRotation().getRotationMatrix2QuaturnionX(),
												taskSpaceTransform.getRotation().getRotationMatrix2QuaturnionY(),
												taskSpaceTransform.getRotation().getRotationMatrix2QuaturnionZ(),
												taskSpaceTransform.getRotation().getRotationMatrix2QuaturnionW(),
												(int)(seconds*1000)
												}, 
										5);

		double [] jointAngles = (double[]) args[0];
		return jointAngles;
	}
	@Override
	public TransformNR getCurrentTaskSpaceTransform() {
		Object [] args = send(	"bcs.cartesian.*",
				BowlerMethod.GET,
				"gctt",
				new Object[]{}, 
						5);
		
		
		return new TransformNR(	(Double)args[0],
								(Double)args[1],
								(Double)args[2],
								(Double)args[3],
								(Double)args[4],
								(Double)args[5],
								(Double)args[6]
										
								);
	}
	@Override
	public TransformNR setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds) {
		
		Object [] args = send(	"bcs.cartesian.*",
				BowlerMethod.POST,
				"sdjv",
				new Object[]{jointSpaceVect,(int)(seconds*1000)}, 
						5);
		
		
		return new TransformNR(	(Double)args[0],
								(Double)args[1],
								(Double)args[2],
								(Double)args[3],
								(Double)args[4],
								(Double)args[5],
								(Double)args[6]
								);
	}
	@Override
	public void setDesiredJointAxisValue(int axis, double value, double seconds) {
		// TODO Auto-generated method stub
		 send(	"bcs.cartesian.*",
				BowlerMethod.POST,
				"sdsj",
				new Object[]{axis,value,(int)(seconds*1000)}, 
						5);
	}

	
}
