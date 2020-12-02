/* (C)2020 */
package org.fogbowcloud.arrebol.execution.creator;

import java.util.Collection;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolController;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.execution.raw.RawConfiguration;
import org.fogbowcloud.arrebol.execution.raw.RawTaskExecutor;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.processor.spec.WorkerNode;
import org.fogbowcloud.arrebol.resource.MatchAnyWorker;
import org.fogbowcloud.arrebol.utils.AppUtil;

public class RawWorkerCreator implements WorkerCreator {

  private final Logger LOGGER = Logger.getLogger(ArrebolController.class);
  private RawConfiguration configuration;

  public RawWorkerCreator(Configuration configuration) throws Exception {
    this.configuration = new RawConfiguration(configuration);
  }

  @Override
  public Collection<Worker> createWorkers(Integer poolId) {
    Collection<Worker> workers = new LinkedList<>();
    int poolSize = new Integer(configuration.getWorkerPoolSize());
    for (int i = 0; i < poolSize; i++) {
      Worker worker = createRawWorker(poolId);
      workers.add(worker);
    }
    return workers;
  }

  @Override
  public Collection<Worker> createWorkers(Integer poolId, WorkerNode workerNode) {
    return null;
  }

  private Worker createRawWorker(Integer poolId) {
    TaskExecutor executor = new RawTaskExecutor();
    Specification resourceSpec = null;
    Worker worker =
        new MatchAnyWorker(AppUtil.generateUniqueStringId(), resourceSpec, poolId, executor);
    LOGGER.info("Created raw worker [" + worker.getId() + "]");
    return worker;
  }
}
