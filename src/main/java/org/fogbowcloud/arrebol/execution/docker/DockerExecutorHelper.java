package org.fogbowcloud.arrebol.execution.docker;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;

import java.util.List;

public class DockerExecutorHelper {

    private final Logger LOGGER = Logger.getLogger(DockerExecutorHelper.class);
    private static final String TASK_SCRIPT_EXECUTOR = "/tmp/task-script-executor.sh";

    private final String taskScriptContent;
    private DockerCommandExecutor dockerCommandExecutor;
    private final WorkerDockerRequestHelper workerDockerRequestHelper;

    public DockerExecutorHelper(String taskScriptContent, WorkerDockerRequestHelper workerDockerRequestHelper) {
        this.taskScriptContent = taskScriptContent;
        this.workerDockerRequestHelper = workerDockerRequestHelper;
        this.dockerCommandExecutor = new DockerCommandExecutor(workerDockerRequestHelper);
    }

    public void runScriptExecutor(String taskId, String tsFilepath) throws Exception {
        this.dockerCommandExecutor.asyncExecuteCommand("/bin/bash " + TASK_SCRIPT_EXECUTOR + " -d -tsf=" + tsFilepath, taskId);
    }

    public void sendTaskScriptExecutor(String taskId) throws Exception {
        LOGGER.debug("Sending Task Script Executor to Docker Worker");
        String writeCommand = "echo '" + this.taskScriptContent + "' > " + TASK_SCRIPT_EXECUTOR;
        try {
            int exitCode = this.dockerCommandExecutor.executeCommand(writeCommand).getExitCode();
            if (exitCode != 0) {
                throw new Exception(
                        "Error while trying to execute send task script executor, exit code ["
                                + exitCode + "]");
            }
        } catch (Throwable e) {
            LOGGER.error(e);
            throw new Exception("Cannot send task script executor of ID=" + taskId + " to worker="
                    + this.workerDockerRequestHelper.getContainerName());
        }
    }

    public void sendTaskScript(List<Command> commands, String tsFilepath, String taskId)
            throws Exception {
        LOGGER.debug(
                "Starting to execute commands [len=" + commands.size() + "] of task " + taskId);

        int[] deliveryResults = writeCommandsToTsFile(commands, tsFilepath, taskId);

        for (int i = 0; i < commands.size(); i++) {
            if (deliveryResults[i] != 0) {
                throw new Exception("Error while trying to send command [" + commands.get(i)
                        + "] exit code=" + deliveryResults[i]);
            }
        }
    }

    public String getEcFile(String ecFilePath) throws Exception {
        String commandToGetFile = String.format("cat %s", ecFilePath);
        String execId = this.workerDockerRequestHelper.createExecInstance(commandToGetFile, true, true);
        String response = this.workerDockerRequestHelper.startExecInstance(execId).trim();
        ExecInstanceResult result = this.workerDockerRequestHelper.inspectExecInstance(execId);
        if(result.getExitCode() != 0){
            throw new RuntimeException("No zero exitcode [" + result.getExitCode() + "] to get ec file: " + response);
        }
        return response;
    }

    public int[] parseEcContentToArray(String ecContent, int size) {
        String[] strExitcodes = ecContent.split("\r\n");
        int[] exitcodes = new int[size];
        if(!ecContent.trim().isEmpty()){
            for (int i = 0; i < strExitcodes.length; i++) {
                exitcodes[i] = Integer.valueOf(strExitcodes[i]);
            }
        }
        for (int i = strExitcodes.length; i < size; i++) {
            exitcodes[i] = TaskExecutionResult.UNDETERMINED_RESULT;
        }
        return exitcodes;
    }

    private int[] writeCommandsToTsFile(List<Command> commands, String tsFilepath, String taskId) {
        int[] exitCodes = new int[commands.size()];
        int i = 0;
        for (Command cmd : commands) {
            Integer exitCode;
            try {
                exitCode = writeToFile(cmd.getCommand(), tsFilepath);
            } catch (Throwable t) {
                exitCode = TaskExecutionResult.UNDETERMINED_RESULT;
            }
            exitCodes[i++] = exitCode;
        }
        return exitCodes;
    }

    private Integer writeToFile(String command, String file) throws Exception {
        return this.dockerCommandExecutor.executeCommand("echo '" + command + "' >> " + file).getExitCode();
    }
}
