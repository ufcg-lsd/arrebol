package org.fogbowcloud.arrebol.execution.docker;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;

import static java.lang.Thread.sleep;

public class DockerCommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(DockerCommandExecutor.class);
    private final WorkerDockerRequestHelper workerDockerRequestHelper;
    private final String containerName;

    public DockerCommandExecutor(WorkerDockerRequestHelper workerDockerRequestHelper, String containerName) {
        this.workerDockerRequestHelper = workerDockerRequestHelper;
        this.containerName = containerName;
    }

    public Integer executeCommand(String command, String taskId) throws Exception {
        LOGGER.info("Sending command to the [" + command + "] for the task [" + taskId + "]"
                + this.containerName + "].");

        ExecInstanceResult execInstanceResult = executeCommand(command);

        LOGGER.info("Executed command [" + command + "] for the task [" + taskId
                + "] with exitcode=[" + execInstanceResult.getExitCode() + "] in worker ["
                + this.containerName + "].");
        return execInstanceResult.getExitCode();
    }

    private ExecInstanceResult executeCommand(String command) throws Exception {
        String execId = this.workerDockerRequestHelper.createExecInstance(command);
        this.workerDockerRequestHelper.startExecInstance(execId);

        ExecInstanceResult execInstanceResult =
                this.workerDockerRequestHelper.inspectExecInstance(execId);
        while (execInstanceResult.getExitCode() == null) {
            execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
            final long poolingPeriodTime = 300;

            try {
                sleep(poolingPeriodTime);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return execInstanceResult;
    }

    public void asyncExecuteCommand(String command, String taskId) throws Exception {
        LOGGER.info("Sending command to the [" + command + "] for the task [" + taskId + "]"
                + this.containerName + "].");

        String execId = this.workerDockerRequestHelper.createExecInstance(command);
        this.workerDockerRequestHelper.startExecInstance(execId);
    }

}
