package org.fogbowcloud.arrebol.execution.creator;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolController;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.execution.raw.RawConfiguration;
import org.fogbowcloud.arrebol.execution.raw.RawTaskExecutor;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.processor.spec.WorkerNode;
import org.fogbowcloud.arrebol.resource.MatchAnyWorker;

import java.util.Collection;
import java.util.LinkedList;

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
            LOGGER.info("Creating raw worker[" + i + "]");
            Worker worker = createRawWorker(poolId, i);
            workers.add(worker);
        }
        return workers;
    }

    @Override
    public Collection<Worker> createWorkers(Integer poolId, WorkerNode workerNode) {
        return null;
    }

    private Worker createRawWorker(Integer poolId, int resourceId) {
        TaskExecutor executor = new RawTaskExecutor();
        Specification resourceSpec = null;
        Worker worker = new MatchAnyWorker(resourceSpec, "resourceId-" + resourceId, poolId,
            executor);
        return worker;
    }
}
