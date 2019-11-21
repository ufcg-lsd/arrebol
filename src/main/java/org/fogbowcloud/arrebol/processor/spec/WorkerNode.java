package org.fogbowcloud.arrebol.processor.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;

public class WorkerNode {

    @NotEmpty
    @JsonProperty("resource_address")
    private String address;

    @JsonProperty("pool_size")
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
