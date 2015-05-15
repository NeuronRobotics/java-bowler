package com.neuronrobotics.sdk.addons.gamepad;

import net.java.games.input.Component;
import net.java.games.input.Event;

public interface IJInputEventListener {

	public void onEvent(Component comp,Event event,float value,String eventString);
}
