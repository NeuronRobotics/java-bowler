package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


//  Auto-generated Javadoc
/**
 * The Class GradiantDecentNode.
 */
public class GradiantDecentNode{
	
	/** The target. */
	TransformNR target;
	
	/** The index. */
	private int index;
	
	/** The offset. */
	double offset;

	/** The my start. */
	double myStart=0;
	
	/** The joint space vector. */
	double[] jointSpaceVector;
	
	/** The upper. */
	double upper;
	
	/** The lower. */
	double lower;
	
	/** The chain. */
	private final DHChain chain;
	
	/** The inc vect. */
	double incVect;
	
	/** The inc orient. */
	double incOrient;
	
	/** The integral size. */
	//integral
	int integralSize = 100;
	
	/** The integral index vect. */
	int integralIndexVect = 0;
	
	/** The integral index orient. */
	int integralIndexOrient = 0;
	
	/** The integral total vect. */
	double integralTotalVect = 0;
	
	/** The integral total orient. */
	double integralTotalOrient = 0;
	
	/** The int vect. */
	double intVect[] = new double[integralSize]; 
	
	/** The int orient. */
	double intOrient[] = new double[integralSize]; 
	
	/** The Kp. */
	double Kp = 1;
	
	/** The Ki. */
	double Ki = 1;
	
	/**
	 * Instantiates a new gradiant decent node.
	 *
	 * @param chain the chain
	 * @param index the index
	 * @param jointSpaceVector the joint space vector
	 * @param cartesianSpace the cartesian space
	 * @param u the u
	 * @param l the l
	 */
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
			intOrient[i]=0;
		}
	}
	
	/**
	 * Step orient.
	 *
	 * @return true, if successful
	 */
	public boolean stepOrient(){
		double none =  myStart+offset;
		double start = offset;
		jointSpaceVector[getIndex()]= bound (none);
		TransformNR tmp =chain.forwardKinematics(jointSpaceVector);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double noneOrient = tmp.getOffsetOrientationMagnitude(target);
		
		double incOrientP = (noneOrient*10);//Multiply by magic number
		//Remove old values off rolling buffer
		integralTotalOrient-=intOrient[integralIndexOrient];
		//Store current values
		intOrient[integralIndexOrient] =incOrientP;
		//Add current values to totals
		integralTotalOrient+=intOrient[integralIndexOrient];
		//Reset the index for next iteration
		integralIndexOrient++;
		if(integralIndexOrient==integralSize){
			integralIndexOrient=0;
		}
		
		//The 2 increment numbers
		incOrient = incOrientP*Kp + (integralTotalOrient/integralSize)*Ki;
		
		double upO = myStart+offset+incOrient;
		double downO =myStart+offset-incOrient;
		
		jointSpaceVector[getIndex()]= bound (upO);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double upOrient = tmp.getOffsetOrientationMagnitude(target);
		
		jointSpaceVector[getIndex()]= bound (downO);
		tmp =chain.forwardKinematics(jointSpaceVector);
		double downOrient = tmp.getOffsetOrientationMagnitude(target);
		

		if( (upOrient>noneOrient && downOrient>noneOrient)){
			jointSpaceVector[getIndex()]=none;
		}

		if(( noneOrient>upOrient && downOrient>upOrient)){
			jointSpaceVector[getIndex()]=upO;
			offset+=incOrient;
		}
		if((upOrient>downOrient && noneOrient>downOrient )){
			jointSpaceVector[getIndex()]=downO;
			offset-=incOrient;
		}
		
		jointSpaceVector[getIndex()] = myStart+offset;
		if(start == offset)
			return true;
		return false;
	}
	
	/**
	 * Step lin.
	 *
	 * @return true, if successful
	 */
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
	
	/**
	 * Step.
	 *
	 * @return true, if successful
	 */
	public boolean step() {
		boolean back = stepOrient()||stepLin();
		return back;
	}
	
	/**
	 * Jitter.
	 */
	public void jitter(){
		double jitterAmmount = 10;
		double jitter=(Math.random()*jitterAmmount)-(jitterAmmount /2) ;
		com.neuronrobotics.sdk.common.Log.error("Jittering Link #"+getIndex()+" jitter:"+jitter+" current offset:"+offset);
		offset += jitter;
		jointSpaceVector[getIndex()] = myStart+offset;
	}
	
	/**
	 * Bound.
	 *
	 * @param in the in
	 * @return the double
	 */
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
	
	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
}
