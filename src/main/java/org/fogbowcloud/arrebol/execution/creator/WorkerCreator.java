/* (C)2020 */
package org.fogbowcloud.arrebol.execution.creator;

import java.util.Collection;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.processor.spec.WorkerNode;

public interface WorkerCreator {

  Collection<Worker> createWorkers(Integer poolId);

  Collection<Worker> createWorkers(Integer poolId, WorkerNode workerNode);
}
