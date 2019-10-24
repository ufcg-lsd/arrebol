package org.fogbowcloud.arrebol.execution.creator;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolController;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.execution.docker.DockerCommandExecutor;
import org.fogbowcloud.arrebol.execution.docker.DockerConfiguration;
import org.fogbowcloud.arrebol.execution.docker.DockerTaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerContainerRequestHelper;
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerFileHandlerHelper;
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerImageRequestHelper;
import org.fogbowcloud.arrebol.execution.docker.resource.DefaultDockerContainerResource;
import org.fogbowcloud.arrebol.execution.docker.resource.DockerContainerResource;
import org.fogbowcloud.arrebol.execution.docker.tasklet.DefaultTasklet;
import org.fogbowcloud.arrebol.execution.docker.tasklet.Tasklet;
import org.fogbowcloud.arrebol.execution.docker.tasklet.TaskletHelper;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.queue.spec.WorkerNode;
import org.fogbowcloud.arrebol.resource.MatchAnyWorker;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class DockerWorkerCreator implements WorkerCreator {

    private static final String TASK_SCRIPT_EXECUTOR_NAME = "task-script-executor.sh";
    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);
    private final String tsExecutorFileContent;
    private final DockerConfiguration configuration;

    public DockerWorkerCreator(Configuration configuration) throws Exception {
        this.configuration = new DockerConfiguration(configuration);
        Resource resource = new ClassPathResource(TASK_SCRIPT_EXECUTOR_NAME);
        try (InputStream is = resource.getInputStream()) {
            this.tsExecutorFileContent = IOUtils.toString(is, "UTF-8");
            LOGGER.debug("Task script executor content [" + this.tsExecutorFileContent + "]");
        }
    }

    @Override
    public Collection<Worker> createWorkers(Integer poolId) {
        Collection<Worker> workers = new LinkedList<>();
        int poolSize = configuration.getWorkerPoolSize();
        for (String address : configuration.getResourceAddresses()) {
            for (int i = 0; i < poolSize; i++) {
                LOGGER.info("Creating docker worker with address=" + address);
                Worker worker = createDockerWorker(poolId, i, address);
                workers.add(worker);
            }
        }
        return workers;
    }

    @Override
    public Collection<Worker> createWorkers(Integer poolId, WorkerNode workerNode) {
        Collection<Worker> workers = new LinkedList<>();
        for(int i = 0; i < workerNode.getWorkerPool(); i++){
            LOGGER.info("Creating docker worker with address=" + workerNode.getAddress());
            Worker worker = createDockerWorker(poolId, i, workerNode.getAddress());
            workers.add(worker);
        }
        return workers;
    }

    private Worker createDockerWorker(Integer poolId, int resourceId, String address) {
        String containerId = "docker-executor-" + UUID.randomUUID().toString();
        DockerContainerResource dockerContainerResource =
                createDockerContainerResource(address, containerId);
        Tasklet tasklet = createTasklet(address, containerId);
        TaskExecutor executor =
                new DockerTaskExecutor(
                        this.configuration.getImageId(), dockerContainerResource, tasklet);
        Specification resourceSpec = null;
        return new MatchAnyWorker(resourceSpec, "resourceId-" + resourceId, poolId, executor);
    }

    private DockerContainerResource createDockerContainerResource(
            String address, String containerId) {
        DockerImageRequestHelper imageRequestHelper =
                new DockerImageRequestHelper(address);
        DockerContainerRequestHelper containerRequestHelper =
                new DockerContainerRequestHelper(address, containerId);
        DockerContainerResource dockerContainerResource =
                new DefaultDockerContainerResource(
                        containerId, containerRequestHelper, imageRequestHelper);
        return dockerContainerResource;
    }

    private Tasklet createTasklet(String address, String containerId){
        DockerCommandExecutor dockerCommandExecutor = new DockerCommandExecutor();
        DockerFileHandlerHelper dockerFileHandlerHelper = new DockerFileHandlerHelper(address, dockerCommandExecutor);
        TaskletHelper taskletHelper = new TaskletHelper(address, containerId, dockerCommandExecutor, dockerFileHandlerHelper);
        Tasklet tasklet = new DefaultTasklet(this.tsExecutorFileContent, taskletHelper);
        return tasklet;
    }
}
