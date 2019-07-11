package org.fogbowcloud.arrebol.execution.creator;

import org.fogbowcloud.arrebol.execution.Worker;

import java.util.Collection;

public interface WorkerCreator {

    Collection<Worker> createWorkers(Integer poolId);
}
