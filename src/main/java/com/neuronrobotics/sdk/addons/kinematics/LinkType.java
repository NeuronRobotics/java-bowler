package com.neuronrobotics.sdk.addons.kinematics;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum LinkType {
	SERVO_ROTORY("servo-rotory"), 
	SERVO_TOOL("servo-tool"), 
	SERVO_PRISMATIC("servo-prismatic"), 
	PID("pid"),
	PID_TOOL("pid-tool"), 
	PID_PRISMATIC("pid-prismatic"), 
	ANALOG_ROTORY("analog-rotory"),
	ANALOG_PRISMATIC("analog-prismatic"),
	DUMMY("dummy"),
	VIRTUAL("virtual"),
	STEPPER_ROTORY("stepper-rotory"), 
	STEPPER_TOOL("stepper-tool"), 
	STEPPER_PRISMATIC("stepper-prismatic");
	
	 private final String name;
	 
	 private static final Map<String, LinkType> map =
	                             new HashMap<String, LinkType>();
	 
	 static {
	   for (LinkType type : LinkType.values()) {
	     map.put(type.name, type);
	   }
	 }
	 
	 private LinkType(String name) {
	   this.name = name;
	 }
	 
	 public String getName() {
	   return name;
	 }
	 
	 public static LinkType fromString(String name) {
	   if (map.containsKey(name)) {
	     return map.get(name);
	   }
	   throw new NoSuchElementException(name + "not found");
	 }
	 
	 public boolean isVirtual(){
		 switch(this){

		case DUMMY:
		case VIRTUAL:
			return true;
		default:
			return false;
		 }
	 }
	 
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
	 
	 
	 @Override
	 public String toString(){
		 return name;
	 }
}
