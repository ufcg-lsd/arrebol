package org.fogbowcloud.arrebol.execution;

import org.fogbowcloud.arrebol.execution.remoteWorker.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.task.Task;

public class RemoteDockerTaskExecutor implements TaskExecutor {

    private String imageId;
    private String containerName;
    private String address;
    private WorkerDockerRequestHelper workerDockerRequestHelper;

    public RemoteDockerTaskExecutor(String imageId, String containerName, String address) {
        this.imageId = imageId;
        this.containerName = containerName;
        this.address = address;
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName, imageId);
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        return null;
    }


}
