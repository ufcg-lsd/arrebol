package org.fogbowcloud.arrebol.api.http.services;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.api.http.dataaccessobject.JobDAO;
import org.fogbowcloud.arrebol.ArrebolFacade;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobSpec;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Lazy
@Service
public class JobService {

    @Lazy
    @Autowired
    private ArrebolFacade arrebolFacade;

    @Autowired
    private JobDAO jobDAO;

    private final Logger LOGGER = Logger.getLogger(JobService.class);

    public String addJob(JobSpec jobSpec){
        LOGGER.debug("Creating job object from job specification.");
        Job job = createJobFromSpec(jobSpec);
        String id = this.arrebolFacade.addJob(job);
        this.jobDAO.addJob(job);
        return id;
    }

    public Job getJobById(String id){
        Job job = this.jobDAO.getJobById(id);
        return job;
    }

    private Job createJobFromSpec(JobSpec jobSpec){

        Collection<Task> taskList = new LinkedList<>();

        for(TaskSpec taskSpec : jobSpec.getTasksSpecs()){
            String taskId = UUID.randomUUID().toString();
            List<Command> commands = taskSpec.getCommands();
            Specification spec = taskSpec.getSpec();
            Map<String, String> metadata = taskSpec.getMetadata();

            Task task = new Task(taskId, spec, commands, metadata);
            taskList.add(task);
        }
        Job job = new Job(jobSpec.getLabel(), taskList);
        LOGGER.debug("Created job object of " + job.getLabel() + " with " + taskList.size() + " tasks.");
        return job;
    }

}
