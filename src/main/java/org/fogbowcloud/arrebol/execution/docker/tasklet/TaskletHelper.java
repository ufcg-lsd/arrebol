package org.fogbowcloud.arrebol.execution.docker.tasklet;

import java.util.List;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.docker.DockerCommandExecutor;
import org.fogbowcloud.arrebol.models.command.Command;

public class TaskletHelper {
    private static final String taskScriptExecutorFilePath = "/tmp/task-script-executor.sh";
    private static final String taskScriptFilePathPattern = "/tmp/%s.ts";
    private static final String ecFilePathPattern = "/tmp/%s.ts.ec";
    private final Logger LOGGER = Logger.getLogger(TaskletHelper.class);

    private String apiAddress;
    private String containerId;
    private DockerCommandExecutor dockerCommandExecutor;

    public TaskletHelper(String apiAddress, String containerId) {
        this.apiAddress = apiAddress;
        this.containerId = containerId;
        this.dockerCommandExecutor = new DockerCommandExecutor();
    }

    public void runTaskScriptExecutor(String taskId) throws Exception {
        String taskScriptFilePath = String.format(taskScriptFilePathPattern, taskId);
        this.dockerCommandExecutor.executeAsyncCommand(
                apiAddress,
                containerId,
                "/bin/bash " + taskScriptExecutorFilePath + " -d -tsf=" + taskScriptFilePath);
    }

    public void sendTaskScriptExecutor(String taskScriptExecutor) throws Exception {
        LOGGER.debug("Sending Task Script Executor to Docker Worker");
        String writeCommand = "echo '" + taskScriptExecutor + "' > " + taskScriptExecutorFilePath;
        try {
            int exitCode =
                    this.dockerCommandExecutor
                            .executeCommand(apiAddress, containerId, writeCommand)
                            .getExitCode();
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
        String taskScriptFilePath = String.format(taskScriptFilePathPattern, taskId);
        LOGGER.debug("Starting to write commands to ts file path [" + taskScriptFilePath + "].");
        int[] deliveryResults = writeCommandsToTaskScript(commands, taskScriptFilePath);

        for (int i = 0; i < commands.size(); i++) {
            if (deliveryResults[i] != 0) {
                throw new Exception(
                        "Error while trying to send command ["
                                + commands.get(i)
                                + "] exit code="
                                + deliveryResults[i]);
            }
        }
    }

    private int[] writeCommandsToTaskScript(List<Command> commands, String tsFilePath) {
        int[] exitCodes = new int[commands.size()];
        int i = 0;
        for (Command cmd : commands) {
            Integer exitCode;
            try {
                exitCode = writeToFile(cmd.getCommand(), tsFilePath);
            } catch (Throwable t) {
                exitCode = TaskExecutionResult.UNDETERMINED_RESULT;
            }
            exitCodes[i++] = exitCode;
        }
        return exitCodes;
    }

    private Integer writeToFile(String command, String file) throws Exception {
        return this.dockerCommandExecutor
                .executeCommand(apiAddress, containerId, "echo '" + command + "' >> " + file)
                .getExitCode();
    }

    public int[] getExitCodes(String taskId, Integer size) throws Exception {
        String ecFilePath = String.format(ecFilePathPattern, taskId);
        String commandToGetFile = String.format("cat %s", ecFilePath);
        String ecFileContent =
                this.dockerCommandExecutor.executeCommandWithStout(
                        apiAddress, containerId, commandToGetFile);
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
}
