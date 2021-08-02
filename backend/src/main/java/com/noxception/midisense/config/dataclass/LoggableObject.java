package com.noxception.midisense.config.dataclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoggableObject {

    protected List<LogType> monitorList = new ArrayList<>();

    protected void monitor(LogType[] types){
        monitorList = Arrays.asList(types);
    }

    //PERTAINING TO LOGGING
    protected static final Logger logger = LoggerFactory.getLogger(LoggableObject.class);
    protected void log(String message, LogType logType){
        switch(logType){
            case INFO: if(monitorList.contains(LogType.INFO)) logger.info(message); break;
            case WARN: if(monitorList.contains(LogType.WARN)) logger.warn(message); break;
            case DEBUG: if(monitorList.contains(LogType.DEBUG)) logger.debug(message); break;
            case ERROR: if(monitorList.contains(LogType.ERROR)) logger.error(message);
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
