package com.neuronrobotics.sdk.dyio.peripherals;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.dyio.IDyIOChannel;


public class PPMReaderChannel  extends DyIOAbstractPeripheral implements IChannelEventListener{
	private static final DyIOChannelMode myMode = DyIOChannelMode.PPM_IN;
	public PPMReaderChannel(IDyIOChannel channel) {
		super(channel);
		if(!getChannel().canBeMode(myMode)) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + myMode +  " mode");
		}
		if(!setMode(myMode)) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + myMode +  " mode");
		}
		getChannel().addChannelEventListener(this);
	}
	
	ArrayList<IPPMReaderListener> listeners = new 	ArrayList<IPPMReaderListener> ();
	void addPPMReaderListener(IPPMReaderListener l) {
		if(!listeners.contains(l))
			listeners.add(l);
	}
	@Override
	public void onChannelEvent(DyIOChannelEvent e) {
		int [] values= new int[6];
		for(int i=0;i<values.length;i++) {
			values[i] = e.getData().getUnsigned(i+1);
		}
		for (IPPMReaderListener l:listeners) {
			l.onPPMPacket(values);
		}
	}
}
