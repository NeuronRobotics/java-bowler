package com.neuronrobotics.replicator.driver;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Enum BowlerBoardKinematicModel.
 */
public enum BowlerBoardKinematicModel {
	/** The STATUS. */
	Delta(0x00),
	
	/** The GET. */
	LinearBox(0x01),
	
	/** The POST. */
	FrogLegScara(0x02);

	/** The Constant lookup. */
	private static final Map<Integer,BowlerBoardKinematicModel> lookup = new HashMap<Integer,BowlerBoardKinematicModel>();
	
	static {
		for(BowlerBoardKinematicModel cm : EnumSet.allOf(BowlerBoardKinematicModel.class)) {
			lookup.put(cm.getValue(), cm);
		}
	}
	
	/** The value. */
	private int value;
	
	/**
	 * Instantiates a new bowler method.
	 *
	 * @param val the val
	 */
	private BowlerBoardKinematicModel(int val) {
		value = val;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return value; 
	}

    /**
     * Gets the.
     *
     * @param args the code
     * @return the bowler method
     */
    public static BowlerBoardKinematicModel get(int args) { 
    	return lookup.get(args); 
    }
	
}
