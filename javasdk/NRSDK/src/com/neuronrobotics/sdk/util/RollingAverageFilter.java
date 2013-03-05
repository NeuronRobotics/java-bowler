package com.neuronrobotics.sdk.util;

public class RollingAverageFilter {
	double [] data;
	int index = 0;
	double average = 0;
	
	public RollingAverageFilter(int size, double startingValue){
		data = new double[size];
		average = startingValue*size;
		for(int i=0;i<size;i++){
			data[i]=startingValue;
		}
	}
	
	public double add(double value){
		average +=value;
		average -=data[index];
		data[index]=value;
		index++;
		if(index==data.length){
			index=0;
		}
		
		return getValue();
	}
	
	public double getValue(){
		return average/data.length;
	}

}
