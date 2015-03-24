package com.neuronrobotics.sdk.addons.walker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BasicWalkerConfig {
	
	public static String getDefaultConfiguration() {
		String s="";
		InputStream is = getDefaultConfigurationStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while (null != (line = br.readLine())) {
			     s+=line+"\n";
			}
		} catch (IOException e) {
		}
		return s;
	}
	public static InputStream getDefaultConfigurationStream() {
		return BasicWalkerConfig.class.getResourceAsStream("miniHexapod.xml");
	}
}
