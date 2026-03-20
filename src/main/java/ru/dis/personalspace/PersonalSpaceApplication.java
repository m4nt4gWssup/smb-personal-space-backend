package ru.dis.personalspace;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

import ru.disgroup.kms.common.actuator.actuatorlogging.annotation.EnableActuatorLogging;
import ru.disgroup.kms.common.core.KmsApplication;

/**
 * Application main class.
 *
 * @author 
 */
@SpringBootApplication
@EnableFeignClients
@EnableActuatorLogging
public class PersonalSpaceApplication extends KmsApplication {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder
                .sources(PersonalSpaceApplication.class)
                .properties(getProperties("smb-personal-space", null));
    }

    /**
     * Application main method.
     *
     * @param args Command-line args
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(PersonalSpaceApplication.class)
                .sources(PersonalSpaceApplication.class)
                .properties(getProperties("smb-personal-space", args))
                .run(args);
    }
}
