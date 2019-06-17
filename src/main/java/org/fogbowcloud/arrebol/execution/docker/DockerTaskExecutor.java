package org.fogbowcloud.arrebol.execution.docker;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.Thread.sleep;

public class DockerTaskExecutor implements TaskExecutor {

    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int FAIL_EXIT_CODE = 127;

    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);
    private final String tsContent;
    private static final String WORKER_TS_FILEPATH = "/tmp/task-script-executor.sh"; 

    public DockerTaskExecutor(String containerName, String address, String tsContent) {
        this.tsContent = tsContent;
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName);
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        // FIXME: We should catch the errors when starting/finishing the container and move the task
        // to its FAILURE state
        // FIXME: also, follow the SAME log format we used in the RawTaskExecutor
        TaskExecutionResult taskExecutionResult;

        Integer startStatus = this.start(task);

        // TODO: sync execution state by exit codes

        if (startStatus != SUCCESS_EXIT_CODE) {
            LOGGER.error("Exit code from container start: " + startStatus);
            throw new DockerStartException("Could not start container " + getContainerName());
        } else {
            LOGGER.debug("Container " + getContainerName() + " started successfully for task "
                    + task.getId());
            
            List<Command> commands = task.getTaskSpec().getCommands();
            try {
                sendTaskScriptExecutor(task.getId());
                
                String taskScriptFilepath = "/tmp/" + task.getId() + ".ts";
                sendTaskScript(commands, taskScriptFilepath, task.getId());
                
                runScriptExecutor(task.getId(), taskScriptFilepath);
            } catch (Throwable e) {
                LOGGER.error(e);
                for (Command cmd : commands) {
                    cmd.setState(CommandState.FAILED);
                    cmd.setExitcode(TaskExecutionResult.UNDETERMINED_RESULT);
                }
            }

            Integer stopStatus = this.stop();
            if (stopStatus != SUCCESS_EXIT_CODE) {
                LOGGER.error("Exit code from container " + getContainerName()
                        + " stopped for the task " + task.getId() + " : " + stopStatus);
            }

            taskExecutionResult = getTaskResult(commands);

            LOGGER.debug("Result of task [" + task.getId() + "]: "
                    + taskExecutionResult.getResult().toString());
            return taskExecutionResult;
        }
    }
    
    private void runScriptExecutor(String taskId, String tsFilepath) throws Exception {
        asyncExecuteCommand("/bin/bash " + WORKER_TS_FILEPATH + " -d -tsf=" + tsFilepath, taskId);
    }
    
    private void sendTaskScriptExecutor(String taskId) throws Exception {
        LOGGER.debug("Sending Task Script Executor to Docker Worker");
        try {
            int exitCode = executeCommand(
                    "echo '" + this.tsContent + "' > " + WORKER_TS_FILEPATH, taskId);
            if (exitCode != 0) {
                throw new Exception(
                        "Error while trying to execute send task script executor, exit code ["
                                + exitCode + "]");
            }
        } catch (Throwable e) {
            LOGGER.error(e);
            throw new Exception("Cannot send task script executor of ID=" + taskId + " to worker="
                    + getContainerName());
        }
    }

    private void sendTaskScript(List<Command> commands, String tsFilepath, String taskId)
            throws Exception {
        LOGGER.debug(
                "Starting to execute commands [len=" + commands.size() + "] of task " + taskId);

        int[] deliveryResults = executeCommands(commands, "echo '", "' >> " + tsFilepath, taskId);

        for (int i = 0; i < commands.size(); i++) {
            if (deliveryResults[i] != 0) {
                throw new Exception("Error while trying to send command [" + commands.get(i)
                        + "] exit code=" + deliveryResults[i]);
            }
        }
    }

    private int[] executeCommands(List<Command> commands, String cmdPrefix, String cmdSuffix, String taskId) {
        int[] exitCodes = new int[commands.size()];
        int i = 0;
        for(Command cmd : commands) {
            Integer exitCode;
            try {
                exitCode = executeCommand(cmdPrefix + cmd.getCommand() + cmdSuffix, taskId);
            } catch (Throwable t) {
                exitCode = TaskExecutionResult.UNDETERMINED_RESULT;
            }
            exitCodes[i++] = exitCode;
        }
        return exitCodes;
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

    private Integer start(Task task) {
        try {
            LOGGER.info("Starting DockerTaskExecutor " + this.getContainerName());
            this.workerDockerRequestHelper.start(task.getTaskSpec());
            return SUCCESS_EXIT_CODE;
        } catch (DockerStartException | DockerCreateContainerException de) {
            LOGGER.info("Set task [" + task.getId() + "] to FAILED because a container error [" + de.getMessage() + "]");
            task.setState(TaskState.FAILED);
            return FAIL_EXIT_CODE;
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Set task [" + task.getId() + "] to FAILED [" + e.getMessage() + "]");
            task.setState(TaskState.FAILED);
            return FAIL_EXIT_CODE;
        }
    }

    private Integer stop() {
        try {
            LOGGER.info("Stopping DockerTaskExecutor " + this.getContainerName());
            this.workerDockerRequestHelper.stop();
            return SUCCESS_EXIT_CODE;
        } catch (DockerRemoveContainerException de){
            LOGGER.error("Failed to stop container with name " + this.getContainerName() +
                    " with exit code " + FAIL_EXIT_CODE);

            return FAIL_EXIT_CODE;
        }
    }

    private Integer executeCommand(String command, String taskId) throws Exception {
        LOGGER.info("Sending command to the [" + command + "] for the task [" + taskId + "]"
                + this.getContainerName() + "].");

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
        LOGGER.info("Executed command [" + command + "] for the task [" + taskId
                + "] with exitcode=[" + execInstanceResult.getExitCode() + "] in worker ["
                + this.getContainerName() + "].");
        return execInstanceResult.getExitCode();
    }
    
    private void asyncExecuteCommand(String command, String taskId) throws Exception {
        LOGGER.info("Sending command to the [" + command + "] for the task [" + taskId + "]"
                + this.getContainerName() + "].");

        String execId = this.workerDockerRequestHelper.createExecInstance(command);
        this.workerDockerRequestHelper.startExecInstance(execId);
    }

    private String getContainerName() {
        return this.workerDockerRequestHelper.getContainerName();
    }

}
