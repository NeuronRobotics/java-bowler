package com.neuronrobotics.sdk.dyio.peripherals;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.io.GetValueCommand;
import com.neuronrobotics.sdk.commands.bcs.io.SetChannelValueCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.dyio.IDyIOChannel;

/**
 * This class is a wrapper for the DyIO PPM signal reader.
 * This manages taking Channel 23 and using it to read values from the VEX rc controller (others might be supported as well)
 * @author hephaestus
 *
 */
public class PPMReaderChannel  extends DyIOAbstractPeripheral implements IChannelEventListener{
	private static final DyIOChannelMode myMode = DyIOChannelMode.PPM_IN;

	private int [] crossLinks =null;
	int [] values=null;
	public static final int NO_CROSSLINK = 0xff;
	/**
	 * Void constructor assumes you are suing the DyIORegestry and channel 23
	 */
	public PPMReaderChannel() {
		this(DyIORegestry.get().getChannel(23));
	}
	/**
	 * Takes a DyIO channel which must be channel 23
	 * @param channel
	 */
	public PPMReaderChannel(DyIOChannel channel) {
		super(channel,myMode,true);
		if(!getChannel().canBeMode(myMode)) {
			throw new DyIOPeripheralException("Could not ever be " + channel + " to " + myMode +  " mode");
		}
		if(!setMode()) {
			throw new DyIOPeripheralException("Could not set channel " + channel + " to " + myMode +  " mode");
		}
		getChannel().addChannelEventListener(this);
	}
	/**
	 * Shut down the internal corss link system
	 */
	public void stopAllCrossLinks(){
		if(crossLinks == null)
			crossLinks = new int[6];
		for(int i=0;i<crossLinks.length;i++) {
			crossLinks[i] = NO_CROSSLINK;
		}
		setCrossLink(crossLinks);
	}
	/**
	 * This sets up the PPM cross link. For each PPM channel you can assign it one DyIO output channel as its direct
	 * control. Indecies in the array corospond to the PPM channel, and values corospond to the DyIO output channel
	 * @param links an array of channel numbers corosponding to the PPM channel to do a direct 1:1 mapping
	 */
	public void setCrossLink(int [] links){
		if(links.length != 6)
			throw new IndexOutOfBoundsException("Array of cross links must be of legnth 6");
		if(crossLinks == null){
			throw new RuntimeException("Must get cross link state before setting a new one");
		}
		//System.out.print("\nSetting cross link map: [");
		for(int i=0;i<crossLinks.length;i++) {
			crossLinks[i] = links[i];
			//System.out.print(" "+crossLinks[i]);
		}
		//System.out.print("]");
		getChannel().getDevice().send(new SetChannelValueCommand(23,crossLinks,myMode));
	}
	/**
	 * Request the cross link map
	 * @return an array of integers corosponding to the cross linking for the DyIO outputs
	 */
	public int [] getCrossLink(){
		if(crossLinks == null){
			updateValues();
		}
		//System.out.print("\nGetting cross link map: [");
		for(int i=0;i<crossLinks.length;i++) {
			System.out.print(" "+crossLinks[i]);
		}
		System.out.print("]");
		return crossLinks;
	}
	/**
	 * Get the current state of the PPM reader
	 * @return the values of the current state of the PPM channels
	 */
	public int [] getValues(){
		if(values == null){
			updateValues();
		}
		return values;
	}
	
	private void updateValues() {
		BowlerDatagram b=null;
		//System.out.println("Updating value map");
		try {
			b= getChannel().getDevice().send(new GetValueCommand(23));
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(b != null) {
			crossLinks = new int[6];
			values= new int[6];
			for(int i=0;i<values.length;i++) {
				values[i] = b.getData().getUnsigned(1+i);
			}
			for(int i=0;i<crossLinks.length;i++) {
				crossLinks[i] = b.getData().getUnsigned(1+6+i);
			}
		}
	}
	
	ArrayList<IPPMReaderListener> listeners = new 	ArrayList<IPPMReaderListener> ();
	/**
	 * Add a PPM reader listener
	 * @param l the IPPMReaderListener to add
	 */
	public void addPPMReaderListener(IPPMReaderListener l) {
		if(!listeners.contains(l))
			listeners.add(l);
	}
	
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
	
	public boolean hasAsync() {
		return true;
	}
}
