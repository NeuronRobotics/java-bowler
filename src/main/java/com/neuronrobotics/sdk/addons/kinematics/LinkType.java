package com.neuronrobotics.sdk.addons.kinematics;

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
	
	/** Camera */
	CAMERA("camera");
	
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
	   throw new NoSuchElementException(name + "not found");
	 }
	 
	 /**
 	 * Checks if is virtual.
 	 *
 	 * @return true, if is virtual
 	 */
 	public boolean isVirtual(){
		 switch(this){

		case DUMMY:
		case VIRTUAL:
			return true;
		default:
			return false;
		 }
	 }
	 
	 /**
 	 * Checks if is tool.
 	 *
 	 * @return true, if is tool
 	 */
 	public boolean isTool(){
		 switch(this){
		case SERVO_TOOL:
		case STEPPER_TOOL:
		case PID_TOOL:
			return true;
		default:
			return false;
		 
		 } 
	 }
	 
	 /**
 	 * Checks if is prismatic.
 	 *
 	 * @return true, if is prismatic
 	 */
 	public boolean isPrismatic(){
		 switch(this){
		case ANALOG_PRISMATIC:
		case PID_PRISMATIC:
		case SERVO_PRISMATIC:
		case STEPPER_PRISMATIC:
			return true;
		default:
			return false;

		 } 
	 }
	 
	 
	 /* (non-Javadoc)
 	 * @see java.lang.Enum#toString()
 	 */
 	@Override
	 public String toString(){
		 return name;
	 }
}
