package com.neuronrobotics.replicator.driver;

import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
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

public class DeltaForgeDevice extends GenericPIDDevice implements ILinkFactoryProvider {
	
	@Override
	public boolean connect(){
		super.connect();
		int count = getPIDChannelCount();
		for(int i=0;i<count ;i++){
			PIDConfiguration conf = getPIDConfiguration(i);
			conf.setAsync(false);
			ConfigurePIDController(conf);
		}
		return true;
	}
	
	private int numSpacesRemaining = 1;
	
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
		return sendLinearSection(taskSpaceTransform, mmOfFiliment, ms, false);
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
		
		numSpacesRemaining = ByteList.convertToInt(dg.getData().getBytes(	0,//Starting index
																				4),//number of bytes
																				false);//True for signed data
		//System.out.println("Running line x="+taskSpaceTransform.getX()+" y="+taskSpaceTransform.getY()+" z="+taskSpaceTransform.getZ()+" num spaces="+numSpacesRemaining);
		//Log.enableSystemPrint(false);
		return numSpacesRemaining;
	}
	
	public void cancelRunningPrint() {
		send(new CancelPrintCommand());
		
	}
	
	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		super.onAsyncResponse(data);
		if(data.getRPC().equalsIgnoreCase("_sli")) {
			//System.out.println(data);
			numSpacesRemaining = ByteList.convertToInt(data.getData().getBytes(	0,//Starting index
																				4),//number of bytes
																				false);//True for signed data
		}
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
	@Override
	public double[] setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) {
		
		return new double []{0,0,0,0,0};
	}
	@Override
	public TransformNR getCurrentTaskSpaceTransform() {
		// TODO Auto-generated method stub
		return new TransformNR();
	}
	@Override
	public TransformNR setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds) {
		// TODO Auto-generated method stub
		return new TransformNR();
	}
	@Override
	public void setDesiredJointAxisValue(int axis, double value, double seconds) {
		// TODO Auto-generated method stub
		
	}

	
}
