package org.fogbowcloud.arrebol.resource;

import java.util.Map;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.models.task.Task;

/**
 * This @{link Worker} implementation matches any @{link Specification}.
 * It delegates @{link TaskExecutor} behaviour to received object.
 */
public class MatchAnyWorker implements Worker {

    //simple resource that accepts any request

    private ResourceState state;
    private final Specification spec;
    private final String id;
    private final int poolId;
    private final TaskExecutor executor;

    public MatchAnyWorker(Specification spec, String id, int poolId, TaskExecutor delegatedExecutor) {
        this.spec = spec;
        this.id = id;
        this.poolId = poolId;
        this.state = ResourceState.IDLE;
        this.executor = delegatedExecutor;
    }

    @Override
    public boolean match(Map<String, String> requirements) {
        return true;
    }

    @Override
    public ResourceState getState() {
        return this.state;
    }

    @Override
    public void setState(ResourceState state) {
        this.state = state;
    }

    @Override
    public Specification getSpecification() {
        return this.spec;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getPoolId() {
        return this.poolId;
    }

    @Override
    public String toString() {
        return "id={" + this.id + "} poolId={" + poolId + "} " +
                "executor={" + this.executor + "}";
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        return this.executor.execute(task);
    }
}
