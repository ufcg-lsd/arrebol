package org.fogbowcloud.arrebol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication(exclude = RepositoryRestMvcAutoConfiguration.class)
public class ArrebolApplication {

    @Autowired
    private ArrebolController arrebolController;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ArrebolApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
        springApplication.run(args);
    }

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

    @Bean
    CommandLineRunner cmdRunner() {
        return new ArrebolMainRunner();
    }

}