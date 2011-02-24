package com.neuronrobotics.sdk.dyio.peripherals;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.io.GetValueCommand;
import com.neuronrobotics.sdk.commands.bcs.io.SetChannelValueCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.dyio.IDyIOChannel;


public class PPMReaderChannel  extends DyIOAbstractPeripheral implements IChannelEventListener{
	private static final DyIOChannelMode myMode = DyIOChannelMode.PPM_IN;

	private int [] crossLinks =null;
	int [] values=null;
	public static final int NO_CROSSLINK = 0xff;
	
	public PPMReaderChannel(IDyIOChannel channel) {
		super(channel);
		if(!getChannel().canBeMode(myMode)) {
			throw new DyIOPeripheralException("Could not ever be " + channel + " to " + myMode +  " mode");
		}
		if(!setMode(myMode)) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + myMode +  " mode");
		}
		getChannel().addChannelEventListener(this);
	}
	public void stopAllCrossLinks(){
		if(crossLinks == null)
			crossLinks = new int[6];
		for(int i=0;i<crossLinks.length;i++) {
			crossLinks[i] = NO_CROSSLINK;
		}
		setCrossLink(crossLinks);
	}
	public void setCrossLink(int [] links){
		if(links.length != 6)
			throw new IndexOutOfBoundsException("Array of cross links must be of legnth 6");
		if(crossLinks == null){
			throw new RuntimeException("Must get cross link state before setting a new one");
		}
		System.out.print("\nSetting cross link map: [");
		for(int i=0;i<crossLinks.length;i++) {
			crossLinks[i] = links[i];
			System.out.print(" , "+crossLinks[i]);
		}
		System.out.print("]");
		getChannel().getDevice().send(new SetChannelValueCommand(23,crossLinks,myMode));
	}
	
	public int [] getCrossLink(){
		if(crossLinks == null){
			crossLinks = new int[6];
			for(int i=0;i<crossLinks.length;i++) {
				crossLinks[i] = NO_CROSSLINK;
			}
		}
		return crossLinks;
	}
	public int [] getValues(){
		if(values == null){
			values= new int[6];
			for(int i=0;i<values.length;i++) {
				values[i] = 127;
			}
		}
		return values;
	}
	
	ArrayList<IPPMReaderListener> listeners = new 	ArrayList<IPPMReaderListener> ();
	public void addPPMReaderListener(IPPMReaderListener l) {
		if(!listeners.contains(l))
			listeners.add(l);
	}
	@Override
	public void onChannelEvent(DyIOChannelEvent e) {
		getValues();
		if(crossLinks == null){
			crossLinks = new int[6];
			ByteList data =new ByteList( e.getData().getBytes(6, 6));
			for(int i=0;i<crossLinks.length;i++) {
				crossLinks[i] = data.getUnsigned(i);
			}
		}
		for(int i=0;i<values.length;i++) {
			values[i] = e.getData().getUnsigned(i);
		}
		for (IPPMReaderListener l:listeners) {
			l.onPPMPacket(values);
		}
	}
}
