package org.fogbowcloud.arrebol.api.http.services;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolFacade;
import org.fogbowcloud.arrebol.api.constants.Messages;
import org.fogbowcloud.arrebol.api.exceptions.InvalidJobSpecException;
import org.fogbowcloud.arrebol.api.exceptions.JobNotFoundException;
import org.fogbowcloud.arrebol.api.http.dataaccessobject.JobDAO;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobSpec;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Lazy
@Service
public class JobService {

    @Lazy
    @Autowired
    private ArrebolFacade arrebolFacade;

    @Autowired
    private JobDAO jobDAO;

    private final Logger LOGGER = Logger.getLogger(JobService.class);

    public String addJob(JobSpec jobSpec) {
        LOGGER.debug("Creating job object from job specification.");
        validateJobSpec(jobSpec);
        Job job = createJobFromSpec(jobSpec);
        LOGGER.info("Created job [ " + job.getId() + " ] from jobSpec");
        String id = this.arrebolFacade.addJob(job);
        this.jobDAO.addJob(job);
        return id;
    }

    public Job getJobById(String id) {
        Job job = this.jobDAO.getJobById(id);
        if(job == null){
            String message = String.format(Messages.Exception.JOB_NOT_FOUND, id);
            throw new JobNotFoundException(message);
        }
        return job;
    }

    private Job createJobFromSpec(JobSpec jobSpec){

        Collection<Task> taskList = new LinkedList<>();

        for(TaskSpec taskSpec : jobSpec.getTasksSpecs()){
            String taskId = UUID.randomUUID().toString();
            Task task = new Task(taskId, taskSpec);
            taskList.add(task);
        }
        Job job = new Job(jobSpec.getLabel(), taskList);
        LOGGER.debug("Created job object of " + job.getLabel() + " with " + taskList.size() + " tasks.");
        return job;
    }

    private void validateJobSpec(JobSpec jobSpec){
        if(jobSpec == null || jobSpec.getTasksSpecs() == null || jobSpec.getTasksSpecs().isEmpty() ||
                ! validateTasksSpecs(jobSpec.getTasksSpecs())){
            String message = String.format(Messages.Exception.INVALID_JOB_SPEC, jobSpec.toString());
            LOGGER.error(message);
            throw new InvalidJobSpecException(message);
        } else {
            LOGGER.info(String.format("JobSpec was validate: %s", jobSpec.toString()));
        }
    }

    private boolean validateTasksSpecs(List<TaskSpec> tasksSpecsList){
        for(TaskSpec taskSpec : tasksSpecsList){
            if(taskSpec == null || taskSpec.getCommands() == null || taskSpec.getCommands().isEmpty()){
                return false;
            }
        }
        return true;
    }

}
