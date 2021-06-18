package com.noxception.midisense.config.dataclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggableObject {
    public Boolean LOGGING_ACTIVE = false;
    //PERTAINING TO LOGGING
    protected static final Logger logger = LoggerFactory.getLogger(LoggableObject.class);
    protected void log(String message, LogType logType){
        if(this.LOGGING_ACTIVE) switch(logType){
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
}
