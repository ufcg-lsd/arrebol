package org.fogbowcloud.arrebol.execution.creator;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolController;
import org.fogbowcloud.arrebol.Configuration;
import org.fogbowcloud.arrebol.execution.docker.DockerTaskExecutor;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.resource.MatchAnyWorker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class DockerWorkerCreator implements WorkerCreator{

    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);

    @Override
    public Collection<Worker> createWorkers(Integer poolId, Configuration configuration) {
        Collection<Worker> workers = new LinkedList<>();
        int poolSize = new Integer(configuration.getWorkerPoolSize());
        String imageId = configuration.getImageId();
        for(String address : configuration.getResourceAddresses()){
            for (int i = 0; i < poolSize; i++) {
                LOGGER.info("Creating docker worker with address=" + address);
                Worker worker = createDockerWorker(poolId, i, imageId, address);
                workers.add(worker);
            }
        }
        return workers;
    }

    private Worker createDockerWorker(Integer poolId, int resourceId, String imageId, String address) {
        TaskExecutor executor = new DockerTaskExecutor(imageId, "docker-executor-" + UUID.randomUUID().toString(), address);
        Specification resourceSpec = null;
        Worker worker = new MatchAnyWorker(resourceSpec, "resourceId-"+resourceId, poolId, executor);
        return worker;
    }
}
