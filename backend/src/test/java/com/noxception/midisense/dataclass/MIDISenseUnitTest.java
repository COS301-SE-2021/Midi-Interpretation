package com.noxception.midisense.dataclass;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MIDISenseUnitTest {

    //PERTAINING TO LOGGING
    protected static final Logger logger = LoggerFactory.getLogger(MIDISenseUnitTest.class);
    protected void log(String message, LogType logType){
        switch(logType){
            case INFO: logger.info(message); break;
            case WARN: logger.warn(message); break;
            case DEBUG: logger.debug(message); break;
            case ERROR: logger.error(message);
        }
    }
    protected void log(String message){
        log(message,LogType.INFO);
    }
    protected void log(Object message, LogType logType){
        log(message.toString(),logType);
    }
    protected void log(Object message){
        log(message,LogType.INFO);
    }
    public enum LogType{
        INFO,
        WARN,
        DEBUG,
        ERROR
    }

    //PERTAINING TO TESTING ANNOTATIONS
    public class TestTags{
        public static final String VALID_INPUT = "VI";
        public static final String MALFORMED_INPUT = "MI";
        public static final String EMPTY_INPUT = "EI";
        public static final String REPEATED_INPUT = "RI";
        public static final String DOMAIN_SENSITIVE_INPUT = "DI";
    }

}
