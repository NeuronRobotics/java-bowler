package junit.test.neuronrobotics.utilities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ByteListTest.class,AbstractConnectionTest.class,BowlerDatagramFactoryTests.class })
public class AllTests {

}
