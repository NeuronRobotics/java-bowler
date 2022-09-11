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
		
		double[] calculated2 = new double[] {calculated[0]+360,calculated[1]+360,calculated[2]+360};
		score(calculated2,current,scores,kin);
		double[] calculated3 = new double[] {calculated[0]-360,calculated[1]-360,calculated[2]-360};
		score(calculated3,current,scores,kin);
		double[] calculated4 = new double[] {alt1[0]+360,alt1[1]+360,alt1[2]+360};
		score(calculated4,current,scores,kin);
		double[] calculated5 = new double[] {alt1[0]-360,alt1[1]-360,alt1[2]-360};
		score(calculated5,current,scores,kin);
		double[] calculated6 = new double[] {calculated[0]-360,calculated[1],calculated[2]};
		score(calculated6,current,scores,kin);
		double[] calculated7 = new double[] {calculated[0]+360,calculated[1],calculated[2]};
		score(calculated7,current,scores,kin);

		double[] calculated8 = new double[] {alt1[0]-360,alt1[1],alt1[2]};
		score(calculated8,current,scores,kin);
		double[] calculated9 = new double[] {alt1[0]+360,alt1[1],alt1[2]};
		score(calculated9,current,scores,kin);
		
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
			calculated[i]=calculated[i] % 360;
			if(calculated[i] >kin.getMaxEngineeringUnits(i3)) {
				return;
			}
			if(calculated[i] <kin.getMinEngineeringUnits(i3)) {
				return;
			}
			double measure = current[i]-calculated[i];
			if(Math.abs(measure)>Math.abs(delt)) {
				delt=measure;
			}
		}
		scores.put(calculated, Math.abs(delt));
	}
}
