package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.lang.reflect.Type;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neuronrobotics.sdk.addons.kinematics.VitaminLocation;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class GsonVitaminLoad {

	@Test
	public void test() {
		Type type = new TypeToken<VitaminLocation>() {
		}.getType();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().setPrettyPrinting()
				.create();
		VitaminLocation src = new VitaminLocation(false, "Tester", "hobbyServo", "mg92b", new TransformNR());
		String content = gson.toJson(src);
		com.neuronrobotics.sdk.common.Log.error(content);
	}

}
