package org.fogbowcloud.arrebol.execution.docker;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.Thread.sleep;

public class DockerTaskExecutor implements TaskExecutor {

    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int FAIL_EXIT_CODE = 127;

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private DockerExecutorHelper dockerExecutorHelper;

    public DockerTaskExecutor(String containerName, String address, String taskScriptContent, String defaultImageId) {
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName, defaultImageId);
        this.dockerExecutorHelper = new DockerExecutorHelper(taskScriptContent,
            this.workerDockerRequestHelper);
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        // FIXME: We should catch the errors when starting/finishing the container and move the task
        // to its FAILURE state
        // FIXME: also, follow the SAME log format we used in the RawTaskExecutor
        TaskExecutionResult taskExecutionResult;
        Integer startStatus = FAIL_EXIT_CODE;

        try {
            startExecution(task);
            setupAndRun(task);
            checkTask(task);
            stopExecution();
        } catch (DockerRemoveContainerException de) {
            LOGGER.error(de);
            LOGGER.error("Failed to stop container with name " + this.workerDockerRequestHelper
                .getContainerName() + " with exit code " + FAIL_EXIT_CODE);
        } catch (Throwable t) {
            LOGGER.error(t);
            LOGGER.error("Set task [" + task.getId() + "] to FAILED [" + t.getMessage() + "]");
            LOGGER.error("Exit code from container start: " + startStatus);
            setNotFinishedToFailed(task.getTaskSpec().getCommands());
        } finally {
            taskExecutionResult = getTaskResult(task.getTaskSpec().getCommands());
            LOGGER.debug("Result of task [" + task.getId() + "]: "
                + taskExecutionResult.getResult().toString());
            return taskExecutionResult;
        }

    }

    protected void setupAndRun(Task task) throws Exception {
        List<Command> commands = task.getTaskSpec().getCommands();
        this.dockerExecutorHelper.sendTaskScriptExecutor(task.getId());
        String taskScriptFilepath = "/tmp/" + task.getId() + ".ts";
        this.dockerExecutorHelper.sendTaskScript(commands, taskScriptFilepath,
            task.getId());
        this.dockerExecutorHelper.runScriptExecutor(task.getId(), taskScriptFilepath);
    }

    private void checkTask(Task task) throws Exception {
        List<Command> commands = task.getTaskSpec().getCommands();
        final String EC_FILEPATH = "/tmp/" + task.getId() + ".ts.ec";
        updateCommandsState(commands, EC_FILEPATH);
    }

    private void startExecution(Task task) throws UnsupportedEncodingException {
        LOGGER.info(
            "Starting DockerTaskExecutor " + this.workerDockerRequestHelper.getContainerName());
        this.workerDockerRequestHelper.start(task.getTaskSpec());
        LOGGER.debug(
            "Container " + getContainerName() + " started successfully for task " + task.getId());
    }

    private void stopExecution() throws DockerRemoveContainerException {
        LOGGER.info(
            "Stopping DockerTaskExecutor " + this.workerDockerRequestHelper.getContainerName());
        this.workerDockerRequestHelper.stopContainer();
    }

    private void updateCommandsState(List<Command> cmds, String ecFilepath) throws Exception {
        final long poolingPeriodTime = 2000;
        int nextRunningIndex = 0;
        while (nextRunningIndex < cmds.size()) {
            Command cmd = cmds.get(nextRunningIndex);
            cmd.setState(CommandState.RUNNING);

            String ecContent = this.dockerExecutorHelper.getEcFile(ecFilepath);
            LOGGER.debug("Exit code file content [" + ecContent + "]");

            int[] exitcodes =
                this.dockerExecutorHelper.parseEcContentToArray(ecContent, cmds.size());
            LOGGER.debug("Exits codes array [" + Arrays.toString(exitcodes) + "]");

            nextRunningIndex = syncCommandsWithEC(cmds, exitcodes, nextRunningIndex);
            LOGGER.debug("After sync waiting for index [" + nextRunningIndex + "]");

            try {
                sleep(poolingPeriodTime);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
    }

    private Integer syncCommandsWithEC(List<Command> cmds, int[] exitcodes, int nextRunningIndex) {
        while (nextRunningIndex < cmds.size()) {
            int exitCode = exitcodes[nextRunningIndex];
            Command cmd = cmds.get(nextRunningIndex);
            if (evaluateCommand(cmd, exitCode)) {
                nextRunningIndex++;
            } else {
                break;
            }
        }
        return nextRunningIndex;
    }

    private boolean evaluateCommand(Command command, int exitcode) {
        boolean isRunning = true;
        if (exitcode != TaskExecutionResult.UNDETERMINED_RESULT) {
            command.setState(CommandState.FINISHED);
            command.setExitcode(exitcode);
            isRunning = false;
        }
        return !isRunning;
    }

    private TaskExecutionResult getTaskResult(List<Command> commands) {
        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for (Command cmd : commands) {
            if (cmd.getState().equals(CommandState.FAILED)) {
                result = TaskExecutionResult.RESULT.FAILURE;
                break;
            }
        }
        return new TaskExecutionResult(result, new Command[commands.size()]);
    }

    private void setNotFinishedToFailed(List<Command> commands) {
        for (Command c : commands) {
            if (!c.getState().equals(CommandState.FINISHED)) {
                c.setState(CommandState.FAILED);
                c.setExitcode(TaskExecutionResult.UNDETERMINED_RESULT);
            }
        }
    }

    protected void setWorkerDockerRequestHelper(
        WorkerDockerRequestHelper workerDockerRequestHelper) {
        this.workerDockerRequestHelper = workerDockerRequestHelper;
    }

    protected void setDockerExecutorHelper(
        DockerExecutorHelper dockerExecutorHelper) {
        this.dockerExecutorHelper = dockerExecutorHelper;
    }

    private String getContainerName() {
        return this.workerDockerRequestHelper.getContainerName();
    }
}
