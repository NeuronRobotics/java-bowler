package com.neuronrobotics.sdk.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
@SuppressWarnings("resource") 
public class DMDevice extends NonBowlerDevice {
	private Object wrapped = null;
	Method methodConnect = null;
	Method methodDisconnect = null;
	boolean hasGetName = false;
	boolean hasIsAvailible=false;
	Method methodGetName = null;
	Method isAvaibleMeth=null;

	public DMDevice(Object o) throws NoSuchMethodException, SecurityException {
		if(!wrappable(o))
			throw new RuntimeException("This object is not wrappable! ");
		setWrapped(o);
		methodConnect = getWrapped().getClass().getMethod("connect",(Class<?>) null);
		methodDisconnect = getWrapped().getClass().getMethod("disconnect",(Class<?>) null);
		hasGetName = methodExists(getWrapped(), "getName");
		hasIsAvailible = methodExists(getWrapped(), "isAvailable");
		methodGetName = null;
	}

	@Override
	public String getScriptingName() {

		if (hasGetName) {
			if (methodGetName == null)
				try {
					methodGetName = getWrapped().getClass().getMethod("getName",(Class<?>) null);
					
				} catch (Exception e) {
					return super.getScriptingName();
				}
		} else {
			return super.getScriptingName();
		}
		if (methodGetName == null)
			return super.getScriptingName();
		try {
			super.setScriptingName( (String) methodGetName.invoke(getWrapped(),(Class<?>) null));
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
			methodDisconnect.invoke(getWrapped(), (Class<?>)null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Determines if the device is available.
	 *
	 * @return true if the device is avaiable, false if it is not
	 * @throws InvalidConnectionException the invalid connection exception
	 */
	@Override
	public boolean isAvailable() throws InvalidConnectionException{
		if(hasIsAvailible) {
			if(isAvaibleMeth==null) {
				try {
					isAvaibleMeth = getWrapped().getClass().getMethod("isAvailable",(Class<?>) null);	
				} catch (Exception e) {
					//true
				}
			}
			try {
				return (boolean) isAvaibleMeth.invoke(getWrapped(), (Class<?>)null);
			} catch (Exception e) {
				//true
			}
		}
		return true;
	}

	@Override
	public boolean connectDeviceImp() {
		try {
			Object value = methodConnect.invoke(getWrapped(), (Class<?>)null);
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
		for (Method method : clazz.getClass().getMethods()) {
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
