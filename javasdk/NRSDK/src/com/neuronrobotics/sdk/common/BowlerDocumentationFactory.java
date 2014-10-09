package com.neuronrobotics.sdk.common;

import java.net.URI;
import java.net.URISyntaxException;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.CounterInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.PPMReaderChannel;
import com.neuronrobotics.sdk.dyio.peripherals.SPIChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.dyio.peripherals.UARTChannel;

/**
 * Factory used to centralize references to web pages (specifically
 * documentation). Any documentation for an object type defined in the NRSDK
 * will be found here. (Hint: if the URI refers to an object type defined in the
 * NRSDK, it goes here).
 * 
 * See also: NRConsoleDocumentationFactory.java
 * 
 */
public class BowlerDocumentationFactory {

	public static URI getDocumentationURL(Object input) {

		if (input instanceof DigitalInputChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/Digital_Input_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}

		} else if (input instanceof AnalogInputChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/Analog_Input_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof CounterInputChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/Counter_Input_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof CounterOutputChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/Counter_Output_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof DigitalOutputChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/Digital_Output_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof PPMReaderChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/PPM_Reader_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof ServoChannel) {

			try {
				return new URI("http://wiki.neuronrobotics.com/Servo_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof SPIChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/SPI_Passthrough_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof UARTChannel) {

			try {
				return new URI(
						"http://wiki.neuronrobotics.com/UART_Passthrough_Channel");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		}

		throw new RuntimeException("No documentation for object of type "
				+ input.getClass());

	}
}
