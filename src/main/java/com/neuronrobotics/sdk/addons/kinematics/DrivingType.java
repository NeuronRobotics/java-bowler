package com.neuronrobotics.sdk.addons.kinematics;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum DrivingType {
	NONE("none"),
	WALKING("walking"), 
	DRIVING("driving");
	
	 private final String name;
	 
	 private static final Map<String, DrivingType> map =
	                             new HashMap<String, DrivingType>();
	 
	 static {
	   for (DrivingType type : DrivingType.values()) {
	     map.put(type.name, type);
	   }
	 }
	 
	 private DrivingType(String name) {
	   this.name = name;
	 }
	 
	 public String getName() {
	   return name;
	 }
	 
	 public static DrivingType fromString(String name) {
	   if (map.containsKey(name)) {
	     return map.get(name);
	   }
	   throw new NoSuchElementException(name + "not found");
	 }
	 
	 
	 @Override
	 public String toString(){
		 return name;
	 }
}
