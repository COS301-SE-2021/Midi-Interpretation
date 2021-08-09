package com.noxception.midisense.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that provides an abstraction of a system configuration loader. subclasses are able to override the means for
 * saving and accessing program configurations, such as loading from the environment, or producing mocked services.
 */
public class StandardConfig {
    public Map<ConfigurationName, String> configurations = new HashMap<>();

    public boolean isSet(ConfigurationName key){ return configurations.containsKey(key); }

    public String configuration(ConfigurationName key){
        return configurations.get(key);
    }
}
