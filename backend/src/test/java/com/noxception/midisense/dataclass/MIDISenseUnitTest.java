package com.noxception.midisense.dataclass;


import java.lang.reflect.Field;

public class MIDISenseUnitTest {

    protected void logAllFields(Object o){
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                break;
            }
            if (value != null) {
                System.out.println(field.getName() + ": " + value);
            }
        }
    }

}
