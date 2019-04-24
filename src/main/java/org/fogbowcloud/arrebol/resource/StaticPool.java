package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.execution.Worker;

import java.util.Collection;
import java.util.LinkedList;

public class StaticPool implements WorkerPool {

    //this is a very simple pool implementation: we receive the workers at
    //the construction time, so the pool does not change.
    //also, we do not expose any information about how workers are being used (to that we can help pool growth)

    private final int poolId;
    private final Collection<Worker> workers;

    public StaticPool(int poolId, Collection<Worker> workers) {
        this.poolId = poolId;
        this.workers = workers;
    }

    @Override
    public int getId() {
        return this.poolId;
    }

    @Override
    public Collection<Worker> getWorkers() {
        return new LinkedList<Worker>(this.workers);
    }

    @Override
    public String toString() {
        return "id={" + getId() + "}";
    }
}
