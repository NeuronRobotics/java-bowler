package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchTreeSolver.
 */
public class SearchTreeSolver implements DhInverseSolver {
	
	/** The dh chain. */
	private DHChain dhChain;
	
	/** The upper. */
	private double [] upper;
	
	/** The lower. */
	private double [] lower;
	
	/** The debug. */
	private boolean debug;
	
	/** The starting increment. */
	double startingIncrement = 1.5;//degrees
	
	/** The target. */
	private TransformNR target;
	
	/**
	 * Instantiates a new search tree solver.
	 *
	 * @param dhChain the dh chain
	 * @param debug the debug
	 */
	public SearchTreeSolver(DHChain dhChain, boolean debug) {
		this.setDhChain(dhChain);
		this.debug = debug;
		upper = dhChain.getUpperLimits();
		lower = dhChain.getlowerLimits();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.DhInverseSolver#inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math.TransformNR, double[], com.neuronrobotics.sdk.addons.kinematics.DHChain)
	 */
	@Override
	public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector, 
			 DHChain chain ) {
		ArrayList<DHLink> links = chain.getLinks();
		setTarget(target);
		searchTree step=new searchTree(jointSpaceVector,startingIncrement);;
		boolean done = false;
		configuration conf = new configuration(jointSpaceVector, target);
//		double previousV =conf.getOffsetOrentationMagnitude();
//		double previousO =conf.getOffsetVectorMagnitude();
		int iter = 1000;
		int i = 0;
		do{
			double [] current = conf.getJoints();
			conf = step.getBest(current);
			
			double vect = conf.getOffsetOrentationMagnitude();
			double orent = conf.getOffsetVectorMagnitude();
			
			if(vect<10 && orent< .05){
				done = true;
				System.out.println("SearchTreeSolver Success stats: \n\tIterations = "+i+" out of "+iter+"\n"+conf);
			}
			if(i++==iter){
				done = true;
				System.err.println("SearchTreeSolver FAILED stats: \n\tIterations = "+i+" out of "+iter+"\n"+conf);
			}
		}while(! done);

		return conf.getJoints();
	}
	
	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public TransformNR getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 *
	 * @param target the new target
	 */
	public void setTarget(TransformNR target) {
		this.target = target;
	}

	/**
	 * Gets the dh chain.
	 *
	 * @return the dh chain
	 */
	public DHChain getDhChain() {
		return dhChain;
	}

	/**
	 * Sets the dh chain.
	 *
	 * @param dhChain the new dh chain
	 */
	public void setDhChain(DHChain dhChain) {
		this.dhChain = dhChain;
	}
	
	/**
	 * Fk.
	 *
	 * @param jointSpaceVector the joint space vector
	 * @return the transform nr
	 */
	public TransformNR fk(double[] jointSpaceVector){
		return getDhChain().forwardKinematics(jointSpaceVector);
	}

	/**
	 * The Class searchTree.
	 */
	private class searchTree{
		
		/** The nodes. */
		//double[] start;
		searchNode [] nodes;
		
		/**
		 * Instantiates a new search tree.
		 *
		 * @param jointSpaceVector the joint space vector
		 * @param startingIncrement the starting increment
		 */
		public searchTree(double[] jointSpaceVector,double startingIncrement){
			nodes = new searchNode [jointSpaceVector.length];
			for(int i=0;i<jointSpaceVector.length;i++){
				nodes[i] = new searchNode(i,jointSpaceVector[i],startingIncrement);
			}
			
		}
		
		/**
		 * Gets the best.
		 *
		 * @param jointSpaceVector the joint space vector
		 * @return the best
		 */
		public configuration getBest(double[] jointSpaceVector){
			ArrayList<configuration> configurations = new ArrayList<configuration> ();
			for(int i=0;i<jointSpaceVector.length;i++){
				nodes[i].setCurrent(jointSpaceVector[i]);
			}
			double [] tmp = new double[6];
			int num = 3;
			for(int i=0;i<num;i++){
				try{
					tmp[0]=nodes[0].get(i);
					for(int i1=0;i1<num;i1++){
						try{
							tmp[1]=nodes[1].get(i1);
							for(int i2=0;i2<num;i2++){
								try{
									tmp[2]=nodes[2].get(i2);
									for(int i3=0;i3<num;i3++){
										try{
											tmp[3]=nodes[3].get(i3);
											for(int i4=0;i4<num;i4++){
												try{
													tmp[4]=nodes[4].get(i4);
													for(int i5=0;i5<num;i5++){
														try{
															tmp[5]=nodes[5].get(i5);
															boolean same = false;
															configuration tempConf = new configuration(tmp.clone(),getTarget());
															for(configuration c:configurations){
																if(tempConf.same(c))
																	same = true;
															}
															if(!same)
																configurations.add(tempConf);
														}catch(Exception ex){}
													}
												}catch(Exception ex){}
											}
										}catch(Exception ex){}
									}
								}catch(Exception ex){}
							}
						}catch(Exception ex){}
					}
				}catch(Exception ex){}
			}
			
			int best = 0;
			int i=0;
			double orent=configurations.get(0).getOffsetOrentationMagnitude();
			double vect =configurations.get(0).getOffsetVectorMagnitude();
			for(configuration c:configurations){
				double tmpOrent = c.getOffsetOrentationMagnitude();
				double tmpVector= c.getOffsetVectorMagnitude();
				if(
						tmpOrent<=orent && 
						tmpVector<=vect){
					orent=tmpOrent;
					vect=tmpVector;
					best = i;
				}
				i++;
			}
			//System.out.println("Selecting "+best+" config");
			return configurations.get(best);
		}
	}
	
	/**
	 * The Class configuration.
	 */
	private class configuration{
		
		/** The joints. */
		private final double[] joints;
		
		/** The transform. */
		private TransformNR transform;
		
		/** The target. */
		private final TransformNR target;
		
		/** The o. */
		double o;
		
		/** The v. */
		double v;

		/**
		 * Instantiates a new configuration.
		 *
		 * @param joints the joints
		 * @param t the t
		 */
		public configuration(double [] joints, TransformNR t){
			this.joints = joints.clone();
			target = t;
		}
		
		/**
		 * Gets the transform.
		 *
		 * @return the transform
		 */
		public TransformNR getTransform() {
			if(transform == null){
				transform = fk(getJoints());
				o = transform.getOffsetOrentationMagnitude(target);
				v = transform.getOffsetVectorMagnitude(target);
			}
			return transform;
		}
		
		/**
		 * Gets the joints.
		 *
		 * @return the joints
		 */
		public double[] getJoints() {
			return joints;
		}
		
		/**
		 * Gets the offset orentation magnitude.
		 *
		 * @return the offset orentation magnitude
		 */
		public double getOffsetOrentationMagnitude(){
			getTransform();
			return o;
		}
		
		/**
		 * Gets the offset vector magnitude.
		 *
		 * @return the offset vector magnitude
		 */
		public double getOffsetVectorMagnitude(){
			getTransform();
			return v;
		}
		
		/**
		 * Same.
		 *
		 * @param c the c
		 * @return true, if successful
		 */
		public boolean same(configuration c){
			for(int i=0;i<6;i++){
				if(		c.getJoints()[i]>getJoints()[i]+.1 ||
						c.getJoints()[i]<getJoints()[i]-.1){
					return false;
				}
			}
			return true;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString(){
			getTransform();
			String s="\tTarget = "+target.toString()+"\n\tVector = "+v+"\n\tOrent "+o+"\n\tCurrent = "+getTransform().toString();	
			return s;
		}
	}
	
	/**
	 * The Class searchNode.
	 */
	private class searchNode{
		
		/** The start. */
		private double start;
		
		/** The starting increment. */
		private final double startingIncrement;
		
		/** The link. */
		private final int link;
		
		/**
		 * Instantiates a new search node.
		 *
		 * @param link the link
		 * @param start the start
		 * @param inc the inc
		 */
		public searchNode(int link,double start, double inc){
			this.link = link;
			this.start = start;
			startingIncrement = inc;
			if (inc<0)
				throw new RuntimeException("Increment must be positive");
		}
		
		/**
		 * Sets the current.
		 *
		 * @param d the new current
		 */
		public void setCurrent(double d) {
			start=d;
		}
		
		/**
		 * Gets the upper.
		 *
		 * @return the upper
		 */
		double getUpper(){
			double b = start+startingIncrement;
			if(		b>getDhChain().getUpperLimits()[link] ||
					b<getDhChain().getlowerLimits()[link]
			   ){
				throw new RuntimeException("Limit bounded");
			}
			return b;
		}
		
		/**
		 * Gets the lower.
		 *
		 * @return the lower
		 */
		double getLower(){
			double b=  start-startingIncrement;
			if(		b>getDhChain().getUpperLimits()[link] ||
					b<getDhChain().getlowerLimits()[link]
			   ){

				throw new RuntimeException("Limit bounded");
			}
			return b;
		}
		
		/**
		 * Gets the none.
		 *
		 * @return the none
		 */
		double getNone(){
			return start;
		}
		
		/**
		 * Gets the.
		 *
		 * @param index the index
		 * @return the double
		 */
		double get(int index){
			switch(index){
			case 0:
				return getLower();
			case 1:
				return getUpper();
			case 2:
				return getNone();
			default:
				throw new RuntimeException("Index must be 0-2");
			}
		}
	}

}