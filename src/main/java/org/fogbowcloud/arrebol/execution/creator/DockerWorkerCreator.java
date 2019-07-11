package org.fogbowcloud.arrebol.execution.creator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolController;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.execution.docker.DockerConfiguration;
import org.fogbowcloud.arrebol.execution.docker.DockerTaskExecutor;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.resource.MatchAnyWorker;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class DockerWorkerCreator implements WorkerCreator{

    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);
    private static final String TASK_SCRIPT_EXECUTOR_NAME = "task-script-executor.sh";
    private final String tsExecutorFileContent;
    private final DockerConfiguration configuration;
    
    public DockerWorkerCreator(Configuration configuration) throws Exception {
        this.configuration = new DockerConfiguration(configuration);
        Resource resource = new ClassPathResource(TASK_SCRIPT_EXECUTOR_NAME);
        try(InputStream is = resource.getInputStream()) {
            this.tsExecutorFileContent = IOUtils.toString(is, "UTF-8");
            LOGGER.debug("Task script executor content [" + this.tsExecutorFileContent + "]");
        }
    }

    @Override
    public Collection<Worker> createWorkers(Integer poolId) {
        Collection<Worker> workers = new LinkedList<>();
        int poolSize = configuration.getWorkerPoolSize();
        for(String address : configuration.getResourceAddresses()){
            for (int i = 0; i < poolSize; i++) {
                LOGGER.info("Creating docker worker with address=" + address);
                Worker worker = createDockerWorker(poolId, i, address);
                workers.add(worker);
            }
        }
        return workers;
    }

    private Worker createDockerWorker(Integer poolId, int resourceId, String address) {
        TaskExecutor executor = new DockerTaskExecutor("docker-executor-" + UUID.randomUUID().toString(), address, this.tsExecutorFileContent, this.configuration.getImageId());
        Specification resourceSpec = null;
        return new MatchAnyWorker(resourceSpec, "resourceId-"+resourceId, poolId, executor);
    }
}
