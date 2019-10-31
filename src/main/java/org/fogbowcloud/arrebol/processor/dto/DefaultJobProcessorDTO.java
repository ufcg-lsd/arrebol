package org.fogbowcloud.arrebol.processor.dto;

import org.fogbowcloud.arrebol.processor.DefaultJobProcessor;

public class DefaultJobProcessorDTO {

    public String id;
    public String name;
    public Integer jobs;
    public Integer workersNodes;
    public Integer workerPool;

    public DefaultJobProcessorDTO(DefaultJobProcessor defaultJobProcessor) {
        this.id = defaultJobProcessor.getId();
        this.name = defaultJobProcessor.getName();
        this.jobs = defaultJobProcessor.getJobs().size();
        this.workersNodes = defaultJobProcessor.getWorkerNodesSize();
        this.workerPool = defaultJobProcessor.getWorkerPoolSize();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getJobs() {
        return jobs;
    }

    public Integer getWorkersNodes() {
        return workersNodes;
    }

    public Integer getWorkerPool() {
        return workerPool;
    }
}
