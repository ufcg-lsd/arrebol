package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.execution.Worker;

import java.util.Collection;

public interface WorkerPool {

    int getId();

    Collection<Worker> getWorkers();

    void addWorkers(Collection<Worker> workers);
}
