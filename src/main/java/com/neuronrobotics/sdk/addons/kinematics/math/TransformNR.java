package com.neuronrobotics.sdk.addons.kinematics.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.neuronrobotics.sdk.common.Log;
import Jama.Matrix;

// TODO: Auto-generated Javadoc
/**
 * The Class TransformNR.
 */
public class TransformNR {
  private ArrayList<ITransformNRChangeListener> listeners=null;
  /** The x. */
  private double x;

  /** The y. */
  private double y;

  /** The z. */
  private double z;

  /** The rotation. */
  private RotationNR rotation;



  /**
   * Instantiates a new transform nr.
   *
   * @param m the m
   */
  public TransformNR(Matrix m) {
    this.setX(m.get(0, 3));
    this.setY(m.get(1, 3));
    this.setZ(m.get(2, 3));
    this.setRotation(new RotationNR(m));
  }

  /**
   * Instantiates a new transform nr.
   *
   * @param x the x
   * @param y the y
   * @param z the z
   * @param w the w
   * @param rotx the rotx
   * @param roty the roty
   * @param rotz the rotz
   */
  public TransformNR(double x, double y, double z, double w, double rotx, double roty,
      double rotz) {
    this.setX(x);
    this.setY(y);
    this.setZ(z);
    this.setRotation(new RotationNR(new double[] {w, rotx, roty, rotz}));
  }

  /**
   * Instantiates a new transform nr.
   *
   * @param cartesianSpaceVector the cartesian space vector
   * @param rotationMatrix the rotation matrix
   */
  public TransformNR(double[] cartesianSpaceVector, double[][] rotationMatrix) {
    this.setX(cartesianSpaceVector[0]);
    this.setY(cartesianSpaceVector[1]);
    this.setZ(cartesianSpaceVector[2]);
    this.setRotation(new RotationNR(rotationMatrix));
  }

  /**
   * Instantiates a new transform nr.
   *
   * @param cartesianSpaceVector the cartesian space vector
   * @param quaternionVector the quaternion vector
   */
  public TransformNR(double[] cartesianSpaceVector, double[] quaternionVector) {
    this.setX(cartesianSpaceVector[0]);
    this.setY(cartesianSpaceVector[1]);
    this.setZ(cartesianSpaceVector[2]);
    this.setRotation(new RotationNR(quaternionVector));
  }

  /**
   * Instantiates a new transform nr.
   *
   * @param x the x
   * @param y the y
   * @param z the z
   * @param q the q
   */
  public TransformNR(double x, double y, double z, RotationNR q) {
    this.setX(x);
    this.setY(y);
    this.setZ(z);
    this.setRotation(q);
  }

  /**
   * Instantiates a new transform nr.
   *
   * @param cartesianSpaceVector the cartesian space vector
   * @param q the q
   */
  public TransformNR(double[] cartesianSpaceVector, RotationNR q) {
    this.setX(cartesianSpaceVector[0]);
    this.setY(cartesianSpaceVector[1]);
    this.setZ(cartesianSpaceVector[2]);
    this.setRotation(q);
  }

  /**
   * Instantiates a new transform nr.
   */
  public TransformNR() {
    this.setX(0);
    this.setY(0);
    this.setZ(0);
    this.setRotation(new RotationNR());
  }

  /**
   * Gets the x.
   *
   * @return the x
   */
  public double getX() {
    return x;
  }

  /**
   * Gets the y.
   *
   * @return the y
   */
  public double getY() {
    return y;
  }

  /**
   * Gets the z.
   *
   * @return the z
   */
  public double getZ() {
    return z;
  }

  /**
   * Gets the rotation matrix array.
   *
   * @return the rotation matrix array
   */
  public double[][] getRotationMatrixArray() {
    return getRotation().getRotationMatrix();
  }

  /**
   * Gets the rotation matrix.
   *
   * @return the rotation matrix
   */
  public RotationNR getRotationMatrix() {
    return getRotation();
  }

  /**
   * Gets the rotation value.
   *
   * @param i the i
   * @param j the j
   * @return the rotation value
   */
  public double getRotationValue(int i, int j) {
    return getRotation().getRotationMatrix()[i][j];
  }

  /**
   * Gets the rotation.
   *
   * @return the rotation
   */
  public RotationNR getRotation() {

    return rotation;
  }

  /**
   * Times.
   *
   * @param t the t
   * @return the transform nr
   */
  public TransformNR times(TransformNR t) {
    return new TransformNR(getMatrixTransform().times(t.getMatrixTransform()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    try {
      return getMatrixString(getMatrixTransform()) + getRotation().toString();
    } catch (Exception ex) {
      return "Transform error" + ex.getLocalizedMessage();
    }
  }
  public String toPositionString() {
	DecimalFormat decimalFormat = new DecimalFormat("000.00");

	  return decimalFormat.format(x)+" "+decimalFormat.format(y)+" "+decimalFormat.format(z);
  }

  /**
   * Gets the matrix string.
   *
   * @param matrix the matrix
   * @return the matrix string
   */
  public static String getMatrixString(Matrix matrix) {

    String s = "{\n";
    double[][] m = matrix.getArray();

	DecimalFormat decimalFormat = new DecimalFormat("000.00");
    int across = m.length;
    int down = m[0].length;

    for (int i = 0; i < across; i++) {
      s += "{ ";
      for (int j = 0; j < down; j++) {
        if (m[i][j] < 0) {
			s += decimalFormat.format(m[i][j]);
		} else
          s += decimalFormat.format(m[i][j]);
        if (j < down - 1)
          s += ",";
        s += "\t";
      }
      s += " }";
      if (i < across - 1)
        s += ",";
      s += "\n";
    }
    return s + "}\n";
  }

  /**
   * Gets the position array.
   *
   * @return the position array
   */
  public double[] getPositionArray() {
    return new double[] {getX(), getY(), getZ()};
  }

  /**
   * Gets the matrix transform.
   *
   * @return the matrix transform
   */
  public Matrix getMatrixTransform() {
    double[][] transform = new double[4][4];
    double[][] rotation = getRotationMatrixArray();


    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        transform[i][j] = rotation[i][j];
      }
    }
    for (int i = 0; i < 3; i++) {
      transform[3][i] = 0;
    }
    transform[3][3] = 1;
    transform[0][3] = getX();
    transform[1][3] = getY();
    transform[2][3] = getZ();


    return new Matrix(transform);
  }

  /**
   * Gets the offset orentation magnitude.
   *
   * @param t the t
   * @return the offset orentation magnitude
   */
  public double getOffsetOrentationMagnitude(TransformNR t) {
    double x = getRotation().getRotationMatrix2QuaturnionX()
        - t.getRotation().getRotationMatrix2QuaturnionX();
    double y = getRotation().getRotationMatrix2QuaturnionY()
        - t.getRotation().getRotationMatrix2QuaturnionY();
    double z = getRotation().getRotationMatrix2QuaturnionZ()
        - t.getRotation().getRotationMatrix2QuaturnionZ();
    double r = Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
    return r;
  }

  /**
   * Gets the offset vector magnitude.
   *
   * @param t the t
   * @return the offset vector magnitude
   */
  public double getOffsetVectorMagnitude(TransformNR t) {
    double x = getX() - t.getX();
    double y = getY() - t.getY();
    double z = getZ() - t.getZ();
    double r = Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
    return r;
  }

  /**
   * Inverse.
   *
   * @return the transform nr
   */
  public TransformNR inverse() {
    return new TransformNR(getMatrixTransform().inverse());
  }

  /**
   * Scale.
   *
   * @param scale the scale
   * @return the transform nr
   */
  public TransformNR scale(BigDecimal scale) {
    return scale(scale.doubleValue());
  }

  /**
   * Scale.
   *
   * @param t the scale from 0 to 1.0
   * @return the transform nr
   */
  public TransformNR scale(double t) {
    if (t > 1)
      t = 1;
    if (t <= 0)
      return new TransformNR();

    double tilt = Math.toDegrees(getRotation().getRotationTilt() * t);
    double az = Math.toDegrees(getRotation().getRotationAzimuth() * t);
    double ele = Math.toDegrees(getRotation().getRotationElevation() * t);
    return new TransformNR(getX() * t, getY() * t, getZ() * t, new RotationNR(tilt, az, ele));
  }

  /**
   * Copy.
   *
   * @return the transform nr
   */
  public TransformNR copy() {
    return new TransformNR(getMatrixTransform());
  }

  /**
   * Translate x.
   *
   * @param translation the translation
   * @return
   */
  public TransformNR translateX(double translation) {
    setX(getX() + translation);
    return this;
  }

  /**
   * Translate y.
   *
   * @param translation the translation
   */
  public TransformNR translateY(double translation) {
    setY(getY() + translation);
    return this;

  }

  /**
   * Translate z.
   *
   * @param translation the translation
   */
  public TransformNR translateZ(double translation) {

    setZ(getZ() + translation);
    return this;
  }
  
	public TransformNR set(double tx, double ty, double tz, double[][] poseRot) {
		if (Double.isNaN(tx))
			throw new RuntimeException("Value can not be NaN");
		x = tx;
		if (Double.isNaN(ty))
			throw new RuntimeException("Value can not be NaN");
		y = ty;
		if (Double.isNaN(tz))
			throw new RuntimeException("Value can not be NaN");
		z = tz;
		getRotation().set(poseRot);
		fireChangeEvent();
		return this;
	}

  /**
   * Sets the x.
   *
   * @param tx the new x
   */
  public TransformNR setX(double tx) {
    if (Double.isNaN(tx))
      throw new RuntimeException("Value can not be NaN");
    x = tx;
    fireChangeEvent();
    return this;
  }

  /**
   * Sets the y.
   *
   * @param ty the new y
   */
  public TransformNR setY(double ty) {
    if (Double.isNaN(ty))
      throw new RuntimeException("Value can not be NaN");
    y = ty;
    fireChangeEvent();
    return this;
  }

  /**
   * Sets the z.
   *
   * @param tz the new z
   */
  public TransformNR setZ(double tz) {
    if (Double.isNaN(tz))
      throw new RuntimeException("Value can not be NaN");
    z = tz;
    fireChangeEvent();
    return this;
  }

  /**
   * Gets the xml.
   *
   * @return the xml
   */
  /*
   * 
   * Generate the xml configuration to generate an XML of this robot.
   */
  public String getXml() {
    String xml =
        "\t<x>" + getX() + "</x>\n" + "\t<y>" + getY() + "</y>\n" + "\t<z>" + getZ() + "</z>\n";
    if (Double.isNaN(getRotation().getRotationMatrix2QuaturnionW())
        || Double.isNaN(getRotation().getRotationMatrix2QuaturnionX())
        || Double.isNaN(getRotation().getRotationMatrix2QuaturnionY())
        || Double.isNaN(getRotation().getRotationMatrix2QuaturnionZ())) {
      xml += "\n\t<!-- ERROR a NaN was detected and replaced with a valid rotation -->\n";
      setRotation(new RotationNR());
    }
    xml += "\t<rotw>" + getRotation().getRotationMatrix2QuaturnionW() + "</rotw>\n" + "\t<rotx>"
        + getRotation().getRotationMatrix2QuaturnionX() + "</rotx>\n" + "\t<roty>"
        + getRotation().getRotationMatrix2QuaturnionY() + "</roty>\n" + "\t<rotz>"
        + getRotation().getRotationMatrix2QuaturnionZ() + "</rotz>";

    return xml;
  }

  /**
   * Sets the rotation.
   *
   * @param rotation the new rotation
   */
  public TransformNR setRotation(RotationNR rotation) {
    this.rotation = rotation;
    fireChangeEvent();
    return this;
  }


	public void addChangeListener(ITransformNRChangeListener l) {
		if(!getListeners().contains(l))
			getListeners().add(l);
	}
	public void removeChangeListener(ITransformNRChangeListener l) {
		if(getListeners().contains(l))
			getListeners().remove(l);
	}
	public void clearChangeListener() {
		getListeners().clear();
		listeners=null;
	}
	public ArrayList<ITransformNRChangeListener> getListeners() {
		if(listeners==null)
			listeners=new ArrayList<ITransformNRChangeListener>();
		return listeners;
	}

	void fireChangeEvent() {
		if(listeners!=null) {
			for(int i=0;i<listeners.size();i++) {
				try {
					listeners.get(i).event(this);
				}catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	public void setTiltDegrees(double newAngleDegrees) {
		double  e=0;
		try{
			e=Math.toDegrees(getRotation().getRotationElevation());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		double  a=0;
		try{
			a=Math.toDegrees(getRotation().getRotationAzimuth());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		setRotation(new RotationNR(newAngleDegrees, a, e));
		
		
	}

	public void setElevationDegrees(double newAngleDegrees) {
		double  t=0;
		try{
			t=Math.toDegrees(getRotation().getRotationTilt());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		double  a=0;
		try{
			a=Math.toDegrees(getRotation().getRotationAzimuth());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		setRotation(new RotationNR(t, a, newAngleDegrees));
	}

	public void setAzimuthDegrees(double newAngleDegrees) {
		double  t=0;
		try{
			t=Math.toDegrees(getRotation().getRotationTilt());
		}catch(Exception ex){
			ex.printStackTrace();
		}
			
		double  e=0;
		try{
			e=Math.toDegrees(getRotation().getRotationElevation());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		setRotation(new RotationNR(t, newAngleDegrees, e));
	}
}
