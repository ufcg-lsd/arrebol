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

/**
 * This class consists of an entity of remote execution of tasks using docker containers. At each
 * execution a new container is started and after the execution of the task is finished the
 * container is removed. The execution of each command is not done directly by this class but by a
 * bash script sent into the container. In cases where container initialization error occurs, the
 * commands go to failure state and a failure result is returned.
 */
public class DockerTaskExecutor implements TaskExecutor {

    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int FAIL_EXIT_CODE = 127;
    private static final long poolingPeriodTime = 2000;

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private final DockerExecutorHelper dockerExecutorHelper;

    /**
     * @param containerName Sets the name of the container, is an identifier.
     * @param address Defines the address where requests for the Docker API should be made
     * @param taskScriptContent Script that will be sent to the container with the function to
     * execute the commands
     * @param defaultImageId Image docker used as default if no one is specified in the task.
     */
    public DockerTaskExecutor(String containerName, String address, String taskScriptContent,
        String defaultImageId) {
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName,
            defaultImageId);
        this.dockerExecutorHelper = new DockerExecutorHelper(taskScriptContent,
            this.workerDockerRequestHelper);
    }

    /**
     * It has function to execute a task. First, the container is started. A task may have
     * requirements such as docker image, which are set at that time. Next, a bash script called
     * task-script-executor is sent, which will execute all commands internally in the container.
     * All the commands of the task are written to a .ts file inside the container, and then the
     * task-script-executor reads this file, executes line by line and each executed command is
     * written its exit code in the .ts.ec file. Execution ends only when the number of ec in the
     * file corresponds to the number of commands. At the end of execution, the result is returned
     * and the container is killed
     */
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

    /**
     * Sends the executor task script, sends the file with the task commands and executes the
     * executor task script
     */
    private void setupAndRun(Task task) throws Exception {
        List<Command> commands = task.getTaskSpec().getCommands();
        this.dockerExecutorHelper.sendTaskScriptExecutor(task.getId());
        String taskScriptFilepath = "/tmp/" + task.getId() + ".ts";
        this.dockerExecutorHelper.sendTaskScript(commands, taskScriptFilepath,
            task.getId());
        this.dockerExecutorHelper.runScriptExecutor(task.getId(), taskScriptFilepath);
    }

    /**
     * Check the state of the commands until all are finalized
     */
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

    /**
     * It reads the exit codes file and then updates the states of the commands, performing this
     * update each period of time {@link DockerTaskExecutor#poolingPeriodTime} until the result of
     * all the commands.
     */
    private void updateCommandsState(List<Command> cmds, String ecFilepath) throws Exception {
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

    private String getContainerName() {
        return this.workerDockerRequestHelper.getContainerName();
    }
}
