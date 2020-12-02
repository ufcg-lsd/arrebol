/* (C)2020 */
package org.fogbowcloud.arrebol.processor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fogbowcloud.arrebol.models.job.JobState;
import org.fogbowcloud.arrebol.processor.DefaultJobProcessor;

public class DefaultJobProcessorDTO {

  public String id;
  public String name;

  @JsonProperty("waiting_jobs")
  public long waitingJobs;

  @JsonProperty("worker_pools")
  public Integer workerPools;

  @JsonProperty("pools_size")
  public Integer poolsSize;

  public DefaultJobProcessorDTO(DefaultJobProcessor defaultJobProcessor) {
    this.id = defaultJobProcessor.getId();
    this.name = defaultJobProcessor.getName();
    this.waitingJobs =
        defaultJobProcessor.getJobs().values().stream()
            .filter(
                job ->
                    !(job.getJobState().equals(JobState.FINISHED)
                        || job.getJobState().equals(JobState.FAILED)))
            .count();
    this.workerPools = defaultJobProcessor.getWorkerPoolsSize();
    this.poolsSize = defaultJobProcessor.getPoolsSize();
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

  public Integer getWorkerPools() {
    return workerPools;
  }

  public Integer getPoolsSize() {
    return poolsSize;
  }
}
