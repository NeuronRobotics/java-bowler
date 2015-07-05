package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.gui.TransformFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.Log;
public  class DHChain {
	private ArrayList<DHLink> links = new ArrayList<DHLink>();
	private ArrayList<TransformNR> chain = new ArrayList<TransformNR>();
	private ArrayList<TransformNR> intChain = new ArrayList<TransformNR>();
	private double[] upperLimits;
	private double[] lowerLimits;
	private boolean debug=false;
	private DhInverseSolver is;
	
	public DHChain(InputStream configFile,LinkFactory f){
		NodeList nList = XmlFactory.getAllNodesFromTag("DHParameters", configFile);
		for (int i = 0; i < nList.getLength(); i++) {			
		    Node nNode = nList.item(i);
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	getLinks().add(new DHLink((Element)	nNode));//0->1
		    	
		    }
		}
		upperLimits = f.getUpperLimits();
		lowerLimits = f.getLowerLimits();
	}
	
	public void addLink(DHLink link){
		if(!getLinks().contains(link)){
			getLinks().add(link);
		}
	}
	
	public void removeLink(DHLink link){
		if(getLinks().contains(link)){
			getLinks().remove(link);
		}
	}
	
	
//	public DHChain(double [] upperLimits,double [] lowerLimits, boolean debugViewer ) {
//		this(upperLimits, lowerLimits);
//
//	}
//	
//	public DHChain(double [] upperLimits,double [] lowerLimits ) {
//		
//		this.upperLimits = upperLimits;
//		this.lowerLimits = lowerLimits;
//		getLinks().add(new DHLink(	13, 	Math.toRadians(180), 	32, 	Math.toRadians(-90)));//0->1
//		getLinks().add(new DHLink(	25, 	Math.toRadians(-90), 	93, 	Math.toRadians(180)));//1->2
//		getLinks().add(new DHLink(	11, 	Math.toRadians(90), 	24, 	Math.toRadians(90)));//2->3 
//		getLinks().add(new DHLink(	128, 	Math.toRadians(-90), 		0, 		Math.toRadians(90)));//3->4
//		
//		getLinks().add(new DHLink(	0, 		Math.toRadians(0), 			0, 		Math.toRadians(-90)));//4->5
//		getLinks().add(new DHLink(	25, 	Math.toRadians(90), 		0, 		Math.toRadians(0)));//5->tool
//		
//		forwardKinematics(new  double [] {0,0,0,0,0,0});
//	}

	public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector )throws Exception {
		
		if(getLinks() == null)
			return null;
		long start = System.currentTimeMillis();
		
		
		//is = new GradiantDecent(this,debug);
		//is = new SearchTreeSolver(this,debug);
		if(getInverseSolver() == null)
			setInverseSolver(new ComputedGeometricModel(this,debug));
		
		double [] inv = getInverseSolver().inverseKinematics(target, jointSpaceVector);	
		if(debug){
			//getViewer().updatePoseDisplay(getChain(jointSpaceVector));
		}
		
		//Log.info( "Inverse Kinematics took "+(System.currentTimeMillis()-start)+"ms");
		return inv;
	}

	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		return forwardKinematics(jointSpaceVector, false);
	}
	
	public TransformNR forwardKinematics(double[] jointSpaceVector, boolean store) {
		return new TransformNR(forwardKinematicsMatrix(jointSpaceVector, store) );
	}
	
	/**
	 * Gets the Jacobian matrix
	 * 
	 * @return a matrix representing the Jacobian for the current configuration
	 */
	public Matrix getJacobian(double[] jointSpaceVector){
		double [][] data = new double[getLinks().size()][6]; 
		getChain(jointSpaceVector);
		for(int i=0;i<getLinks().size();i++){
			
			double [] zVect = new double [3];
			
			if(i==0){
				zVect[0]=0;
				zVect[1]=0;
				zVect[2]=1;
			}else{
				//Get the rz vector from matrix
				zVect[0]=chain.get(i-1).getRotationMatrix().getRotationMatrix()[0][2];
				zVect[1]=chain.get(i-1).getRotationMatrix().getRotationMatrix()[1][2];
				zVect[2]=chain.get(i-1).getRotationMatrix().getRotationMatrix()[2][2];
			}
			//Assume all rotational joints
			//Set to zero if prismatic
			data[i][3]=zVect[0];
			data[i][4]=zVect[1];
			data[i][5]=zVect[2];
			
			//Figure out the current 
			Matrix current = new TransformNR().getMatrixTransform();
			for(int j=i;j<getLinks().size();j++) {
				Matrix step = getLinks().get(j).DhStepRotory(Math.toRadians(jointSpaceVector[j]));
				//Log.info( "Current:\n"+current+"Step:\n"+step);
				current = current.times(step);
			}
			double []rVect = new double [3];
			TransformNR tmp = new TransformNR(current);
			rVect[0]=tmp.getX();
			rVect[1]=tmp.getY();
			rVect[2]=tmp.getZ();
			
			//Cross product of rVect and Z vect
			double []xProd = crossProduct(rVect, zVect);
			
			data[i][0]=xProd[0];
			data[i][1]=xProd[1];
			data[i][2]=xProd[2];
			
		}
		
		return new Matrix(data);
	}
	
	private double [] crossProduct(double[] a, double[] b){
		double [] xProd = new double [3];
		
		xProd[0]=a[1]*b[2]-a[2]*b[1];
		xProd[1]=a[2]*b[0]-a[0]*b[2];
		xProd[2]=a[0]*b[1]-a[1]*b[0];
		
		return xProd;
	}
	
	public Matrix forwardKinematicsMatrix(double[] jointSpaceVector, boolean store) {
		if(getLinks() == null)
			return new TransformNR().getMatrixTransform();
		if (jointSpaceVector.length!=getLinks().size())
			throw new IndexOutOfBoundsException("DH links do not match defined links");
		Matrix current = new TransformNR().getMatrixTransform();
		if(store)
			setChain(new ArrayList<TransformNR>());
		for(int i=0;i<getLinks().size();i++) {
			Matrix step = getLinks().get(i).DhStepRotory(Math.toRadians(jointSpaceVector[i]));
			//Log.info( "Current:\n"+current+"Step:\n"+step);
			current = current.times(step);
			final Matrix update=current.copy();
			final int index=i;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
							TransformFactory.getTransform(new TransformNR(update), getLinks().get(index).getListener());
				}
			});
			if(store){
				intChain.add(new TransformNR(step));
				chain.add(new TransformNR(current));
			}
		}
		//Log.info( "Final:\n"+current);
		return current;
	}

	public void setChain(ArrayList<TransformNR> chain) {
		this.chain = chain;
	}

	public ArrayList<TransformNR> getChain(double[] jointSpaceVector) {
		forwardKinematics(jointSpaceVector,true);
		return chain;
	}
	
	public double[] getUpperLimits() {
		// TODO Auto-generated method stub
		return upperLimits;
	}

	public double[] getlowerLimits() {
		// TODO Auto-generated method stub
		return lowerLimits;
	}

	public void setLinks(ArrayList<DHLink> links) {
		this.links = links;
	}

	public ArrayList<DHLink> getLinks() {
		return links;
	}
	
	public String toString(){
		String s="";
		for(DHLink l:getLinks()){
			s+=l.toString()+"\n";
		}
		return s;
				
	}

	public DhInverseSolver getInverseSolver() {
		if(is==null){
			is=new DhInverseSolver() {
				
				@Override
				public double[] inverseKinematics(TransformNR target,
						double[] jointSpaceVector) {
					int linkNum = jointSpaceVector.length;
					double [] inv = new double[linkNum];
					// this is an ad-hock kinematic model for d-h parameters and only works for specific configurations

					double dx = links.get(1).getD()-
							links.get(2).getD();
					double dy = links.get(0).getR();
					
					double xSet = target.getX();
					double ySet = target.getY();
					
					double polarR = Math.sqrt(xSet*xSet+ySet*ySet);
					double polarTheta = Math.asin(ySet/polarR);
					
					
					double adjustedR = Math.sqrt((polarR*polarR)+(dx*dx))-dy;
					double adjustedTheta =Math.asin(dx/polarR);
					
					
					xSet = adjustedR*Math.sin(polarTheta-adjustedTheta);
					ySet = adjustedR*Math.cos(polarTheta-adjustedTheta);
				
					
					double orentation = polarTheta-adjustedTheta;
					
					double zSet = target.getZ()
							-links.get(0).getD();
					if(links.size()>4){
						zSet+=links.get(4).getD();
					}
					// Actual target for anylitical solution is above the target minus the z offset
					TransformNR overGripper = new TransformNR(
							xSet,
							ySet,
							zSet,
							target.getRotation());


					double l1 = links.get(1).getR();// First link length
					double l2 = links.get(2).getR();

					double vect = Math.sqrt(xSet*xSet+ySet*ySet+zSet*zSet);
					Log.info( "TO: "+overGripper);
					Log.info( "polarR: "+polarR);
					Log.info( "polarTheta: "+Math.toDegrees(polarTheta));
					Log.info( "adjustedTheta: "+Math.toDegrees(adjustedTheta));
					Log.info( "adjustedR: "+adjustedR);
					
					Log.info( "x Correction: "+xSet);
					Log.info( "y Correction: "+ySet);
					
					Log.info( "Orentation: "+Math.toDegrees(orentation));
					Log.info( "z: "+zSet);

					

					if (vect > l1+l2) {
						throw new RuntimeException("Hypotenus too long: "+vect+" longer then "+l1+l2);
					}
					//from https://www.mathsisfun.com/algebra/trig-solving-sss-triangles.html
					double a=l2;
					double b=l1;
					double c=vect;
					double A =Math.acos((Math.pow(b,2)+ Math.pow(c,2) - Math.pow(a,2)) / (2*b*c));
					double B =Math.acos((Math.pow(c,2)+ Math.pow(a,2) - Math.pow(b,2)) / (2*a*c));
					double C =Math.PI-A-B;//Rule of triangles
					double elevation = Math.asin(zSet/vect);


					Log.info( "vect: "+vect);
					Log.info( "A: "+Math.toDegrees(A));
					Log.info( "elevation: "+Math.toDegrees(elevation));
					Log.info( "l1 from x/y plane: "+Math.toDegrees(A+elevation));
					Log.info( "l2 from l1: "+Math.toDegrees(C));
					inv[0] = Math.toDegrees(orentation);
					inv[1] = -Math.toDegrees((A+elevation+links.get(1).getTheta()));
					inv[2] = (Math.toDegrees(C))+//interior angle of the triangle, map to external angle
							Math.toDegrees(links.get(2).getTheta());// offset for kinematics
					if(links.size()>3)
						inv[3] =(inv[1] -inv[2]);// keep it parallell
						// We know the wrist twist will always be 0 for this model
					if(links.size()>4)
						inv[4] = inv[0];//keep the camera orentation paralell from the base
					
					for(int i=0;i<inv.length;i++){
						Log.info( "Link#"+i+" is set to "+inv[i]);
					}
					int i=3;
					if(links.size()>3)
						i=5;
					//copy over remaining links so they do not move
					for(;i<inv.length && i<jointSpaceVector.length ;i++){
						inv[i]=jointSpaceVector[i];
					}

					return inv;
				}
			};
		}
		return is;
	}

	public void setInverseSolver(DhInverseSolver is) {
		this.is = is;
	}

}
