package org.fogbowcloud.arrebol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.Properties;

@SpringBootApplication(exclude = RepositoryRestMvcAutoConfiguration.class)
public class ArrebolApplication {

    @Autowired
    private ArrebolController arrebolController;

    @Bean
    @Lazy
    public ArrebolFacade arrebolFacade() {
        ArrebolFacade arrebolFacade = new ArrebolFacade(arrebolController);
        try {
            arrebolFacade.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrebolFacade;
    }

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ArrebolApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
        springApplication.run(args);
    }

}