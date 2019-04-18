package org.fogbowcloud.arrebol.api.http.services;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.ArrebolFacade;
import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.models.job.JobSpec;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Lazy
@Component
public class JobService {

    @Lazy
    @Autowired
    private ArrebolFacade arrebolFacade;

    private final Logger LOGGER = Logger.getLogger(JobService.class);

    public String addJob(JobSpec jobSpec){
        Job job = createJobFromSpec(jobSpec);
        return this.arrebolFacade.addJob(job);
    }

    private Job createJobFromSpec(JobSpec jobSpec){
        Job job = new Job(jobSpec.getLabel());
        for(TaskSpec taskSpec : jobSpec.getTasksSpecs()){
            List<Command> commands = taskSpec.getCommands();
            Specification spec = taskSpec.getSpec();
            String taskId = UUID.randomUUID().toString();
            Task task = new Task(taskId, spec, commands);
            job.addTask(task);
        }
        return job;
    }

}
