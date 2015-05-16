package com.neuronrobotics.sdk.addons.gamepad;

import java.util.ArrayList;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class BowlerJInputDevice extends NonBowlerDevice {
	
	private Controller controller;
	private ArrayList<IJInputEventListener>  listeners  = new ArrayList<IJInputEventListener>(); 
	
	boolean run=true;
	private Thread poller;

	public BowlerJInputDevice(Controller controller){
		if(controller!=null)
			this.setController(controller);
		else
			throw new RuntimeException("Contoller must not be null");
	}

	@Override
	public void disconnectDeviceImp() {
		listeners.clear();
		run=false;
	}

	@Override
	public boolean connectDeviceImp() {
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
			                  if(value==1.0f) {
			                     buffer.append("On");
			                  } else {
			                     buffer.append("Off");
			                  }
			               }
			               Log.warning(buffer.toString());
			               for(IJInputEventListener l:listeners){
			            	   l.onEvent(comp, event, value, buffer.toString());
			               }
			        }
					ThreadUtil.wait(10);
				}
			}
		};
		poller.start();
		return true;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	

	public void removeListeners(IJInputEventListener l) {
		if(listeners.contains(l))
			this.listeners.remove(l);
	}

	public void addListeners(IJInputEventListener l) {
		if(!listeners.contains(l))
			this.listeners.add(l);
	}
	
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

}
