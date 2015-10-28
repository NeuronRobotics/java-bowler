package com.neuronrobotics.sdk.util;

// TODO: Auto-generated Javadoc
/**
 * The Class RollingAverageFilter.
 */
public class RollingAverageFilter {
	
	/** The data. */
	double [] data;
	
	/** The index. */
	int index = 0;
	
	/** The average. */
	double average = 0;
	
	/**
	 * Instantiates a new rolling average filter.
	 *
	 * @param size the size
	 * @param startingValue the starting value
	 */
	public RollingAverageFilter(int size, double startingValue){
		data = new double[size];
		average = startingValue*size;
		for(int i=0;i<size;i++){
			data[i]=startingValue;
		}
	}
	
	/**
	 * Adds the.
	 *
	 * @param value the value
	 * @return the double
	 */
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
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public double getValue(){
		return average/data.length;
	}

}
