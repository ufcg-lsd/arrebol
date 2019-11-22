package org.fogbowcloud.arrebol.processor.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class WorkerNode {

    @NotEmpty
    @JsonProperty("resource_address")
    private String address;

    @Min(value = 1)
    @JsonProperty("pool_size")
    private Integer poolSize;

    public WorkerNode(){}

    public WorkerNode(String address, Integer poolSize) {
        this.address = address;
        this.poolSize = poolSize;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPoolSize() {
        return poolSize;
    }
}
