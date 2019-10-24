package org.fogbowcloud.arrebol.queue.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;

public class WorkerNode {

    @NotEmpty
    private String address;

    @JsonProperty("worker_pool")
    private Integer workerPool;

    public WorkerNode(){}

    public WorkerNode(String address, Integer workerPool) {
        this.address = address;
        this.workerPool = workerPool;
    }

    public String getAddress() {
        return address;
    }

    public Integer getWorkerPool() {
        return workerPool;
    }
}
