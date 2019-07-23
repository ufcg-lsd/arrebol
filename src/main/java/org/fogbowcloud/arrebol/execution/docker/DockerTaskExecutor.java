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

    private static final long poolingPeriodTime = 2000;
    private static final String taskScriptFilePathPattern = "/tmp/%s/.ts";
    private static final String ecFilePathPattern = "/tmp/%s/.ts.ec";

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

        try {
            startContainer(task);
            containerEnvironmentSetup(task.getId(), task.getTaskSpec().getCommands());
            LOGGER.debug("Starting to execute commands [len=" + task.getTaskSpec().getCommands().size() + "] of task " + task.getId());
            runScript(task.getId());
            checkTask(task);
            stopContainer();
        } catch (DockerRemoveContainerException de) {
            LOGGER.error(de.getMessage(), de);
        } catch (Throwable t) {
            LOGGER.error("Set task [" + task.getId() + "] to FAILED [" + t.getMessage() + "]", t);
            setNotFinishedToFailed(task.getTaskSpec().getCommands());
        } finally {
            List<Command> commands = task.getTaskSpec().getCommands();
            taskExecutionResult = new TaskExecutionResult(getTaskResult(commands),
                getExitCodes(commands));
            LOGGER.debug("Result of task [" + task.getId() + "]: "
                + taskExecutionResult.getResult().toString());
            return taskExecutionResult;
        }

    }

    /**
     * Sends the executor task script, sends the file with the task commands and executes the
     * executor task script
     */
    private String containerEnvironmentSetup (String taskId, List<Command> commands) throws Exception {
        this.dockerExecutorHelper.sendTaskScriptExecutor();
        LOGGER.debug(
            "Starting to write commands [len=" + commands.size() + "] of task " + taskId
                + " to .ts file.");
        String taskScriptFilePath = String.format(taskScriptFilePathPattern, taskId);
        this.dockerExecutorHelper.writeTaskScript(commands, taskScriptFilePath);
        return taskScriptFilePath;
    }

    private void runScript(String taskId) throws Exception {
        String taskScriptFilepath = String.format(taskScriptFilePathPattern, taskId);
        this.dockerExecutorHelper.runScriptExecutor(taskScriptFilepath);
    }

    /**
     * Check the state of the commands until all are finalized
     */
    private void checkTask(Task task) throws Exception {
        List<Command> commands = task.getTaskSpec().getCommands();
        String ecFilePath = String.format(ecFilePathPattern, task.getId());
        updateCommandsState(commands, ecFilePath);
    }

    /**
     * Start the container considering task specifications
     */
    private void startContainer(Task task) throws UnsupportedEncodingException {
        LOGGER.info(
            "Starting DockerTaskExecutor " + this.workerDockerRequestHelper.getContainerName());
        this.workerDockerRequestHelper.start(task.getTaskSpec());
        LOGGER.debug(
            "Container " + getContainerName() + " started successfully for task " + task.getId());
    }

    private void stopContainer() throws DockerRemoveContainerException {
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
        int currentIndex = 0;
        while (currentIndex < cmds.size()) {
            Command cmd = cmds.get(currentIndex);
            cmd.setState(CommandState.RUNNING);
            updateCommandsState(cmds, ecFilepath, currentIndex);
            try {
                sleep(poolingPeriodTime);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
    }

    private int updateCommandsState(List<Command> cmds, String ecFilepath, int startIndex)
        throws Exception {
        int lastIndex;
        int[] exitcodes = getExitCodeFromEcFile(ecFilepath, cmds.size());
        lastIndex = syncUntilTheLastCmdFinished(cmds, exitcodes, startIndex);
        LOGGER.debug("After sync waiting for index [" + lastIndex + "]");
        return lastIndex;
    }

    private int[] getExitCodeFromEcFile(String ecFilePath, int size) throws Exception {
        String ecContent = this.dockerExecutorHelper.getEcFile(ecFilePath);
        LOGGER.debug("Exit code file content [" + ecContent + "]");

        int[] exitcodes = this.dockerExecutorHelper.parseEcContentToArray(ecContent, size);
        LOGGER.debug("Exits codes array [" + Arrays.toString(exitcodes) + "]");
        return exitcodes;
    }

    private Integer syncUntilTheLastCmdFinished(List<Command> cmds, int[] exitcodes,
        int startIndex) {
        int currentIndex = startIndex;
        while (currentIndex < cmds.size()) {
            int exitCode = exitcodes[currentIndex];
            Command cmd = cmds.get(currentIndex);
            boolean hasFinished = updateCommandState(cmd, exitCode);
            if (hasFinished) {
                currentIndex++;
            } else {
                break;
            }
        }
        return currentIndex;
    }

    /**
     * Checks if the command has finished, if finished changes its state to finished, sets its exit
     * code and returns true.
     */
    private boolean updateCommandState(Command command, int exitcode) {
        boolean hasFinished = !isUndetermined(exitcode);
        if (hasFinished) {
            command.setState(CommandState.FINISHED);
            command.setExitcode(exitcode);
        }
        return hasFinished;
    }

    private boolean isUndetermined(int exitcode) {
        boolean isUndetermined = false;
        if (exitcode == TaskExecutionResult.UNDETERMINED_RESULT) {
            isUndetermined = true;
        }
        return isUndetermined;
    }

    private TaskExecutionResult.RESULT getTaskResult(List<Command> commands) {
        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for (Command cmd : commands) {
            if (cmd.getState().equals(CommandState.FAILED)) {
                result = TaskExecutionResult.RESULT.FAILURE;
                break;
            }
        }
        return result;
    }

    private int[] getExitCodes(List<Command> commands) {
        int[] exitcodes = new int[commands.size()];
        for (int i = 0; i < commands.size(); i++) {
            exitcodes[i] = commands.get(i).getExitcode();
        }
        return exitcodes;
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
