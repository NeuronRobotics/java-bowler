package com.neuronrobotics.sdk.addons.kinematics;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// TODO: Auto-generated Javadoc
/**
 * The Enum DrivingType.
 */
public enum DrivingType {
	
	/** The none. */
	NONE("none"),
	
	/** The walking. */
	WALKING("walking"), 
	
	/** The driving. */
	DRIVING("driving");
	
	 /** The name. */
 	private final String name;
	 
	 /** The Constant map. */
 	private static final Map<String, DrivingType> map =
	                             new HashMap<String, DrivingType>();
	 
	 static {
	   for (DrivingType type : DrivingType.values()) {
	     map.put(type.name, type);
	   }
	 }
	 
	 /**
 	 * Instantiates a new driving type.
 	 *
 	 * @param name the name
 	 */
 	private DrivingType(String name) {
	   this.name = name;
	 }
	 
	 /**
 	 * Gets the name.
 	 *
 	 * @return the name
 	 */
 	public String getName() {
	   return name;
	 }
	 
	 /**
 	 * From string.
 	 *
 	 * @param name the name
 	 * @return the driving type
 	 */
 	public static DrivingType fromString(String name) {
	   if (map.containsKey(name)) {
	     return map.get(name);
	   }
	   return NONE;
	 }
	 
	 
	 /* (non-Javadoc)
 	 * @see java.lang.Enum#toString()
 	 */
 	@Override
	 public String toString(){
		 return name;
	 }
}
