package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.api.http.dataaccessobject.JobDAO;
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
    public JobDAO jobDAO() {
        return new JobDAO();
    }

    @Bean
    @Lazy
    public ArrebolFacade arrebolFacade(Properties properties, JobDAO jobDAO) {
        ArrebolController arrebolController = new ArrebolController(properties, jobDAO);
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


    @Autowired
    JobRepository jobRepository;


    @Override
    public void run(String... args) throws Exception {

        Command c1 = new Command("echo Hello World");
        Command c2 = new Command("echo Hello World");

        Map<String, String> requeriments1 = new HashMap<>();
        requeriments1.put("Glue2Ram", "2048");
        Specification spec1 = new Specification("ubuntu", "my_user", "publicKey", "privateKeyPath", requeriments1, "my_cloud");

        Task t1 = new Task("36961076-35f3-42bc-a932-bd2fe0249be9", spec1, Arrays.asList(c1, c2));

        t1.putMetadata("AnyData", "AnyValue");

        Map<String, Task> taskMap1 = new HashMap<>();
        taskMap1.put("36961076-35f3-42bc-a932-bd2fe0249be9", t1);

        Job j1 = new Job("job", taskMap1);

        Command c3 = new Command("sleep 10000");
        Command c4 = new Command("sleep 10000");

        Map<String, String> requeriments2 = new HashMap<>();
        requeriments2.put("Glue2Disk", "20");

        Specification spec2 = new Specification("fedora", "your_user", "anotherPublicKey", "anotherPrivateKeyPath", requeriments2, "cloud");

        Task t2 = new Task("fa100b5b-52a8-46c8-97aa-7413fe3100cb", spec2, Arrays.asList(c3, c4));

        t2.putMetadata("SuperData", "SuperValue");

        Map<String, Task> taskMap2 = new HashMap<>();
        taskMap2.put("36961076-35f3-42bc-a932-bd2fe0249be9", t2);

        Job j2 = new Job("job", taskMap2);

        jobRepository.save(j1);
        jobRepository.save(j2);

        Thread.currentThread().sleep(5000);

        jobRepository.deleteById(j1.getId());

    }
}