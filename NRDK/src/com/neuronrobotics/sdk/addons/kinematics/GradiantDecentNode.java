package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public class GradiantDecentNode{
	TransformNR target;
	private int index;
	double offset;

	double myStart=0;
	double[] jointSpaceVector;
	double upper;
	double lower;
	private final DHChain chain;
	double incVect;
	double incOrent;
	
	//integral
	int integralSize = 100;
	int integralIndexVect = 0;
	int integralIndexOrent = 0;
	double integralTotalVect = 0;
	double integralTotalOrent = 0;
	double intVect[] = new double[integralSize]; 
	double intOrent[] = new double[integralSize]; 
	
	double Kp = 1;
	double Ki = 1;
	
	public GradiantDecentNode(DHChain chain,int index,double[] jointSpaceVector,TransformNR cartesianSpace, double u, double l){
		this.chain = chain;
		this.offset=0;
		this.setIndex(index);
		this.jointSpaceVector=jointSpaceVector;
		target = cartesianSpace;
		myStart =  jointSpaceVector[index];
		upper = u;
		lower = l;
		for(int i=0;i<integralSize;i++){
			intVect[i]=0;
			intOrent[i]=0;
		}
	}
	public boolean stepOrent(){
		double none =  myStart+offset;
		double start = offset;
		jointSpaceVector[getIndex()]= bound (none);
		TransformNR tmp =chain.forwardKinematics(jointSpaceVector);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double noneOrent = tmp.getOffsetOrentationMagnitude(target);
		
		double incOrentP = (noneOrent*10);//Multiply by magic number
		//Remove old values off rolling buffer
		integralTotalOrent-=intOrent[integralIndexOrent];
		//Store current values
		intOrent[integralIndexOrent] =incOrentP;
		//Add current values to totals
		integralTotalOrent+=intOrent[integralIndexOrent];
		//Reset the index for next iteration
		integralIndexOrent++;
		if(integralIndexOrent==integralSize){
			integralIndexOrent=0;
		}
		
		//The 2 increment numbers
		incOrent = incOrentP*Kp + (integralTotalOrent/integralSize)*Ki;
		
		double upO = myStart+offset+incOrent;
		double downO =myStart+offset-incOrent;
		
		jointSpaceVector[getIndex()]= bound (upO);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double upOrent = tmp.getOffsetOrentationMagnitude(target);
		
		jointSpaceVector[getIndex()]= bound (downO);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double downOrent = tmp.getOffsetOrentationMagnitude(target);
		

		if( (upOrent>noneOrent && downOrent>noneOrent)){
			jointSpaceVector[getIndex()]=none;
		}

		if(( noneOrent>upOrent && downOrent>upOrent)){
			jointSpaceVector[getIndex()]=upO;
			offset+=incOrent;
		}
		if((upOrent>downOrent && noneOrent>downOrent )){
			jointSpaceVector[getIndex()]=downO;
			offset-=incOrent;
		}
		
		jointSpaceVector[getIndex()] = myStart+offset;
		if(start == offset)
			return true;
		return false;
	}
	public boolean stepLin(){
		double none =  myStart+offset;
		double start = offset;
		jointSpaceVector[getIndex()]= bound (none);
		TransformNR tmp =chain.forwardKinematics(jointSpaceVector);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double nonevect = tmp.getOffsetVectorMagnitude(target);
		
		double incVectP = (nonevect/1000);// Divide by magic number
		//Remove old values off rolling buffer
		integralTotalVect-=intVect[integralIndexVect];
		//Store current values
		intVect[integralIndexVect] =incVectP; 
		//Add current values to totals
		integralTotalVect+=intVect[integralIndexVect];
		//Reset the index for next iteration
		integralIndexVect++;
		if(integralIndexVect==integralSize){
			integralIndexVect=0;
		}
		
		//The 2 increment numbers
		incVect = incVectP*Kp + (integralTotalVect/integralSize)*Ki;
		
		double up = myStart+offset+incVect;
		double down =myStart+offset-incVect;
		
		jointSpaceVector[getIndex()]= bound (up);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double upvect = tmp.getOffsetVectorMagnitude(target);

		
		jointSpaceVector[getIndex()]= bound (down);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double downvect = tmp.getOffsetVectorMagnitude(target);
		

		if((upvect>nonevect && downvect>nonevect) ){
			jointSpaceVector[getIndex()]=none;
		}
		if((nonevect>upvect && downvect>upvect ) ){
			jointSpaceVector[getIndex()]=up;
			offset+=incVect;
		}
		if((upvect>downvect && nonevect>downvect)  ){
			jointSpaceVector[getIndex()]=down;
			offset-=incVect;
		}
		
		jointSpaceVector[getIndex()] = myStart+offset;
		if(start == offset)
			return true;
		return false;
	}
	
	public boolean step() {
		boolean back = stepOrent()||stepLin();
		return back;
	}
	public void jitter(){
		double jitterAmmount = 10;
		double jitter=(Math.random()*jitterAmmount)-(jitterAmmount /2) ;
		System.out.println("Jittering Link #"+getIndex()+" jitter:"+jitter+" current offset:"+offset);
		offset += jitter;
		jointSpaceVector[getIndex()] = myStart+offset;
	}
	double bound(double in){
		if(in>upper){
			offset = 0;// Attempt to reset a link on error case
			return upper;
		}
		if(in<lower){
			offset = 0;// Attempt to reset a link on error case
			return lower;
		}
		return in;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getIndex() {
		return index;
	}
}
