package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.core.ArrebolController;
import org.fogbowcloud.arrebol.core.ArrebolFacade;
import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
public class ArrebolApplication implements CommandLineRunner {

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

    @Autowired
    private JobRepository jobRepository;

    public static void main(String[] args) {
        SpringApplication.run(ArrebolApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Command c1 = new Command("echo teste1");
        Command c2 = new Command("echo teste2");

        Specification spec1 = new Specification("ubuntu", "my_user", "publicKey", "privateKeyPath", "my_cloud");

        Task t = new Task("task_id", spec1, Arrays.asList(c1, c2));

        Map<String, Task> taskMap = new HashMap<>();
        taskMap.put("task_id", t);

        Job j = new Job("job", taskMap);

        t.setJob(j);
        c1.setTask(t);
        c2.setTask(t);

        jobRepository.save(j);
    }
}