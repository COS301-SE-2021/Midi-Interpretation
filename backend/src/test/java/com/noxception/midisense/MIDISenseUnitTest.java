package com.noxception.midisense;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MIDISenseUnitTest {

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
    public enum LogType{
        INFO,
        WARN,
        DEBUG,
        ERROR
    }
}
