package com.neuronrobotics.sdk.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class DMDevice extends NonBowlerDevice {
	private Object wrapped = null;
	Method methodConnect = null;
	Method methodDisconnect = null;
	boolean hasGetName = false;
	Method methodGetName = null;

	public DMDevice(Object o) throws NoSuchMethodException, SecurityException {
		if(!wrappable(o))
			throw new RuntimeException("This object is not wrappable! ");
		setWrapped(o);
		methodConnect = getWrapped().getClass().getDeclaredMethod("connect", null);
		methodDisconnect = getWrapped().getClass().getDeclaredMethod("disconnect", null);
		hasGetName = methodExists(getWrapped(), "getName");
		methodGetName = null;
	}

	@Override
	public String getScriptingName() {

		if (hasGetName) {
			if (methodGetName == null)
				try {
					methodGetName = getWrapped().getClass().getDeclaredMethod("getName", null);
					
				} catch (Exception e) {
					return super.getScriptingName();
				}
		} else {
			return super.getScriptingName();
		}
		if (methodGetName == null)
			return super.getScriptingName();
		try {
			super.setScriptingName( (String) methodGetName.invoke(getWrapped(), null));
		} catch (Exception e) {
			return super.getScriptingName();
		}
		return super.getScriptingName();
	}

	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	@Override
	public void disconnectDeviceImp() {
		try {
			methodDisconnect.invoke(getWrapped(), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean connectDeviceImp() {
		try {
			Object value = methodConnect.invoke(getWrapped(), null);
			try {
				return (Boolean) value;
			} catch (Exception e) {

			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static boolean wrappable(Object o) {
		if(o==null)
			return false;
		return methodExists(o, "connect") &&
				   methodExists(o, "disconnect");
		
	}
	public static boolean methodExists(Object clazz, String methodName) {
		for (Method method : clazz.getClass().getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				return true;
			}
		}
		return false;
	}

	public Object getWrapped() {
		return wrapped;
	}

	public void setWrapped(Object wrapped) {
		this.wrapped = wrapped;
	}

}
