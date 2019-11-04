package org.fogbowcloud.arrebol.processor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fogbowcloud.arrebol.models.job.JobState;
import org.fogbowcloud.arrebol.processor.DefaultJobProcessor;

public class DefaultJobProcessorDTO {

    public String id;
    public String name;
    @JsonProperty("waiting_jobs")
    public long waitingJobs;
    @JsonProperty("workers_nodes")
    public Integer workersNodes;
    @JsonProperty("worker_pool")
    public Integer workerPool;

    public DefaultJobProcessorDTO(DefaultJobProcessor defaultJobProcessor) {
        this.id = defaultJobProcessor.getId();
        this.name = defaultJobProcessor.getName();
        this.waitingJobs = defaultJobProcessor.getJobs().values().stream().filter(
            job -> !(job.getJobState().equals(JobState.FINISHED) || job.getJobState()
                .equals(JobState.FAILED))).count();
        this.workersNodes = defaultJobProcessor.getWorkerNodesSize();
        this.workerPool = defaultJobProcessor.getWorkerPoolSize();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getWaitingJobs() {
        return waitingJobs;
    }

    public Integer getWorkersNodes() {
        return workersNodes;
    }

    public Integer getWorkerPool() {
        return workerPool;
    }
}
