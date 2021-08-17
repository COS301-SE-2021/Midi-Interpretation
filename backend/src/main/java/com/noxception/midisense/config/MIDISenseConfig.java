package com.noxception.midisense.config;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles configuration settings relative to the Spring environment
 */
@Service
public class MIDISenseConfig extends StandardConfig{

    public static Map<ConfigurationName, String> configurations = new HashMap<>();

    @Override
    public boolean isSet(ConfigurationName key){ return configurations.containsKey(key); }

    @Override
    public String configuration(ConfigurationName key){
        return configurations.get(key);
    }



}
