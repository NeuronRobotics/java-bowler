package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class SearchTreeSolver implements DhInverseSolver {
	private DHChain dhChain;
	private double [] upper;
	private double [] lower;
	private boolean debug;
	double startingIncrement = 1.5;//degrees
	private TransformNR target;
	public SearchTreeSolver(DHChain dhChain, boolean debug) {
		this.setDhChain(dhChain);
		this.debug = debug;
		upper = dhChain.getUpperLimits();
		lower = dhChain.getlowerLimits();
	}

	@Override
	public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector) {
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
	
	public TransformNR getTarget() {
		return target;
	}

	public void setTarget(TransformNR target) {
		this.target = target;
	}

	public DHChain getDhChain() {
		return dhChain;
	}

	public void setDhChain(DHChain dhChain) {
		this.dhChain = dhChain;
	}
	public TransformNR fk(double[] jointSpaceVector){
		return getDhChain().forwardKinematics(jointSpaceVector);
	}

	private class searchTree{
		//double[] start;
		searchNode [] nodes;
		
		public searchTree(double[] jointSpaceVector,double startingIncrement){
			nodes = new searchNode [jointSpaceVector.length];
			for(int i=0;i<jointSpaceVector.length;i++){
				nodes[i] = new searchNode(i,jointSpaceVector[i],startingIncrement);
			}
			
		}
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
	private class configuration{
		private final double[] joints;
		private TransformNR transform;
		private final TransformNR target;
		double o;
		double v;

		public configuration(double [] joints, TransformNR t){
			this.joints = joints.clone();
			target = t;
		}
		public TransformNR getTransform() {
			if(transform == null){
				transform = fk(getJoints());
				o = transform.getOffsetOrentationMagnitude(target);
				v = transform.getOffsetVectorMagnitude(target);
			}
			return transform;
		}
		public double[] getJoints() {
			return joints;
		}
		public double getOffsetOrentationMagnitude(){
			getTransform();
			return o;
		}
		public double getOffsetVectorMagnitude(){
			getTransform();
			return v;
		}
		public boolean same(configuration c){
			for(int i=0;i<6;i++){
				if(		c.getJoints()[i]>getJoints()[i]+.1 ||
						c.getJoints()[i]<getJoints()[i]-.1){
					return false;
				}
			}
			return true;
		}
		public String toString(){
			getTransform();
			String s="\tTarget = "+target.toString()+"\n\tVector = "+v+"\n\tOrent "+o+"\n\tCurrent = "+getTransform().toString();	
			return s;
		}
	}
	private class searchNode{
		private double start;
		private final double startingIncrement;
		private final int link;
		public searchNode(int link,double start, double inc){
			this.link = link;
			this.start = start;
			startingIncrement = inc;
			if (inc<0)
				throw new RuntimeException("Increment must be positive");
		}
		public void setCurrent(double d) {
			start=d;
		}
		double getUpper(){
			double b = start+startingIncrement;
			if(		b>getDhChain().getUpperLimits()[link] ||
					b<getDhChain().getlowerLimits()[link]
			   ){
				throw new RuntimeException("Limit bounded");
			}
			return b;
		}
		double getLower(){
			double b=  start-startingIncrement;
			if(		b>getDhChain().getUpperLimits()[link] ||
					b<getDhChain().getlowerLimits()[link]
			   ){

				throw new RuntimeException("Limit bounded");
			}
			return b;
		}
		double getNone(){
			return start;
		}
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