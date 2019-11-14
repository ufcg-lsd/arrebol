package org.fogbowcloud.arrebol;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

    private static final Logger LOGGER = LogManager.getLogger(ArrebolApplication.class);
    public static final String CONF_FILE_PROPERTY = "conf_file";

    @Autowired
    private ArrebolController arrebolController;

    public static void main(String[] args) {
        loadArguments(args);
        SpringApplication springApplication = new SpringApplication(ArrebolApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
        springApplication.run(args);
    }


    private static void loadArguments(String[] args) {
        Options options = new Options();

        String opt = "c";
        String longOpt = CONF_FILE_PROPERTY;
        String description = "Configuration file path";
        Option confFilePath = new Option(opt, longOpt, true, description);
        confFilePath.setRequired(false);
        options.addOption(confFilePath);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption(longOpt)) {
                String inputFilePath = cmd.getOptionValue(longOpt);
                System.setProperty(CONF_FILE_PROPERTY , inputFilePath);
            }
        } catch (ParseException e) {
            LOGGER.error("Error while loading command line arguments: " + e.getMessage(), e);
            System.exit(1);
        }
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