package com.neuronrobotics.sdk.addons.kinematics;

import java.util.HashMap;

public class WristNormalizer {
	private static boolean strictMode = false;
	
	public static double[] normalize(double[] calculated, double[] current, DHChain chain) {
		AbstractKinematicsNR kin = chain.kin;
		// DecimalFormat df = new DecimalFormat("000.00");
		double[] alt1 = new double[] { calculated[0] - 180, -calculated[1], calculated[2] - 180 };
		double[] calculated2 = new double[] { calculated[0] + 360, calculated[1] + 360, calculated[2] + 360 };
		double[] calculated3 = new double[] { calculated[0] - 360, calculated[1] - 360, calculated[2] - 360 };
		double[] alt2 = new double[] { alt1[0] + 360, alt1[1] + 360, alt1[2] + 360 };
		double[] alt3 = new double[] { alt1[0] - 360, alt1[1] - 360, alt1[2] - 360 };
		double[] calculated6 = new double[] { calculated[0] - 360, calculated[1], calculated[2] };
		double[] calculated7 = new double[] { calculated[0] + 360, calculated[1], calculated[2] };
		double[] als4 = new double[] { alt1[0] - 360, alt1[1], alt1[2] };
		double[] alt5 = new double[] { alt1[0] + 360, alt1[1], alt1[2] };
		
		
		HashMap<double[], Double> scores = new HashMap<>();
		score(calculated, current, scores, kin);
		score(alt1, current, scores, kin);
		score(calculated2, current, scores, kin);
		score(calculated3, current, scores, kin);
		score(alt2, current, scores, kin);
		score(alt3, current, scores, kin);
		score(calculated6, current, scores, kin);
		score(calculated7, current, scores, kin);
		score(als4, current, scores, kin);
		score(alt5, current, scores, kin);
		if (scores.size() > 0) {
			double score = scores.get(calculated);
			double[] ret = calculated;
			for (double[] tmp : scores.keySet()) {
				double delt = scores.get(tmp);
				if (delt < score) {
					score = delt;
					ret = tmp;
				}
			}
			scores.clear();

			return ret;
		}
		if(!strictMode)
			return current;
		throw new RuntimeException("No Wrist Solution! ");
	}

	private static void score(double[] calculated, double[] current, HashMap<double[], Double> scores,
			AbstractKinematicsNR kin) {
		double delt = 0;
		for (int i = 0; i < 3; i++) {
			int i3 = i + 3;
			calculated[i] = calculated[i] % 360;
			
			if (calculated[i] > kin.getMaxEngineeringUnits(i3)) {
				if(strictMode)return;
				calculated[i]=kin.getMaxEngineeringUnits(i3);
			}
			if (calculated[i] < kin.getMinEngineeringUnits(i3)) {
				if(strictMode)return;
				calculated[i]=kin.getMinEngineeringUnits(i3);
			}
			double measure = current[i] - calculated[i];
			if (Math.abs(measure) > Math.abs(delt)) {
				delt = measure;
			}
		}
		scores.put(calculated, Math.abs(delt));
	}

	public static boolean isStrictMode() {
		return strictMode;
	}
	public static void setBoundLinkValueMode() {
		WristNormalizer.strictMode = false;
	}
	public static void setStrictMode() {
		WristNormalizer.strictMode = true;
	}
}
