package com.noxception.midisense.dataclass;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Class that mocks the functionality of {@link com.noxception.midisense.config.MIDISenseConfig} configuration class.
 * The configurations are accessed in the same manner, due to the {@link StandardConfig} wrapper from which both classes
 * inherit.
 */
public class MockConfigurationSettings extends StandardConfig {

    //TODO: Claudio fill in a framework for loading settings here

    public MockConfigurationSettings(){
        try {
            FileInputStream configFile = new FileInputStream("src/main/resources/application.properties");
            Properties properties = new Properties();
            properties.load(configFile);
            properties.forEach(
                    (k ,v)->{
                        if(k.toString().contains("midisense.config")){
                            String source = k.toString().substring(17);
                            ConfigurationName key = ConfigurationName.valueOf(source);
                            String value = v.toString();
                            this.configurations.put(key,value);
                        }
                    }
            );
        }
        catch (IOException ignored) {

        }
    }
    public MockConfigurationSettings(Map<ConfigurationName, String> inputSettings){
       this.configurations = inputSettings;
    }

}
