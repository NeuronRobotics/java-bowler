package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.TransformFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.Log;
// TODO: Auto-generated Javadoc

/**
 * The Class DHChain.
 */
public  class DHChain {
	
	/** The links. */
	private ArrayList<DHLink> links = new ArrayList<DHLink>();
	
	/** The chain. */
	private ArrayList<TransformNR> chain = new ArrayList<TransformNR>();
	
	/** The int chain. */
	private ArrayList<TransformNR> intChain = new ArrayList<TransformNR>();
	
	/** The upper limits. */
	private double[] upperLimits;
	
	/** The lower limits. */
	private double[] lowerLimits;
	
	/** The debug. */
	private boolean debug=false;
	
	/** The is. */
	private DhInverseSolver is;
	
	/** The kin. */
	private AbstractKinematicsNR kin;
	
	/** The factory. */
	private LinkFactory factory;
	static{
		new JFXPanel(); // initializes JavaFX environment
	}
	
	/**
	 * Instantiates a new DH chain.
	 *
	 * @param kin the kin
	 */
	public DHChain( AbstractKinematicsNR kin){
		this.kin = kin;

	}
	
	/**
	 * Adds the link.
	 *
	 * @param link the link
	 */
	public void addLink(DHLink link){
		if(!getLinks().contains(link)){
			getLinks().add(link);
		}
	}
	
	/**
	 * Removes the link.
	 *
	 * @param link the link
	 */
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

	/**
 * Inverse kinematics.
 *
 * @param target the target
 * @param jointSpaceVector the joint space vector
 * @return the double[]
 * @throws Exception the exception
 */
public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector )throws Exception {
		
		if(getLinks() == null)
			return null;
		long start = System.currentTimeMillis();
		
		
		//is = new GradiantDecent(this,debug);
		//is = new SearchTreeSolver(this,debug);
		if(getInverseSolver() == null)
			setInverseSolver(new ComputedGeometricModel(this,debug));
		
		double [] inv = getInverseSolver().inverseKinematics(target, jointSpaceVector,this);	
		if(debug){
			//getViewer().updatePoseDisplay(getChain(jointSpaceVector));
		}
		
		//Log.info( "Inverse Kinematics took "+(System.currentTimeMillis()-start)+"ms");
		return inv;
	}

	/**
	 * Forward kinematics.
	 *
	 * @param jointSpaceVector the joint space vector
	 * @return the transform nr
	 */
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		return forwardKinematics(jointSpaceVector, true);
	}
	
	/**
	 * Forward kinematics.
	 *
	 * @param jointSpaceVector the joint space vector
	 * @param store the store
	 * @return the transform nr
	 */
	public TransformNR forwardKinematics(double[] jointSpaceVector, boolean store) {
		return new TransformNR(forwardKinematicsMatrix(jointSpaceVector, store) );
	}
	
	/**
	 * Gets the Jacobian matrix.
	 *
	 * @param jointSpaceVector the joint space vector
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
				zVect[0]=intChain.get(i-1).getRotationMatrix().getRotationMatrix()[0][2];
				zVect[1]=intChain.get(i-1).getRotationMatrix().getRotationMatrix()[1][2];
				zVect[2]=intChain.get(i-1).getRotationMatrix().getRotationMatrix()[2][2];
			}
			//Assume all rotational joints
			//Set to zero if prismatic
			if(getLinks().get(i).getLinkType()==DhLinkType.ROTORY){
				data[i][3]=zVect[0];
				data[i][4]=zVect[1];
				data[i][5]=zVect[2];
			}else{
				data[i][3]=0;
				data[i][4]=0;
				data[i][5]=0;
			}
			
			//Figure out the current 
			Matrix current = new TransformNR().getMatrixTransform();
			for(int j=i;j<getLinks().size();j++) {
				double value=0;
				if(getLinks().get(j).getLinkType()==DhLinkType.ROTORY)
					value=Math.toRadians(jointSpaceVector[j]);
				else
					value=jointSpaceVector[j];
				Matrix step = getLinks().get(j).DhStep(value);
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
	
	/**
	 * Cross product.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double[]
	 */
	private double [] crossProduct(double[] a, double[] b){
		double [] xProd = new double [3];
		
		xProd[0]=a[1]*b[2]-a[2]*b[1];
		xProd[1]=a[2]*b[0]-a[0]*b[2];
		xProd[2]=a[0]*b[1]-a[1]*b[0];
		
		return xProd;
	}
	
	/**
	 * Forward kinematics matrix.
	 *
	 * @param jointSpaceVector the joint space vector
	 * @param store the store
	 * @return the matrix
	 */
	public Matrix forwardKinematicsMatrix(double[] jointSpaceVector, boolean store) {
		if(getLinks() == null)
			return new TransformNR().getMatrixTransform();
		if (jointSpaceVector.length!=getLinks().size())
			throw new IndexOutOfBoundsException("DH links do not match defined links");
		Matrix current = new TransformNR().getMatrixTransform();
		if(store)
			setChain(new ArrayList<TransformNR>());
		for(int i=0;i<getLinks().size();i++) {
			LinkConfiguration conf= getFactory().getLinkConfigurations().get(i);
			Matrix step;
			if(conf.getType().isPrismatic())
				step= getLinks().get(i).DhStep(jointSpaceVector[i]);
			else
				step= getLinks().get(i).DhStep(Math.toRadians(jointSpaceVector[i]));
			//Log.info( "Current:\n"+current+"Step:\n"+step);
			current = current.times(step);
			final Matrix update=current.copy();
			final int index=i;
			final TransformNR pose =forwardOffset(new TransformNR(update));
			//getLinks().get(index).fireOnLinkGlobalPositionChange(pose);	

			if(store){
				if(intChain.size()<=i)
					intChain.add(new TransformNR(step));
				else{
					intChain.set(i, new TransformNR(step));
				}
				if(chain.size()<=i)
					chain.add(pose);
				else{
					chain.set(i, pose);
				}
			}
		}
		//Log.info( "Final:\n"+current);
		return current;
	}
	
	/**
	 * Forward offset.
	 *
	 * @param transformNR the transform nr
	 * @return the transform nr
	 */
	private TransformNR forwardOffset(TransformNR transformNR) {
		return kin.forwardOffset(transformNR);
	}

	/**
	 * Sets the chain.
	 *
	 * @param chain the new chain
	 */
	public void setChain(ArrayList<TransformNR> chain) {
		this.chain = chain;
	}

	/**
	 * Gets the chain.
	 *
	 * @param jointSpaceVector the joint space vector
	 * @return the chain
	 */
	public ArrayList<TransformNR> getChain(double[] jointSpaceVector) {
		forwardKinematics(jointSpaceVector,true);
		return chain;
	}
	
	/**
	 * Gets the cached chain.
	 *
	 * @return the cached chain
	 */
	public ArrayList<TransformNR> getCachedChain() {
		
		return chain;
	}
	
	/**
	 * Gets the upper limits.
	 *
	 * @return the upper limits
	 */
	public double[] getUpperLimits() {
		// TODO Auto-generated method stub
		return upperLimits;
	}

	/**
	 * Gets the lower limits.
	 *
	 * @return the lower limits
	 */
	public double[] getlowerLimits() {
		// TODO Auto-generated method stub
		return lowerLimits;
	}

	/**
	 * Sets the links.
	 *
	 * @param links the new links
	 */
	public void setLinks(ArrayList<DHLink> links) {
		this.links = links;
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public ArrayList<DHLink> getLinks() {
		return links;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s="";
		for(DHLink l:getLinks()){
			s+=l.toString()+"\n";
		}
		return s;
				
	}

	/**
	 * Gets the inverse solver.
	 *
	 * @return the inverse solver
	 */
	public DhInverseSolver getInverseSolver() {
		if(is==null){
			is=new DhInverseSolver() {
				
				@Override
				public double[] inverseKinematics(TransformNR target,
						double[] jointSpaceVector, DHChain chain ) {
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

	/**
	 * Sets the inverse solver.
	 *
	 * @param is the new inverse solver
	 */
	public void setInverseSolver(DhInverseSolver is) {
		this.is = is;
	}

	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public LinkFactory getFactory() {
		return factory;
	}

	/**
	 * Sets the factory.
	 *
	 * @param factory the new factory
	 */
	public void setFactory(LinkFactory factory) {
		upperLimits = factory.getUpperLimits();
		lowerLimits = factory.getLowerLimits();
		this.factory = factory;
	}

}
