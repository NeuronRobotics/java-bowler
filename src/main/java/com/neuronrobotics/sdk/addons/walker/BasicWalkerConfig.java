package com.neuronrobotics.sdk.addons.walker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicWalkerConfig.
 */
public class BasicWalkerConfig {
	
	/**
	 * Gets the default configuration.
	 *
	 * @return the default configuration
	 */
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
	
	/**
	 * Gets the default configuration stream.
	 *
	 * @return the default configuration stream
	 */
	public static InputStream getDefaultConfigurationStream() {
		return BasicWalkerConfig.class.getResourceAsStream("miniHexapod.xml");
	}
}
