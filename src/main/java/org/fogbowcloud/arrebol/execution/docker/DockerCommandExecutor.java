package org.fogbowcloud.arrebol.execution.docker;

import java.util.Objects;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;

import static java.lang.Thread.sleep;

public class DockerCommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(DockerCommandExecutor.class);
    private final WorkerDockerRequestHelper workerDockerRequestHelper;

    private final long poolingPeriodTime = 300;

    public DockerCommandExecutor(WorkerDockerRequestHelper workerDockerRequestHelper) {
        this.workerDockerRequestHelper = workerDockerRequestHelper;
    }

    /**
     * It creates the command execution instance, sends it to the docker and each period of time
     * {@link DockerCommandExecutor#poolingPeriodTime} checks if exit code already exists. If exists
     * then returns an {@link ExecInstanceResult}.
     */
    public ExecInstanceResult executeCommand(String command) throws Exception {
        LOGGER.info("Sending command [" + command + "] to the [" + this.workerDockerRequestHelper
            .getContainerName() + "].");

        String execId = this.workerDockerRequestHelper.createExecInstance(command, false, false);
        this.workerDockerRequestHelper.startExecInstance(execId);

        ExecInstanceResult execInstanceResult =
            this.workerDockerRequestHelper.inspectExecInstance(execId);
        while (Objects.isNull(execInstanceResult.getExitCode())) {
            execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
            try {
                sleep(poolingPeriodTime);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        LOGGER.info("Executed command [" + command + "] + with exitcode=[" + execInstanceResult
            .getExitCode() + "] in worker ["
            + this.workerDockerRequestHelper.getContainerName() + "].");
        return execInstanceResult;
    }

    /**
     * It creates the command execution instance and sends it to the docker, without waiting for the
     * end of execution and nor for its exit code.
     */
    public void asyncExecuteCommand(String command) throws Exception {
        LOGGER.info(
            "Sending command [" + command + "] to the container [" + this.workerDockerRequestHelper
                .getContainerName() + "].");

        String execId = this.workerDockerRequestHelper.createExecInstance(command, false, false);
        this.workerDockerRequestHelper.startExecInstance(execId);
    }

}
