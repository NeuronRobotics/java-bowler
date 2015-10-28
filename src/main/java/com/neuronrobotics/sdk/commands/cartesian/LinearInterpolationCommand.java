package com.neuronrobotics.sdk.commands.cartesian;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearInterpolationCommand.
 */
public class LinearInterpolationCommand extends BowlerAbstractCommand {

	/**
	 * Instantiates a new linear interpolation command.
	 *
	 * @param taskSpaceTransform the task space transform
	 * @param mmOfFiliment the mm of filiment
	 * @param ms the ms
	 * @param forceNoBuffer the force no buffer
	 */
	public LinearInterpolationCommand(TransformNR taskSpaceTransform, double mmOfFiliment, int ms, boolean forceNoBuffer) {
		setOpCode("_sli");
		setMethod(BowlerMethod.POST);
		getCallingDataStorage().add(forceNoBuffer?1:0);
		getCallingDataStorage().addAs32(ms);
		
		getCallingDataStorage().addAs32((int) (taskSpaceTransform.getX()*1000));
		getCallingDataStorage().addAs32((int) (taskSpaceTransform.getY()*1000));
		getCallingDataStorage().addAs32((int) (taskSpaceTransform.getZ()*1000));
		
		getCallingDataStorage().addAs32((int) (mmOfFiliment*1000));


	}
}
