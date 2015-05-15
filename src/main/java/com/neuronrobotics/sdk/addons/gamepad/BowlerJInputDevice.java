package com.neuronrobotics.sdk.addons.gamepad;

import net.java.games.input.Controller;

import com.neuronrobotics.sdk.common.NonBowlerDevice;

public class BowlerJInputDevice extends NonBowlerDevice {
	
	private Controller controller;

	public BowlerJInputDevice(Controller controller){
		this.setController(controller);
		
	}

	@Override
	public void disconnectDeviceImp() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean connectDeviceImp() {
		// TODO Auto-generated method stub
		return false;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}
