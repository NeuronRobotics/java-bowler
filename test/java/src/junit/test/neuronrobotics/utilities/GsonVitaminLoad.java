package junit.test.neuronrobotics.utilities;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neuronrobotics.sdk.addons.kinematics.VitaminLocation;

public class GsonVitaminLoad {

	@Test
	public void test() {
		Type type = new TypeToken<VitaminLocation>() {}.getType();
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create();
		VitaminLocation src = new VitaminLocation();
		String content = gson.toJson(src);
		System.out.println(content);
	}

}
