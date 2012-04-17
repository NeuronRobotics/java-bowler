package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;

import Jama.Matrix;

import com.neuronrobotics.addons.driving.virtual.VirtualGenericPIDDevice;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
//import com.neuronrobotics.sdk.pid.IPIDControl;

public class GenericKinematicsModelNR extends AbstractKinematicsNR {
	
	double deg2rad=Math.PI/180;
	
	public GenericKinematicsModelNR(InputStream configFile,GenericPIDDevice device ){
		super(configFile,new LinkFactory( device));
	}
	public GenericKinematicsModelNR(GenericPIDDevice dev){
		super(XmlFactory.getDefaultConfigurationStream("GenericKinematics.xml"),new LinkFactory( dev));
	}

	public GenericKinematicsModelNR() {
		super(XmlFactory.getDefaultConfigurationStream("GenericKinematics.xml"),new LinkFactory( new VirtualGenericPIDDevice(1000000)));
	}

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


}
