package com.noxception.midisense.dataclass;


import com.noxception.midisense.config.dataclass.LoggableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class MIDISenseUnitTest extends LoggableObject {

    //PERTAINING TO TESTING ANNOTATIONS
    public static class TestTags{
        public static final String VALID_INPUT = "VI";
        public static final String MALFORMED_INPUT = "MI";
        public static final String EMPTY_INPUT = "EI";
        public static final String REPEATED_INPUT = "RI";
        public static final String DOMAIN_SENSITIVE_INPUT = "DI";
    }

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
