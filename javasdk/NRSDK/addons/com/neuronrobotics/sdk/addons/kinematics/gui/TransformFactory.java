package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.Color;
import javafx.scene.Group;
import javafx.scene.transform.Affine;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public class TransformFactory {
	
	public static Affine getTransform(double x, double y, double z){
		return getTransform(new TransformNR(x, y, z, new RotationNR()));
	}
	
	public static Affine getTransform(TransformNR input){
		Affine rotations =new Affine();
		return getTransform( input , rotations);
	}
	
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
	
	public static Group getLabledAxis(Affine trans, String text,Color color){
		Group back = new Group();
		back.getChildren().add(new Axis(color));
		back.getTransforms().add(trans);
		
		return back;
	}
	public static Group getLabledAxis(TransformNR input, String text,Color color){
		Affine trans = getTransform(input);
		return getLabledAxis(trans, text,color);
	}
}
