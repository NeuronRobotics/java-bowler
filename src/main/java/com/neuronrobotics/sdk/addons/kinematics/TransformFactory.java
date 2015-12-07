package com.neuronrobotics.sdk.addons.kinematics;

import java.awt.Color;
import javafx.scene.Group;
import javafx.scene.transform.Affine;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating Transform objects.
 */
public class TransformFactory {
	
	/**
	 * Gets the transform.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return the transform
	 */
	public static Affine getTransform(double x, double y, double z){
		return getTransform(new TransformNR(x, y, z, new RotationNR()));
	}
	
	/**
	 * Gets the transform.
	 *
	 * @param input the input
	 * @return the transform
	 */
	public static Affine getTransform(TransformNR input){
		Affine rotations =new Affine();
		return getTransform( input , rotations);
	}
	
	/**
	 * Gets the transform.
	 *
	 * @param input the input
	 * @param rotations the rotations
	 * @return the transform
	 */
	public static Affine getTransform(TransformNR input ,Affine rotations){
		double[][] poseRot = input
				.getRotationMatrixArray();
		
		rotations.setMxx(poseRot[0][0]);
		rotations.setMxy(poseRot[0][1]);
		rotations.setMxz(poseRot[0][2]);
		rotations.setMyx(poseRot[1][0]);
		rotations.setMyy(poseRot[1][1]);
		rotations.setMyz(poseRot[1][2]);
		rotations.setMzx(poseRot[2][0]);
		rotations.setMzy(poseRot[2][1]);
		rotations.setMzz(poseRot[2][2]);
		rotations.setTx(input.getX());
		rotations.setTy(input.getY());
		rotations.setTz(input.getZ());
		return rotations;
	}
	
	
}
