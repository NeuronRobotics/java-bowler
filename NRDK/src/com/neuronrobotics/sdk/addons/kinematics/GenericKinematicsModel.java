package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;

import Jama.Matrix;

import com.neuronrobotics.addons.driving.virtual.VirtualGenericPIDDevice;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.Rotation;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
//import com.neuronrobotics.sdk.pid.IPIDControl;

public class GenericKinematicsModel extends AbstractKinematics {
	
	double deg2rad=Math.PI/180;
	
	public GenericKinematicsModel(InputStream configFile,GenericPIDDevice device ){
		super(configFile,new LinkFactory( device));
	}
	public GenericKinematicsModel(GenericPIDDevice dev){
		super(XmlFactory.getDefaultConfigurationStream("GenericKinematics.xml"),new LinkFactory( dev));
	}

	public GenericKinematicsModel() {
		super(XmlFactory.getDefaultConfigurationStream("GenericKinematics.xml"),new LinkFactory( new VirtualGenericPIDDevice(1000000)));
	}

	@Override
	public Transform forwardKinematics(double[] jointSpaceVector) {
		double x  =  jointSpaceVector[0];
		double y  =  jointSpaceVector[1];
		double z  =  jointSpaceVector[2];
		
		Matrix rotX= new Matrix(Rotation.getRotationX(jointSpaceVector[3]).getRotationMatrix());
		Matrix rotY= new Matrix(Rotation.getRotationY(jointSpaceVector[4]).getRotationMatrix());
		Matrix rotZ= new Matrix(Rotation.getRotationZ(jointSpaceVector[5]).getRotationMatrix());
		
		Matrix rotAll = rotX.times(rotY).times(rotZ);

		Transform back =new Transform(	x,
										y,
										z,
										new Rotation(rotAll)
									  );
		return back;
	}

	@Override
	public double[] inverseKinematics(Transform cartesianSpaceVector)throws Exception {
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
