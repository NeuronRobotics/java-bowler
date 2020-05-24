package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// TODO: Auto-generated Javadoc
/**
 * The Enum LinkType.
 */
public enum LinkType {
	
	/** The servo rotory. */
	SERVO_ROTORY("servo-rotory"), 
	
	/** The servo tool. */
	SERVO_TOOL("servo-tool"), 
	
	/** The servo prismatic. */
	SERVO_PRISMATIC("servo-prismatic"), 
	
	/** The pid. */
	PID("pid"),
	
	/** The pid tool. */
	PID_TOOL("pid-tool"), 
	
	/** The pid prismatic. */
	PID_PRISMATIC("pid-prismatic"), 
	
	/** The analog rotory. */
	ANALOG_ROTORY("analog-rotory"),
	
	/** The analog prismatic. */
	ANALOG_PRISMATIC("analog-prismatic"),
	
	/** The dummy. */
	DUMMY("dummy"),
	
	/** The virtual. */
	VIRTUAL("virtual"),
	
	/** The stepper rotory. */
	STEPPER_ROTORY("stepper-rotory"), 
	
	/** The stepper tool. */
	STEPPER_TOOL("stepper-tool"), 

	/** The stepper prismatic. */
	STEPPER_PRISMATIC("stepper-prismatic"),
	
	/** The stepper rotory. */
	GCODE_STEPPER_ROTORY("gcode-stepper-rotory"), 
	/** The stepper tool. */
	GCODE_STEPPER_TOOL("gcode-stepper-tool"), 
	/** The stepper tool. */
	GCODE_STEPPER_PRISMATIC("gcode-stepper-prismatic"), 
	/** The stepper tool. */
	GCODE_HEATER_TOOL("gcode-heater-tool"), 
	
	/** Camera */
	CAMERA("camera"),
	/** Camera */
	USERDEFINED(null);
	
	 /** The name. */
 	private final String name;
	 
	 /** The Constant map. */
 	private static final Map<String, LinkType> map =
	                             new HashMap<String, LinkType>();
	 
	 static {
	   for (LinkType type : LinkType.values()) {
	     map.put(type.name, type);
	   }
	 }
	 
	 public static ArrayList<String> getUserDefined(){
		 ArrayList<String> back = new ArrayList<String>();
		 for(String s:map.keySet()) {
			 if(map.get(s)==USERDEFINED) {
				 back.add(s);
			 }
		 }
		 return back;
		 
	 }
	 /**
	  * Only classes in this package should add types, and only from LinkFactory
	  * @param type a new type name to regester as user defined
	  */
	 public static void addType(String type){
		 map.put(type, USERDEFINED);
	 }
	 
	 /**
 	 * Instantiates a new link type.
 	 *
 	 * @param name the name
 	 */
 	private LinkType(String name) {
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
 	 * @return the link type
 	 */
 	public static LinkType fromString(String name) {
	   if (map.containsKey(name)) {
	     return map.get(name);
	   }
	   throw new NoSuchElementException(name + " not found");
	 }
	 

	 
	 
	 /* (non-Javadoc)
 	 * @see java.lang.Enum#toString()
 	 */
 	@Override
	 public String toString(){
		 return name;
	 }
}
