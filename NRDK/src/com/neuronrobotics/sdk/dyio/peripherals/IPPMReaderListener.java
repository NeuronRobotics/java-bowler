package com.neuronrobotics.sdk.dyio.peripherals;

//import com.neuronrobotics.sdk.dyio.IChannelEventListener;

public interface IPPMReaderListener{
	void onPPMPacket(int [] values);
}
