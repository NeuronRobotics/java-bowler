package com.neuronrobotics.sdk.dyio.peripherals;

// TODO: Auto-generated Javadoc
//import com.neuronrobotics.sdk.dyio.IChannelEventListener;

/**
 * The listener interface for receiving IPPMReader events.
 * The class that is interested in processing a IPPMReader
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIPPMReaderListener  method. When
 * the IPPMReader event occurs, that object's appropriate
 * method is invoked.
 *

 */
public interface IPPMReaderListener{
	
	/**
	 * On ppm packet.
	 *
	 * @param values the values
	 */
	void onPPMPacket(int [] values);
}
