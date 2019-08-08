package org.fogbowcloud.arrebol.execution.docker.tasklet;

import java.util.List;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.docker.DockerCommandExecutor;
import org.fogbowcloud.arrebol.execution.docker.request.DockerFileHandlerHelper;
import org.fogbowcloud.arrebol.models.command.Command;

public class TaskletHelper {
    private static final String TASK_SCRIPT_EXECUTOR_FILE_PATH = "/tmp/task-script-executor.sh";
    private static final String TASK_SCRIPT_FILE_PATH_PATTERN = "/tmp/%s.ts";
    private static final String EC_FILE_PATH_PATTERN = "/tmp/%s.ts.ec";
    private static final String RUN_TS_EXECUTOR_PATTERN = "/bin/bash %s -d -tsf=%s";
    private final Logger LOGGER = Logger.getLogger(TaskletHelper.class);

    private String apiAddress;
    private String containerId;
    private DockerCommandExecutor dockerCommandExecutor;
    private DockerFileHandlerHelper dockerFileHandlerHelper;

    public TaskletHelper(String apiAddress, String containerId) {
        this.apiAddress = apiAddress;
        this.containerId = containerId;
        this.dockerCommandExecutor = new DockerCommandExecutor();
        this.dockerFileHandlerHelper =
                new DockerFileHandlerHelper(apiAddress, dockerCommandExecutor);
    }

    public void runTaskScriptExecutor(String taskId) throws Exception {
        String taskScriptExecutorFilePath = String.format(TASK_SCRIPT_FILE_PATH_PATTERN, taskId);
        String command =
                String.format(
                        RUN_TS_EXECUTOR_PATTERN,
                        TASK_SCRIPT_EXECUTOR_FILE_PATH,
                        taskScriptExecutorFilePath);
        this.dockerCommandExecutor.executeAsyncCommand(apiAddress, containerId, command);
    }

    public void sendTaskScriptExecutor(String taskScriptExecutor) throws Exception {
        LOGGER.debug("Sending Task Script Executor to Docker Worker");
        try {
            int exitCode =
                    dockerFileHandlerHelper.writeToFile(
                            containerId, taskScriptExecutor, TASK_SCRIPT_EXECUTOR_FILE_PATH);
            if (exitCode != 0) {
                throw new Exception(
                        "Error while trying to execute send task script executor, exit code ["
                                + exitCode
                                + "]");
            }
        } catch (Throwable e) {
            LOGGER.error(e);
            throw new Exception("Cannot send task script executor to worker=" + containerId);
        }
    }

    public void sendTaskScript(String taskId, List<Command> commands) throws Exception {
        String taskScriptFilePath = String.format(TASK_SCRIPT_FILE_PATH_PATTERN, taskId);
        String taskScriptContent = commandsToString(commands);
        LOGGER.debug("Starting to write commands to ts file path [" + taskScriptFilePath + "].");
        try {
            int exitCode =
                    dockerFileHandlerHelper.writeToFile(
                            containerId, taskScriptContent, taskScriptFilePath);
            if (exitCode != 0) {
                throw new Exception(
                        "Error while trying to send task script ["
                                + taskId
                                + "] to container ["
                                + containerId
                                + "]; exit code ["
                                + exitCode
                                + "]");
            }
        } catch (Throwable e) {
            LOGGER.error(e);
            throw new Exception(
                    "Cannot send task script [" + taskId + "] to container [" + containerId + "]");
        }
    }

    private String commandsToString(List<Command> commands) {
        String result = "";
        for (Command c : commands) {
            result += c.getCommand() + "\n";
        }
        result = result.trim();
        return result;
    }

    public int[] getExitCodes(String taskId, Integer size) throws Exception {
        String ecFilePath = String.format(EC_FILE_PATH_PATTERN, taskId);
        String ecFileContent = this.dockerFileHandlerHelper.readFile(containerId, ecFilePath);
        String[] strExitcodes = ecFileContent.split("\r\n");
        int[] exitcodes = new int[size];
        if (!ecFileContent.trim().isEmpty()) {
            for (int i = 0; i < strExitcodes.length; i++) {
                exitcodes[i] = Integer.valueOf(strExitcodes[i]);
            }
        }
        for (int i = strExitcodes.length; i < size; i++) {
            exitcodes[i] = TaskExecutionResult.UNDETERMINED_RESULT;
        }
        return exitcodes;
    }

    public void setDockerCommandExecutor(DockerCommandExecutor dockerCommandExecutor) {
        this.dockerCommandExecutor = dockerCommandExecutor;
    }
}
