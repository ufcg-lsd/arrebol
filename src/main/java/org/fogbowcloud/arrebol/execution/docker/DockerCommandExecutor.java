package org.fogbowcloud.arrebol.execution.docker;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;

import static java.lang.Thread.sleep;

public class DockerCommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(DockerCommandExecutor.class);
    private final WorkerDockerRequestHelper workerDockerRequestHelper;

    public DockerCommandExecutor(WorkerDockerRequestHelper workerDockerRequestHelper) {
        this.workerDockerRequestHelper = workerDockerRequestHelper;
    }

    public Integer executeCommand(String command) throws Exception {
        LOGGER.info("Sending command [" + command + "] to the [" + this.workerDockerRequestHelper.getContainerName() + "].");

        ExecInstanceResult execInstanceResult = executeCommandInWorker(command);

        LOGGER.info("Executed command [" + command + "] + with exitcode=[" + execInstanceResult.getExitCode() + "] in worker ["
            + this.workerDockerRequestHelper.getContainerName() + "].");
        return execInstanceResult.getExitCode();
    }

    private ExecInstanceResult executeCommandInWorker(String command) throws Exception {
        String execId = this.workerDockerRequestHelper.createExecInstance(command, false, false);
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
            + this.workerDockerRequestHelper.getContainerName() + "].");

        String execId = this.workerDockerRequestHelper.createExecInstance(command, false, false);
        this.workerDockerRequestHelper.startExecInstance(execId);
    }

}
