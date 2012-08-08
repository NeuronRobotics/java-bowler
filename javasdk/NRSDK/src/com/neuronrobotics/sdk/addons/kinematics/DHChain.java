package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;

import Jama.Matrix;
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
		if(is == null)
			is = new ComputedGeometricModel(this,debug);
		
		double [] inv = is.inverseKinematics(target, jointSpaceVector);	
		if(debug){
			//getViewer().updatePoseDisplay(getChain(jointSpaceVector));
		}
		
		//System.out.println("Inverse Kinematics took "+(System.currentTimeMillis()-start)+"ms");
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
				//System.out.println("Current:\n"+current+"Step:\n"+step);
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
			//System.out.println("Current:\n"+current+"Step:\n"+step);
			current = current.times(step);
			if(store){
				intChain.add(new TransformNR(step));
				chain.add(new TransformNR(current));
			}
		}
		//System.out.println("Final:\n"+current);
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

}
