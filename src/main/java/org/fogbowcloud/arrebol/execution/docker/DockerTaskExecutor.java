package org.fogbowcloud.arrebol.execution.docker;

import static java.lang.Thread.sleep;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;

/**
 * This implementation of {@link TaskExecutor} manages the execution of a {@link Task} in a, possible remote,
 * DockerContainer. A new container is created to execute every {@link Task} and destroy after the execution
 * has finished. A task representation is sent to the container, which drives the execution of the
 * commands itself. This objects monitors the execution until it ends on success or failure. If any container
 * initialization error occurs, the return {@link TaskExecutionResult} indicates the Failure.
 */
public class DockerTaskExecutor implements TaskExecutor {

    private static final long poolingPeriodTimeMs = 2000;
    private static final String taskScriptFilePathPattern = "/tmp/%s.ts";
    private static final String ecFilePathPattern = "/tmp/%s.ts.ec";

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private DockerExecutorHelper dockerExecutorHelper;

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
     * {@inheritDoc}
     */
    @Override
    public TaskExecutionResult execute(Task task) {

        // It has function to execute a task. First, the container is started. A task may have
        //requirements such as docker image, which are set at that time. Next, a bash script called
        //task-script-executor is sent, which will execute all commands internally in the container.
        //All the commands of the task are written to a .ts file inside the container, and then the
        //task-script-executor reads this file, executes line by line and each executed command is
        //written its exit code in the .ts.ec file. Execution ends only when the number of ec in the
        //file corresponds to the number of commands. At the end of execution, the result is returned
        //and the container is killed

        // FIXME: also, follow the SAME log format we used in the RawTaskExecutor
        TaskExecutionResult taskExecutionResult;

        try {
            List<Command> commands = task.getTaskSpec().getCommands();
            startContainer(task);
            setupContainerEnvironment(task.getId(), commands);
            LOGGER.debug(
                "Starting to execute commands [len=" + commands.size() + "] of task " + task
                    .getId());
            runScript(task.getId());
            trackTaskExecution(task.getId(), commands);
            stopContainer();
        } catch (DockerRemoveContainerException de) {
            LOGGER.error(de.getMessage(), de);
        } catch (Throwable t) {
            LOGGER.error("Set task [" + task.getId() + "] to FAILED [" + t.getMessage() + "]", t);
            failUnfinishedCommands(task.getTaskSpec().getCommands());
        } finally {
            List<Command> commands = task.getTaskSpec().getCommands();
            taskExecutionResult = new TaskExecutionResult(getTaskResult(commands),
                getExitCodes(commands));
            LOGGER.debug("Result of task [" + task.getId() + "]: "
                + taskExecutionResult.getResult().toString());
        }

        return taskExecutionResult;
    }

    private void setupContainerEnvironment(String taskId, List<Command> commands)
        throws Exception {

        //Sends the executor task script and write task commands inside the .ts file
        this.dockerExecutorHelper.sendTaskExecutorScript();
        LOGGER.debug(
            "Starting to write commands [len=" + commands.size() + "] of task " + taskId
                + " to .ts file.");
        String taskScriptFilePath = String.format(taskScriptFilePathPattern, taskId);
        this.dockerExecutorHelper.writeTaskScript(commands, taskScriptFilePath);
    }

    private void runScript(String taskId) throws Exception {
        String taskScriptFilepath = String.format(taskScriptFilePathPattern, taskId);
        this.dockerExecutorHelper.runExecutorScript(taskScriptFilepath);
    }

    /**
     * It reads the .ts.ec file and then updates the states of the commands, performing this update
     * each period of time {@link DockerTaskExecutor#poolingPeriodTimeMs} until the result of all
     * the commands.
     */
    private void trackTaskExecution(String taskId, List<Command> commands) throws Exception {
        String ecFilePath = String.format(ecFilePathPattern, taskId);
        int currentIndex = 0;
        while (currentIndex < commands.size()) {
            Command cmd = commands.get(currentIndex);
            cmd.setState(CommandState.RUNNING);
            currentIndex = updateCommandsState(commands, ecFilePath, currentIndex);
            LOGGER.debug("After sync waiting for index [" + currentIndex + "]");
            try {
                sleep(poolingPeriodTimeMs);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
    }

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

    private int updateCommandsState(List<Command> cmds, String ecFilepath, int startIndex)
        throws Exception {
        int[] exitcodes = getExitCodeFromEcFile(ecFilepath, cmds.size());
        int lastIndex = syncUntilTheLastCmdFinished(cmds, exitcodes, startIndex);
        return lastIndex;
    }

    private int[] getExitCodeFromEcFile(String ecFilePath, int size) throws Exception {
        String ecContent = this.dockerExecutorHelper.getEcFile(ecFilePath);
        LOGGER.debug("Exit code file content [" + ecContent + "]");

        int[] exitcodes = this.dockerExecutorHelper.parseEcContentToArray(ecContent, size);
        LOGGER.debug("Exits codes array [" + Arrays.toString(exitcodes) + "]");
        return exitcodes;
    }

    /**
     * Step through the commands and update their state according to their respective exitcode, if
     * your exit code is equal to {@link TaskExecutionResult#UNDETERMINED_RESULT}, the iteration is stopped
     * and the current index is returned.
     */
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

    private void failUnfinishedCommands(List<Command> commands) {
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
