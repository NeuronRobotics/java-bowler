package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.namespace.bcs.pid.IExtendedPIDControl;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;
// TODO: Auto-generated Javadoc
//import com.neuronrobotics.sdk.pid.IPIDControl;

/**
 * The Class GenericKinematicsModelNR.
 */
public class GenericKinematicsModelNR extends AbstractKinematicsNR {
	
	/** The deg2rad. */
	double deg2rad=Math.PI/180;
	
	/**
	 * Instantiates a new generic kinematics model nr.
	 *
	 * @param configFile the config file
	 * @param device the device
	 */
	public GenericKinematicsModelNR(InputStream configFile,GenericPIDDevice device ){
		super(configFile,new LinkFactory( device));
	}
//	public GenericKinematicsModelNR(IExtendedPIDControl dev){
//		super(XmlFactory.getDefaultConfigurationStream("GenericKinematics.xml"),new LinkFactory( dev));
//	}

	/**
 * Instantiates a new generic kinematics model nr.
 */
public GenericKinematicsModelNR() {
		super(XmlFactory.getDefaultConfigurationStream("GenericKinematics.xml"),new LinkFactory( new VirtualGenericPIDDevice(1000000)));
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#forwardKinematics(double[])
	 */
	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		double x  =  jointSpaceVector[0];
		double y  =  jointSpaceVector[1];
		double z  =  jointSpaceVector[2];
		
		Matrix rotX= new Matrix(RotationNR.getRotationX(jointSpaceVector[3]).getRotationMatrix());
		Matrix rotY= new Matrix(RotationNR.getRotationY(jointSpaceVector[4]).getRotationMatrix());
		Matrix rotZ= new Matrix(RotationNR.getRotationZ(jointSpaceVector[5]).getRotationMatrix());
		
		Matrix rotAll = rotX.times(rotY).times(rotZ);

		TransformNR back =new TransformNR(	x,
										y,
										z,
										new RotationNR(rotAll)
									  );
		return back;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public double[] inverseKinematics(TransformNR cartesianSpaceVector)throws Exception {
		double [] inv = new double[getNumberOfLinks()];		
		//Dump from cartesian to joint space, used as an example
		inv[0]= cartesianSpaceVector.getX();
		inv[1]= cartesianSpaceVector.getY();
		inv[2]= cartesianSpaceVector.getZ();
		
		Matrix rotationMatrixArray = new Matrix(cartesianSpaceVector.getRotation().getRotationMatrix()); 
		
		//X rotation
		inv[3]=Math.atan2(-rotationMatrixArray.get(1, 2), rotationMatrixArray.get(2, 2))*180/Math.PI;
		//Y rotation
		inv[4]=Math.asin(rotationMatrixArray.get(0, 2))*180/Math.PI;		
		//Z rotation
		inv[5]=Math.atan2(-rotationMatrixArray.get(0, 1), rotationMatrixArray.get(0, 0))*180/Math.PI;
		return inv;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#disconnectDevice()
	 */
	@Override
	public void disconnectDevice() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#connectDevice()
	 */
	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return false;
	}

}
