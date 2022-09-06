package com.neuronrobotics.sdk.addons.kinematics;

import java.util.HashMap;

public class WristNormalizer {
	public static double[] normalize(double[] calculated,double[] current, DHChain chain) {
		AbstractKinematicsNR kin = chain.kin;
		//DecimalFormat df = new DecimalFormat("000.00");
		double[] alt1 = new double[] {calculated[0]-180,-calculated[1],calculated[2]-180};
		
		HashMap<double[],Double> scores= new HashMap<>();
		score(calculated,current,scores,kin);
		score(alt1,current,scores,kin);

		score(new double[] {calculated[0]-360,calculated[1],calculated[2]},current,scores,kin);
		score(new double[] {calculated[0]+360,calculated[1],calculated[2]},current,scores,kin);

		score(new double[] {alt1[0]-360,alt1[1],alt1[2]},current,scores,kin);
		score(new double[] {alt1[0]+360,alt1[1],alt1[2]},current,scores,kin);
		
		double score=scores.get(calculated);
		double[] ret=calculated;
		for(double[]  tmp:scores.keySet()) {
			double delt =scores.get(tmp)
;			if(delt<score) {
				score=delt;
				ret=tmp;
			}
		}
		scores.clear();
		return ret;
	}
	
	private static void score(double[] calculated,double[] current,HashMap<double[],Double> scores,AbstractKinematicsNR kin ) {
		double delt=0;
		for(int i=0;i<3;i++) {
			int i3 = i+3;
			if(calculated[i] >kin.getMaxEngineeringUnits(i3)) {
				calculated[i]-=360;
			}
			if(calculated[i] <kin.getMinEngineeringUnits(i3)) {
				calculated[i]+=360;
			}
			double measure = current[i]-calculated[i];
			if(Math.abs(measure)>Math.abs(delt)) {
				delt=measure;
			}
		}
		scores.put(calculated, Math.abs(delt));
	}
}
