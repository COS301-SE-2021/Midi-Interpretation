package com.noxception.midisense.dataclass;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;

import java.util.Map;

/**
 * Class that mocks the functionality of {@link com.noxception.midisense.config.MIDISenseConfig} configuration class.
 * The configurations are accessed in the same manner, due to the {@link StandardConfig} wrapper from which both classes
 * inherit.
 */
public class MockConfigurationSettings extends StandardConfig {

    //TODO: Claudio fill in a framework for loading settings here

    public MockConfigurationSettings(){

    }
    public MockConfigurationSettings(Map<ConfigurationName, String> inputSettings){
       this.configurations = inputSettings;
    }

}
