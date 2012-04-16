package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.Transform;


public class GradiantDecent implements DhInverseSolver{

	private final DHChain dhChain;
	private final boolean debug;

	public GradiantDecent(DHChain dhChain, boolean debug) {
		this.dhChain = dhChain;
		// TODO Auto-generated constructor stub
		this.debug = debug;
	}
	
	public double[] inverseKinematics(Transform target,double[] jointSpaceVector ){
		int linkNum = jointSpaceVector.length;
		double [] inv = new double[linkNum];	
		
		GradiantDecentNode [] increments = new GradiantDecentNode[linkNum];	
		for(int i=0;i<linkNum;i++){
			increments[i] = new GradiantDecentNode(dhChain,i,jointSpaceVector, target, dhChain.getUpperLimits()[i],dhChain.getlowerLimits()[i] );
		}
		double posOffset = 10;
		int iter=0;
		double vect=0;
		double orent = 0;
		boolean stopped;
		boolean notArrived = false;
		boolean [] stop = new boolean [increments.length];
		double previousV =dhChain.forwardKinematics(jointSpaceVector).getOffsetVectorMagnitude(target);
		double previousO =dhChain.forwardKinematics(jointSpaceVector).getOffsetOrentationMagnitude(target);
		do{
			stopped = true;
			for(int i=increments.length-1;i>=0;i--){
				stop[i]=increments[i].step();
				if(!stop[i]){
					stopped = false;
				}
			}
			vect = dhChain.forwardKinematics(jointSpaceVector).getOffsetVectorMagnitude(target);
			orent = dhChain.forwardKinematics(jointSpaceVector).getOffsetOrentationMagnitude(target);
			if(previousV>=vect && previousO>=orent){
				for(int i=0;i<inv.length;i++){
					inv[i]=jointSpaceVector[i];
				}
				previousV=vect;
				previousO=orent;
			}
			
			notArrived = (previousV > posOffset|| previousO > .001);
			if(stopped == true && notArrived == true){
				stopped = false;
				for(int i=0;i<increments.length;i++){
					increments[i].jitter();
				}
				//ThreadUtil.wait(100);
			}
			
			if(debug){
				//dhChain.getViewer().updatePoseDisplay(dhChain.getChain(jointSpaceVector));
			}
		}while(++iter<200 && notArrived && stopped == false);//preincrement and check
		if(debug){
			System.out.println("Numer of iterations #"+iter+" \n\tStalled = "+stopped+" \n\tArrived = "+!notArrived+" \n\tFinal offset= "+vect+" \n\tFinal orent= "+orent);
		}
		return inv;
	}
	
}
