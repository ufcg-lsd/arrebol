package org.fogbowcloud.arrebol.queue.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;

public class QueueSpec {

    @NotNull
    private String name;

    @NotNull
    @JsonProperty("worker_nodes")
    private List<WorkerNode> workerNodes;

    public QueueSpec() {
    }

    public String getName() {
        return name;
    }

    public List<WorkerNode> getWorkerNodes() {
        return workerNodes;
    }
}
