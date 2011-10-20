package com.neuronrobotics.addons.driving.virtual;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ObsticleType {

	LINE(Color.black),

	WALL(Color.blue),
	
	FIRE(Color.orange),
	
	NONE(Color.white);
	
	/** The Constant lookup. */
	private static final Map<Color,ObsticleType > lookup = new HashMap<Color,ObsticleType >();
	
	static {
		for(ObsticleType cm : EnumSet.allOf(ObsticleType.class)) {
			lookup.put(cm.getValue(), cm);
		}
	}
	
	/** The value. */
	private Color value;
	
	/**
	 * Instantiates a new bowler method.
	 *
	 * @param val the val
	 */
	private ObsticleType(Color c) {
		value = c;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Color getValue() {
		return value; 
	}

    /**
     * Gets the.
     *
     * @param code the code
     * @return the bowler method
     */
    public static ObsticleType get(Color c) { 
    	ObsticleType ot = lookup.get(c);
    	if(ot==null)
    		ot=ObsticleType.NONE;
    	return ot; 
    }

	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		String s="NOT VALID";
		switch (this){
		case LINE:
			return "On The Line";
		case WALL:
			return "In The Wall";
		case FIRE:
			return "In The Fire";
		case NONE:
			return "No Obsticle";
		}
		return s;
	}
}
