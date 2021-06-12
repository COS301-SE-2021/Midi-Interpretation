package com.noxception.midisense;

import com.noxception.midisense.config.DevelopmentNote;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.display.DisplayController;
import com.noxception.midisense.display.DisplayServiceImpl;
import com.noxception.midisense.intelligence.IntelligenceController;
import com.noxception.midisense.intelligence.IntelligenceServiceImpl;
import com.noxception.midisense.interpreter.InterpreterController;
import com.noxception.midisense.interpreter.InterpreterServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

public class DeveloperProgress extends MIDISenseUnitTest {

    @Test
    public void getTaskProgress(){
        Class[] testingClasses = {
                DisplayServiceImpl.class,
                IntelligenceServiceImpl.class,
                InterpreterServiceImpl.class,
                DisplayController.class,
                IntelligenceController.class,
                InterpreterController.class
        };
        for(Class caseClass: testingClasses){
            System.out.println("====== ["+caseClass.getSimpleName()+"] ======");
            for(Method method: caseClass.getDeclaredMethods()){
                if(method.isAnnotationPresent(DevelopmentNote.class)){
                    DevelopmentNote note = method.getAnnotation(DevelopmentNote.class);
                    String summary = "+ ["+note.status()+" | "+note.lastModified()+"] "+note.taskName()+" | "+Arrays.toString(note.developers())+
                            " | "+note.comments();
                    System.out.println(summary);
                }
            }
        }

    }
}
