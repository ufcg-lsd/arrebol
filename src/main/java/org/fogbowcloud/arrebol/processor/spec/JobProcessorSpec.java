package org.fogbowcloud.arrebol.processor.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

public class JobProcessorSpec {

    @NotNull
    private String name;

    @JsonProperty("worker_nodes")
    private List<WorkerNode> workerNodes;

    public JobProcessorSpec() {
        this.workerNodes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<WorkerNode> getWorkerNodes() {
        return workerNodes;
    }
}
