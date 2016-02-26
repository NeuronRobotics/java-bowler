package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import javax.xml.transform.TransformerFactory;

import javafx.application.Platform;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.TransformFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class DHLink.
 */
public class DHLink {
	
	/** The d. */
	private double d;
	
	/** The theta. */
	private double theta;
	
	/** The radius. */
	private double radius;
	
	/** The alpha. */
	private double alpha;
	
	/** The trans x. */
	private Matrix transX;
	
	/** The rot x. */
	private Matrix rotX;
	
	/** The trans z. */
	private Matrix transZ;
	
	/** The rot z. */
	private Matrix rotZ;
	
	/** The trans x_ j. */
	private Matrix transX_J;
	
	/** The rot x_ j. */
	private Matrix rotX_J;
	
	/** The trans z_ j. */
	private Matrix transZ_J;
	
	/** The rot z_ j. */
	private Matrix rotZ_J;
	
	/** The listener. */
	private Affine listener=null;
	
	/** The root. */
	private Affine root=null;
	
	/** The type. */
	private DhLinkType type = DhLinkType.ROTORY;
	
	/** The dhlisteners. */
	private ArrayList<IDhLinkPositionListener> dhlisteners = new ArrayList<IDhLinkPositionListener>();
	
	/** The embedable xml. */
	private MobileBase embedableXml=null;
	
	
	/**
	 * Instantiates a new DH link.
	 *
	 * @param d the d
	 * @param theta the theta
	 * @param r the r
	 * @param alpha the alpha
	 */
	public DHLink(double d, double theta,double r, double alpha) {
		this.setDelta(d);
		this.setTheta(theta);
		this.setRadius(r);
		this.setAlpha(alpha);
		
	}

	/**
	 * Instantiates a new DH link.
	 *
	 * @param nNode the n node
	 */
	public DHLink(Element nNode) {
		setDelta(XmlFactory.getTagValueDouble("Delta", nNode));
		setTheta(Math.toRadians(XmlFactory.getTagValueDouble("Theta", nNode)));
		setRadius(XmlFactory.getTagValueDouble("Radius", nNode));
		setAlpha(Math.toRadians(XmlFactory.getTagValueDouble("Alpha", nNode)));
		
	}
	
	/**
	 * Fire on link global position change.
	 *
	 * @param newPose the new pose
	 */
	public void fireOnLinkGlobalPositionChange(TransformNR newPose){
		for(IDhLinkPositionListener l:dhlisteners){
			l.onLinkGlobalPositionChange(newPose);
		}
	}
	
	/**
	 * Adds the dh link position listener.
	 *
	 * @param l the l
	 */
	public void addDhLinkPositionListener(IDhLinkPositionListener l){
		if(!dhlisteners.contains(l))
			dhlisteners.add(l);
	}
	
	/**
	 * Removes the dh link position listener.
	 *
	 * @param l the l
	 */
	public void removeDhLinkPositionListener(IDhLinkPositionListener l){
		if(dhlisteners.contains(l))
			dhlisteners.remove(l);
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
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
	
	/**
	 * Gets the d.
	 *
	 * @return the d
	 */
	public double getD() {
		return getDelta();
	}

	/**
	 * Gets the theta.
	 *
	 * @return the theta
	 */
	public double getTheta() {
		return theta;
	}

	/**
	 * Gets the r.
	 *
	 * @return the r
	 */
	public double getR() {
		return getRadius();
	}

	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}
	

	/**
	 * Dh step inverse .
	 *
	 * @param end the end
	 * @param jointValue the joint value
	 * @return the matrix
	 */
	public Matrix DhStepInverse(Matrix end, double jointValue) {	
		switch(type){
		case PRISMATIC:
			return DhStepInverse(end,0,jointValue);
		case ROTORY:
			return  DhStepInverse(end,jointValue,0);
		default:
		case TOOL:
			return  DhStepInverse(end,0,0);
		}

	}
	
	/**
	 * Dh step prismatic.
	 *
	 * @param jointValue the joint value
	 * @return the matrix
	 */
	public Matrix DhStep(double jointValue) {
		switch(type){
		case PRISMATIC:
			return DhStep(0,jointValue);
		case ROTORY:
			return DhStep(jointValue,0);
		default:
		case TOOL:
			return DhStep(0,0);
		}
	}
	
	
	/**
	 * Dh step.
	 *
	 * @param rotory the rotory
	 * @param prismatic the prismatic
	 * @return the matrix
	 */
	public Matrix DhStep(double rotory,double prismatic) {

		setMatrix(rotory, prismatic);
		
		Matrix step = getTransZ();
		step = step.times(getRotZ());
		step = step.times(getTransX());
		step = step.times(getRotX());
		

		return step;
	}
	
	/**
	 * Dh step inverse.
	 *
	 * @param end the end
	 * @param rotory the rotory
	 * @param prismatic the prismatic
	 * @return the matrix
	 */
	public Matrix DhStepInverse(Matrix end,double rotory,double prismatic) {
		setMatrix(rotory, prismatic);
		
		Matrix step = end.times(getRotX().inverse());
		step = step.times(getTransX().inverse());
		step = step.times(getRotZ().inverse());
		step = step.times(getTransZ().inverse());
		
		return step;
	}
	
	/**
	 * Dh step inverse.
	 *
	 * @param rotory the rotory
	 * @param prismatic the prismatic
	 * @return the matrix
	 */
	public Matrix DhStepInverse(double rotory,double prismatic) {
		
		Matrix end=new TransformNR().getMatrixTransform();
		
		return DhStepInverse(end,rotory,prismatic);
	}

	
	/**
	 * Dh step inverse.
	 *
	 * @param prismatic the prismatic
	 * @return the matrix
	 */
	public Matrix DhStepInversePrismatic(double prismatic) {
		
		return DhStepInverse(0,prismatic);
	}

	
	/**
	 * Dh step inverse.
	 *
	 * @param rotory the rotory
	 * @return the matrix
	 */
	public Matrix DhStepInverseRotory(double rotory) {
		return DhStepInverse(rotory,0);
	}


	/**
	 * Sets the trans x.
	 *
	 * @param transX the new trans x
	 */
	public void setTransX(Matrix transX) {
		this.transX = transX;
	}

	/**
	 * Sets the rot x.
	 *
	 * @param rotX the new rot x
	 */
	public void setRotX(Matrix rotX) {
		this.rotX = rotX;
	}
	
	
	/**
	 * Sets the matrix.
	 *
	 * @param rotory the rotory
	 * @param prismatic the prismatic
	 */
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
	
	/**
	 * Gets the trans x.
	 *
	 * @return the trans x
	 */
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

	/**
	 * Gets the rot x.
	 *
	 * @return the rot x
	 */
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
	
	/**
	 * Gets the trans z.
	 *
	 * @return the trans z
	 */
	public Matrix getTransZ() {
		return transZ;
	}

	/**
	 * Gets the rot z.
	 *
	 * @return the rot z
	 */
	public Matrix getRotZ() {
		return rotZ;
	}
	
	/**
	 * Gets a jacobian matrix of this link.
	 *
	 * @param rotoryVelocity the rotory velocity
	 * @param prismaticVelocity the prismatic velocity
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
	 * Sets up the 2 alterable Jacobian matrixs .
	 *
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
	
	/**
	 * Gets the rot x_ j.
	 *
	 * @return the rot x_ j
	 */
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
	
	/**
	 * Gets the trans x_ j.
	 *
	 * @return the trans x_ j
	 */
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



	/**
	 * Gets the trans z_ j.
	 *
	 * @return the trans z_ j
	 */
	public Matrix getTransZ_J() {
		return transZ_J;
	}
	
	/**
	 * Gets the rot z_ j.
	 *
	 * @return the rot z_ j
	 */
	public Matrix getRotZ_J() {
		return rotZ_J;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString(){
		String s="";
		s+=" Delta = "+getDelta();
		s+=" Theta = "+Math.toDegrees(getTheta())+" deg";
		s+=" Radius = "+getRadius();
		s+=" Alpha = "+Math.toDegrees(getAlpha())+" deg";
		return s;
	}

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public Affine getListener() {
		return listener;
	}

	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	void setListener(Affine listener) {
		this.listener = listener;
	}
	
	/**
	 * Gets the root listener.
	 *
	 * @return the root listener
	 */
	public Affine getRootListener() {
		return root;
	}

	/**
	 * Sets the root listener.
	 *
	 * @param listener the new root listener
	 */
	void setRootListener(Affine listener) {
		this.root = listener;
	}
	
	/**
	 * Gets the delta.
	 *
	 * @return the delta
	 */
	public double getDelta() {
		return d;
	}

	/**
	 * Sets the delta.
	 *
	 * @param d the new delta
	 */
	public void setDelta(double d) {
		this.d = d;
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the radius.
	 *
	 * @param radius the new radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
		transX_J=null;
		transX=null;
	}

	/**
	 * Sets the theta.
	 *
	 * @param theta the new theta
	 */
	public void setTheta(double theta) {
		this.theta = theta;
	}

	/**
	 * Sets the alpha.
	 *
	 * @param alpha the new alpha
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
		rotX=null;
		rotX_J=null;
	}

	/**
	 * Checks if is degenerate.
	 *
	 * @return true, if is degenerate
	 */
	public DhLinkType getLinkType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setLinkType(DhLinkType type) {
		this.type = type;
	}

	/**
	 * Sets the mobile base xml.
	 *
	 * @param embedableXml the new mobile base xml
	 */
	public void setMobileBaseXml(MobileBase embedableXml) {
		this.embedableXml = embedableXml;
	}

}
