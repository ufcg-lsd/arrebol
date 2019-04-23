package org.fogbowcloud.arrebol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.Properties;

@SpringBootApplication
public class ArrebolApplication {

    @Bean
    public Properties properties() {
        return new Properties();
    }

    @Bean
    @Lazy
    public ArrebolFacade arrebolFacade(Properties properties) {
        ArrebolController arrebolController = new ArrebolController(properties);
        ArrebolFacade arrebolFacade = new ArrebolFacade(arrebolController);
        try {
            arrebolFacade.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrebolFacade;
    }

    public static void main(String[] args) {
        SpringApplication.run(ArrebolApplication.class, args);
    }

}