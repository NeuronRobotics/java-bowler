package com.neuronrobotics.sdk.addons.kinematics.gui;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class SimpleTransformViewer extends Canvas3D {
	/**
	 * long 
	 */
	private static final long serialVersionUID = -5190028697311178389L;
	SimpleUniverse simpleU ;
	private Transform3D baseLocation = null;
	BranchGroup rootBranchGroup= new BranchGroup();
	BranchGroup base= new BranchGroup();
	public SimpleTransformViewer() {
		super( SimpleUniverse.getPreferredConfiguration());
        resetView();
	}
	public void createDefaultSceneGraph() {
		
		if(simpleU!=null){
			simpleU.removeAllLocales();
			simpleU.cleanup();
		}else
			simpleU = new SimpleUniverse(this);
    	// Create the root of the branch graph
		rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		rootBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		base.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		base.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		base.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		base.setCapability(BranchGroup.ALLOW_DETACH);
		
		
		rootBranchGroup.addChild(new Axis());
		rootBranchGroup.addChild(base);
    	
    	
        BoundingSphere mouseBounds = null;

        TransformGroup  vpTrans = simpleU.getViewingPlatform().getViewPlatformTransform();

        mouseBounds = new BoundingSphere(new Point3d(), 100.0);

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(new TransformGroup(getBaseLocation()));
        myMouseRotate.setSchedulingBounds(mouseBounds);

        MouseTranslate myMouseTranslate = new MouseTranslate(MouseBehavior.INVERT_INPUT);
        myMouseTranslate.setTransformGroup(vpTrans);
        myMouseTranslate.setSchedulingBounds(mouseBounds);
        rootBranchGroup.addChild(myMouseTranslate);
        //base.addChild(myMouseTranslate);

        MouseWheelZoom myMouseZoom = new MouseWheelZoom(MouseBehavior.INVERT_INPUT);
        myMouseZoom.setTransformGroup(vpTrans);
        myMouseZoom.setSchedulingBounds(mouseBounds);
        rootBranchGroup.addChild(myMouseZoom);

    	// Let Java 3D perform optimizations on this scene graph.
    	rootBranchGroup.compile();
    	simpleU.addBranchGraph(rootBranchGroup);
	} // end of CreateSceneGraph method of HelloJava3Db
	
	public TransformGroup addTransform(TransformNR tr,String label) {
		Transform3D trans = TransformFactory.getTransform(tr);
		return addTransform(trans,label);
	}
	public TransformGroup addTransform(Transform3D tr,String label) {
		//System.out.println("Adding a transform "+label);
		TransformGroup tmp = TransformFactory.getLabledAxis(tr, label);
		BranchGroup child = new BranchGroup();
		child.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		child.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		child.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		child.setCapability(BranchGroup.ALLOW_DETACH);
		child.addChild(tmp);
		base.addChild(child);
		return tmp;
	}
	
	public void resetView(){
		createDefaultSceneGraph();
		clearTransforms();
		//System.out.println("Resetting view");
        TransformGroup viewTransform = simpleU.getViewingPlatform().getViewPlatformTransform();
        
        Transform3D t3d = new Transform3D();        
        t3d.lookAt(new Point3d(20,20,20),//Position of camera
        		new Point3d(0,0,0), //position of base frame
        		new Vector3d(0,1,0)); // orentation of camera
        t3d.invert();
        
        viewTransform.setTransform(t3d);
	}
	public Transform3D getBaseLocation() {
		if(baseLocation == null) {
			baseLocation = new Transform3D();
			Transform3D x = new Transform3D();
			x.rotX(Math.toRadians(-90));
			Transform3D z = new Transform3D();
			z.rotZ(Math.toRadians(180));
			baseLocation.mul(x);
			baseLocation.mul(z);
		}
		return baseLocation;
	}
	public void clearTransforms() {
		base.removeAllChildren();
		addTransform(getBaseLocation() , "Base");
	}

}
