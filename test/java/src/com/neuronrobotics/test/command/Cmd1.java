package com.neuronrobotics.test.command;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerMethod;
public class Cmd1 extends BowlerAbstractCommand {
        public Cmd1(int boadrNumber,int arg1, int arg2){
                setOpCode("exmp");
                setMethod(BowlerMethod.POST);
                getCallingDataStorage().add(boadrNumber);
                getCallingDataStorage().add(arg1);
                getCallingDataStorage().addAs32(arg2);
        }
}