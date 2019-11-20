package org.fogbowcloud.arrebol.api.http.services;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolFacade;
import org.fogbowcloud.arrebol.api.exceptions.JobNotFoundException;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobSpec;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.fogbowcloud.arrebol.processor.dto.DefaultJobProcessorDTO;
import org.fogbowcloud.arrebol.processor.spec.JobProcessorSpec;
import org.fogbowcloud.arrebol.processor.spec.WorkerNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private final Logger LOGGER = Logger.getLogger(QueueService.class);

    @Lazy
    @Autowired
    private ArrebolFacade arrebolFacade;

    public String addJobToQueue(String queue, JobSpec jobSpec) {
        LOGGER.debug("Create job object from job specification.");
        Job job = createJobFromSpec(jobSpec);
        LOGGER.info("Created job [ " + job.getId() + " ] from jobSpec");
        String id = this.arrebolFacade.addJob(queue, job);
        return id;
    }

    private Job createJobFromSpec(JobSpec jobSpec) {

        Collection<Task> taskList = new LinkedList<>();

        for (TaskSpec taskSpec : jobSpec.getTasksSpecs()) {
            validateTaskSpec(taskSpec);
            String taskId = UUID.randomUUID().toString();
            taskSpec.setId(taskId);
            Task task = new Task(taskId, taskSpec);
            taskList.add(task);
        }
        Job job = new Job(jobSpec.getLabel(), taskList);
        LOGGER.debug(
            "Created job object of " + job.getLabel() + " with " + taskList.size() + " tasks.");
        return job;
    }

    private void validateTaskSpec(TaskSpec task) {
        if(Objects.isNull(task.getCommands())) {
            throw new IllegalArgumentException("Commands list may not be null");
        }
    }

    public Job getJobByIdFromQueue(String queueId, String jobId) {
        Job job = arrebolFacade.getJob(queueId, jobId);
        if (job == null) {
            String message = String.format("Job [%s] not found in queue [%s]", jobId, queueId);
            throw new JobNotFoundException(message);
        }
        return job;
    }

    public String createQueue(JobProcessorSpec jobProcessorSpec) {
        String queueId = arrebolFacade.createQueue(jobProcessorSpec);
        return queueId;
    }

    public List<DefaultJobProcessorDTO> getQueues() {
        return arrebolFacade.getQueues();
    }

    public DefaultJobProcessorDTO getQueue(String queueId) {
        return arrebolFacade.getQueue(queueId);
    }

    public void addWorkers(String queueId, WorkerNode workerNode) {
        this.arrebolFacade.addWorkers(queueId, workerNode);
    }
}
