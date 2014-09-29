package com.neuronrobotics.sdk.common;

import java.net.URI;
import java.net.URISyntaxException;

import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;

public class BowlerDocumentationFactory {

	public static URI getDocumentationURL(Object input){
		
		if(input instanceof DigitalInputChannel){

				try {
					return new URI("http://wiki.neuronrobotics.com/Digital_Input_Channel");
				} catch (URISyntaxException e) {
					Log.error(e.getMessage());
				}

		}
		
		throw new RuntimeException("No documentation for object of type "+ input.getClass());
		
	}
}
