package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.imageprovider.AbstractImageProvider;

import javafx.scene.transform.Affine;

public class CameraLink extends AbstractLink {

	private AbstractImageProvider img;

	public CameraLink(LinkConfiguration conf, AbstractImageProvider img) {
		super(conf);
		// TODO Auto-generated constructor stub
		this.setImageProvider(img);
	}
	
	
	@Override
	public void setGlobalPositionListener(Affine affine) {
		super.setGlobalPositionListener(affine);
		img.setGlobalPositionListener(affine);
	}

	@Override
	public void cacheTargetValueDevice() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushDevice(double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushAllDevice(double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 0;
	}



	public AbstractImageProvider getImageProvider() {
		return img;
	}



	public void setImageProvider(AbstractImageProvider img) {
		this.img = img;
	}

}
