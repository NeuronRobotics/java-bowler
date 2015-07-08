package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import javax.xml.transform.TransformerFactory;

import javafx.application.Platform;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.gui.TransformFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;

public class DHLink {
	
	private double d;
	private double theta;
	private double radius;
	private double alpha;
	private Matrix transX;
	private Matrix rotX;
	private Matrix transZ;
	private Matrix rotZ;
	
	private Matrix transX_J;
	private Matrix rotX_J;
	private Matrix transZ_J;
	private Matrix rotZ_J;
	private Affine listener=null;
	private boolean degenerate = false;
	
	private ArrayList<IDhLinkPositionListener> dhlisteners = new ArrayList<IDhLinkPositionListener>();
	private String embedableXml=null;
	
	
	public DHLink(double d, double theta,double r, double alpha) {
		this.setDelta(d);
		this.setTheta(theta);
		this.setRadius(r);
		this.setAlpha(alpha);
		
	}

	public DHLink(Element nNode) {
		setDelta(XmlFactory.getTagValueDouble("Delta", nNode));
		setTheta(Math.toRadians(XmlFactory.getTagValueDouble("Theta", nNode)));
		setRadius(XmlFactory.getTagValueDouble("Radius", nNode));
		setAlpha(Math.toRadians(XmlFactory.getTagValueDouble("Alpha", nNode)));
	}
	
	public void fireOnLinkGlobalPositionChange(TransformNR newPose){
		for(IDhLinkPositionListener l:dhlisteners){
			l.onLinkGlobalPositionChange(newPose);
		}
	}
	
	public void addDhLinkPositionListener(IDhLinkPositionListener l){
		if(!dhlisteners.contains(l))
			dhlisteners.add(l);
	}
	public void removeDhLinkPositionListener(IDhLinkPositionListener l){
		if(dhlisteners.contains(l))
			dhlisteners.remove(l);
	}
	/*
	 * 
	 * Generate the xml configuration to generate a link of this configuration. 
	 */
	public String getXml(){
		String mb = embedableXml==null?"":"\n\t\t"+embedableXml+"\n";
		return "\n\t<DHParameters>\n"+
		    "\t\t<Delta>"+d+"</Delta>\n"+
		    "\t\t<Theta>"+Math.toDegrees(theta)+"</Theta>\n"+
		   "\t\t<Radius>"+radius+"</Radius>\n"+
		   "\t\t<Alpha>"+Math.toDegrees(alpha)+"</Alpha>\n"+
		   mb+
		"\t</DHParameters>\n";
	}
	public double getD() {
		return getDelta();
	}

	public double getTheta() {
		return theta;
	}

	public double getR() {
		return getRadius();
	}

	public double getAlpha() {
		return alpha;
	}
	public Matrix DhStepInverseRotory(Matrix end, double jointValue) {	
		if(degenerate)
			jointValue=0;
		return  DhStepInverse(end,jointValue,0);
	}
	public Matrix DhStepInversePrismatic(Matrix end, double jointValue) {	
		if(degenerate)
			jointValue=0;
		return  DhStepInverse(end,0,jointValue);
	}
	public Matrix DhStepRotory(double jointValue) {	
		if(degenerate)
			jointValue=0;
		return DhStep(jointValue,0);
	}
	public Matrix DhStepPrismatic(double jointValue) {
		if(degenerate)
			jointValue=0;
		
		return DhStep(0,jointValue);
	}
	
	public Matrix DhStep(double rotory,double prismatic) {

		setMatrix(rotory, prismatic);
		
		Matrix step = getTransZ();
		step = step.times(getRotZ());
		step = step.times(getTransX());
		step = step.times(getRotX());
		

		return step;
	}
	
	public Matrix DhStepInverse(Matrix end,double rotory,double prismatic) {
		setMatrix(rotory, prismatic);
		
		Matrix step = end.times(getTransZ().inverse());
		step = step.times(getRotZ().inverse());
		step = step.times(getTransX().inverse());
		step = step.times(getRotX().inverse());
		
		return step;
	}


	public void setTransX(Matrix transX) {
		this.transX = transX;
	}

	public void setRotX(Matrix rotX) {
		this.rotX = rotX;
	}
	
	
	private void setMatrix(double rotory,double prismatic){
		transZ = new Matrix( new double [][] {	
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,getD()+prismatic},
				{0,0,0,1}});
		
		rotZ=new Matrix( new double [][] {	
				{Math.cos(getTheta()+rotory),	 -Math.sin(getTheta()+rotory),	0,	0},
				{Math.sin(getTheta()+rotory),	  Math.cos(getTheta()+rotory),	0,	0},
				{0,															0,	1,	0},
				{0,															0,	0,	1}});
	}
	public Matrix getTransX() {
		 if(transX == null){
			 transX=new Matrix( new double [][] {	
					{1,0,0,getR()},
					{0,1,0,0},
					{0,0,1,0},
					{0,0,0,1}});
		}	
		return transX;
	}

	public Matrix getRotX() {
		 if(rotX == null){
			 rotX=new Matrix( new double [][] {	
				{1,	0,						0,							0},
				{0,	Math.cos(getAlpha()),	-Math.sin(getAlpha()),		0},
				{0,	Math.sin(getAlpha()),	Math.cos(getAlpha()),		0},
				{0,	0,						0,							1}});
		 }
		return rotX;
	}
	public Matrix getTransZ() {
		return transZ;
	}

	public Matrix getRotZ() {
		return rotZ;
	}
	
	/**
	 * Gets a jacobian matrix of this link
	 * @param rotoryVelocity
	 * @param prismaticVelocity
	 * @return the Jacobian
	 */
	public Matrix DhStepJacobian(double rotoryVelocity,double prismaticVelocity) {

		setJacobianMatrix(rotoryVelocity, prismaticVelocity);
		
		Matrix step = getTransZ_J();
		step = step.times(getRotZ_J());
		step = step.times(getTransX_J());
		step = step.times(getRotX_J());
		
		return step;
	}
	/**
	 * Sets up the 2 alterable Jacobian matrixs 
	 * @param rotory the rotory velocity of the Theta link
	 * @param prismatic the linear velocity of the D link
	 */
	private void setJacobianMatrix(double rotory,double prismatic){
		rotZ_J = new Matrix( new double [][] {	
				{1,0,0,					0	},
				{0,1,0,					0	},
				{0,0,1,	getD()+prismatic	},
				{0,0,0,					1	}});
		
		transZ_J  = new Matrix( new double [][] {	
				{Math.cos(getTheta()+rotory),-Math.sin(getTheta()+rotory),	0,	0},
				{Math.sin(getTheta()+rotory), Math.cos(getTheta()+rotory),	0,	0},
				{0,														0,	1,	0},
				{0,														0,	0,	1}});
	}
	public Matrix getRotX_J() {
		 if(rotX_J == null){
			 rotX_J = new Matrix( new double [][] {	
				{1,	0,						0,							0},
				{0,	Math.cos(getAlpha()),	-Math.sin(getAlpha()),		0},
				{0,	Math.sin(getAlpha()),	Math.cos(getAlpha()),		0},
				{0,	0,						0,							1}});
		 }
		return rotX_J;
	}
	
	public Matrix getTransX_J() {
		 if(transX_J == null){
			 transX_J= new Matrix( new double [][] {	
					{1,0,0,getR()},
					{0,1,0,0},
					{0,0,1,0},
					{0,0,0,1}});
		 }	
		return transX_J;
	}



	public Matrix getTransZ_J() {
		return transZ_J;
	}
	public Matrix getRotZ_J() {
		return rotZ_J;
	}
	
	@Override 
	public String toString(){
		String s="";
		s+=" Delta = "+getDelta();
		s+=" Theta = "+Math.toDegrees(getTheta())+" deg";
		s+=" Radius = "+getRadius();
		s+=" Alpha = "+Math.toDegrees(getAlpha())+" deg";
		return s;
	}

	public Affine getListener() {
		return listener;
	}

	void setListener(Affine listener) {
		this.listener = listener;
	}

	public double getDelta() {
		return d;
	}

	public void setDelta(double d) {
		this.d = d;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		transX_J=null;
		transX=null;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
		rotX=null;
		rotX_J=null;
	}

	public boolean isDegenerate() {
		return degenerate;
	}

	public void setDegenerate(boolean degenerate) {
		this.degenerate = degenerate;
	}

	public void setMobileBaseXml(String embedableXml) {
		this.embedableXml = embedableXml;
	}

}
