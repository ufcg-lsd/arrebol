package org.fogbowcloud.arrebol.execution.creator;

import org.fogbowcloud.arrebol.execution.Worker;

import java.util.Collection;
import org.fogbowcloud.arrebol.queue.spec.WorkerNode;

public interface WorkerCreator {

    Collection<Worker> createWorkers(Integer poolId);

    Collection<Worker> createWorkers(Integer poolId, WorkerNode workerNode);
}
