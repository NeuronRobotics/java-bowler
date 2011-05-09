package com.neuronrobotics.sdk.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class SDKBuildInfo {
	public static String getVersion(){
		String s=getTag("app.version");
		if(s==null)
			s="0.0.0";
		return s;
	}
	public static int getMajorVersion() {
		return getBuildInfo()[0];
	}

	public static int getMinorVersion() {
		return getBuildInfo()[1];
	}

	public static int getBuild() {
		return getBuildInfo()[2];
	}
	public static int[] getBuildInfo(){
		String s = getVersion();
		String [] splits=s.split(".");
		int [] rev = new int[3];
		for(int i=0;i<3;i++){
			rev[i]=new Integer(splits[i]);
		}
		return rev;
	}
	
	private static String getTag(String target){
		String s="";
		InputStream is = getBuildPropertiesStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while (null != (line = br.readLine())) {
			     s+=line+"\n";
			}
		} catch (IOException e) {
		}
		String [] splitAll = s.split("\n");
		for(int i=0;i<splitAll.length;i++){
			if(splitAll[i].contains(target)){
				String [] split = splitAll[i].split("=");
				return split[1];
			}
		}
		return null;
	}
	private static InputStream getBuildPropertiesStream() {
		return SDKBuildInfo.class.getResourceAsStream("build.properties");
	}
}
