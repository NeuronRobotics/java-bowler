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
