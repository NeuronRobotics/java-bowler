package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public class TransformFactory {
	
	public static Transform3D getTransform(double x, double y, double z){
		return getTransform(new TransformNR(x, y, z, new RotationNR()));
	}
	
	public static Transform3D getTransform(TransformNR input){
		double [][] data = input.getMatrixTransform().getArray();
		int div = 20;
		data[0][3]/=div;
		data[1][3]/=div;
		data[2][3]/=div;
		double [] output = new double [16];
		int x=0;
		int y=0;
		for(int i=0;i<16;i++){
			output[i]=data[y][x++];
			if(x>3){
				x=0;
				y++;
			}
		}
		Transform3D t = new Transform3D(output);
		return t;
	}
	public static TransformGroup getLabledAxis(Transform3D trans, String text,Color color){
		TransformGroup back = new TransformGroup(trans);
		
		
		Appearance textAppear = new Appearance();
        Material textMaterial = new Material();
        textMaterial.setDiffuseColor(new Color3f(color));
        textAppear.setMaterial(textMaterial);
        
        Font3D font3D = new Font3D(new Font("Helvetica", Font.PLAIN, 1), new FontExtrusion());
		Text3D textGeom = new Text3D(font3D, new String(text));
		textGeom.setAlignment(Text3D.ALIGN_FIRST);
		Shape3D textShape = new Shape3D();
		textShape.setGeometry(textGeom);
		textShape.setAppearance(textAppear);
		
		back.addChild(new Axis());
		Transform3D txtTrans = new Transform3D();
		txtTrans.mul(trans);
		txtTrans.rotX(Math.PI/2);
		txtTrans.rotY(-Math.PI/2);
		TransformGroup textGroup = new TransformGroup(txtTrans);
		//Add text
		textGroup.addChild(textShape);
		
		back.addChild(textGroup);

		back.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		back.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		back.setCapability(TransformGroup.ALLOW_BOUNDS_READ);
		back.setCapability(TransformGroup.ALLOW_BOUNDS_WRITE);
		back.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	
		
		back.setPickable(true);
		return back;
	}
	public static TransformGroup getLabledAxis(TransformNR input, String text,Color color){
		//System.out.println("Creating transform "+text+" with\n"+input);
		Transform3D trans = getTransform(input);
		return getLabledAxis(trans, text,color);
	}
}
