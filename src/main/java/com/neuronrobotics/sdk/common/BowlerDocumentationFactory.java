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

// TODO: Auto-generated Javadoc
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

	/**
	 * Gets the documentation url.
	 *
	 * @param input the input
	 * @return the documentation url
	 */
	public static URI getDocumentationURL(Object input) {
		String basURL = "http://neuronrobotics.github.io/Java-Code-Library/";
		if (input instanceof DigitalInputChannel) {

			try {
				return new URI(
						basURL+"Digital-Input-Example-Simple/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}

		} else if (input instanceof AnalogInputChannel) {

			try {
				return new URI(
						basURL+"Analog-Input-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof CounterInputChannel) {

			try {
				return new URI(
						basURL+"Counter-Input-Quadrature-Encoder-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof CounterOutputChannel) {

			try {
				return new URI(
						basURL+"Counter-Output-Stepper-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof DigitalOutputChannel) {

			try {
				return new URI(
						basURL+"Digital-Output-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof PPMReaderChannel) {

			try {
				return new URI(
						basURL+"PPM-RC-Signal-Reader-Channle-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof ServoChannel) {

			try {
				return new URI(basURL+"Servo-Output-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof SPIChannel) {

			try {
				return new URI(
						basURL+"SPI-Channel-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		} else if (input instanceof UARTChannel) {

			try {
				return new URI(
						basURL+"USART-Channel-Example/");
			} catch (URISyntaxException e) {
				Log.error(e.getMessage());
			}
		}

		throw new RuntimeException("No documentation for object of type "
				+ input.getClass());

	}
}
