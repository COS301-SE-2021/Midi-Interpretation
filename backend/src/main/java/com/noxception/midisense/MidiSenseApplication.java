package com.noxception.midisense;

import com.noxception.midisense.config.MIDISenseConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;

@EnableSwagger2
@SpringBootApplication
public class MidiSenseApplication {

    /**
     * The main method of the MidiSense Application
     * @param args system arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MidiSenseApplication.class, args);
    }

    /**
     * Method that loads configuration properties from the respective .properties file into the MidiSenseConfig class
     */
    @Bean
    ApplicationRunner configurationLoader (Environment environment){
        return args -> {
            for(MIDISenseConfig.ConfigurationName key : MIDISenseConfig.ConfigurationName.values()){
                String property = environment.getProperty("midisense.config."+key.toString());
                if (property == null) {
                    System.out.println("Missing MidiSense Program Configuration : "+key+" : Exiting");
                    System.exit(0);
                }
                MIDISenseConfig.configurations.put(key, property);
            }
        };
    }

}
