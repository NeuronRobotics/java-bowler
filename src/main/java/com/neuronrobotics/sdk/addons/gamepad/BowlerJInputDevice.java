package com.neuronrobotics.sdk.addons.gamepad;

import java.util.ArrayList;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class BowlerJInputDevice.
 */
public class BowlerJInputDevice extends NonBowlerDevice {
	
	/** The controller. */
	private Controller controller;
	
	/** The listeners. */
	private ArrayList<IJInputEventListener>  listeners  = new ArrayList<IJInputEventListener>(); 
	
	/** The run. */
	boolean run=true;
	
	/** The poller. */
	private Thread poller;

	/**
	 * Instantiates a new bowler j input device.
	 *
	 * @param controller the controller
	 */
	public BowlerJInputDevice(Controller controller){
		if(controller!=null)
			this.setController(controller);
		else
			throw new RuntimeException("Contoller must not be null");
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#disconnectDeviceImp()
	 */
	@Override
	public void disconnectDeviceImp() {
		listeners.clear();
		poller = null;
		run=false;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#connectDeviceImp()
	 */
	@Override
	public boolean connectDeviceImp() {
		if(poller == null){
			poller = new Thread(){
				public void run(){
					setName("Game Controller Poll thread");
					Log.warning("Starting game Pad Poller");
					while(run){
						controller.poll();
						EventQueue queue = controller.getEventQueue();
						Event event = new Event();
						while(queue.getNextEvent(event) && run) {
				               StringBuffer buffer = new StringBuffer(controller.getName());
				               buffer.append(" at ");
				               buffer.append(event.getNanos()).append(", ");
				               Component comp = event.getComponent();
				               buffer.append(comp.getName()).append(" changed to ");
				               float value = event.getValue(); 
				               if(comp.isAnalog()) {
				                  buffer.append(value);
				               } else {
				                  if(value>0) {
				                     buffer.append("On");
				                  } else {
				                     buffer.append("Off");
				                  }
				               }
				               Log.info(buffer.toString());
				               for(int i=0;i<listeners.size();i++){
				            	   IJInputEventListener l = listeners.get(i);
				            	   try{
				            		   l.onEvent(comp, event, value, buffer.toString());
				            	   }catch(Exception ex){
				            		   ex.printStackTrace();
				            	   }
				               }
				        }
						ThreadUtil.wait(10);
					}
				}
			};
			poller.start();
		}
		return true;
	}

	/**
	 * Gets the controller.
	 *
	 * @return the controller
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * Sets the controller.
	 *
	 * @param controller the new controller
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	

	/**
	 * Removes the listeners.
	 *
	 * @param l the l
	 */
	public void removeListeners(IJInputEventListener l) {
		if(listeners.contains(l))
			this.listeners.remove(l);
	}

	/**
	 * Adds the listeners.
	 *
	 * @param l the l
	 */
	public void addListeners(IJInputEventListener l) {
		if(!listeners.contains(l))
			this.listeners.add(l);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#getNamespacesImp()
	 */
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

}
