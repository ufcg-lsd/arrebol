package org.fogbowcloud.arrebol.execution.docker;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.NotFoundDockerImage;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.fogbowcloud.arrebol.models.task.TaskState;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class DockerTaskExecutor implements TaskExecutor {

    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int FAIL_EXIT_CODE = 127;

    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    public DockerTaskExecutor(String containerName, String address) {
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName);
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        //FIXME: We should catch the errors when starting/finishing the container and move the task to its FAILURE state
        //FIXME: also, follow the SAME log format we used in the RawTaskExecutor
        TaskExecutionResult taskExecutionResult;

        Integer startStatus = this.start(task);

        if (startStatus != SUCCESS_EXIT_CODE) {
            LOGGER.error("Exit code from container start: " + startStatus);
            throw new DockerStartException("Could not start container " + getContainerName());
        } else {
            LOGGER.info("Container " + getContainerName() + " started successfully for task " + task.getId());
            Command[] commands = getCommands(task);
            LOGGER.info("Starting to execute commands [len=" + commands.length + "] of task " + task.getId());
            int[] commandsResults = executeCommands(commands, task.getId());

            Integer stopStatus = this.stop();
            if (stopStatus != SUCCESS_EXIT_CODE) {
                LOGGER.error("Exit code from container " + getContainerName() + " stopped for the task "
                        + task.getId() + " : " + stopStatus);
            }

            taskExecutionResult = getTaskResult(commands, commandsResults);

            LOGGER.info("Result of task [" + task.getId() + "]: " + taskExecutionResult.getResult().toString());
            return taskExecutionResult;
        }
    }

    private Command[] getCommands(Task task) {
        TaskSpec taskSpec = task.getTaskSpec();
        List<Command> commandsList = taskSpec.getCommands();

        int commandsSize = commandsList.size();
        return commandsList.toArray(new Command[commandsSize]);
    }

    private int[] executeCommands(Command[] commands, String taskId) {
        int[] commandsResults = new int[commands.length];
        Arrays.fill(commandsResults, TaskExecutionResult.UNDETERMINED_RESULT);
        for (int i = 0; i < commands.length; i++) {
            Command c = commands[i];
            c.setState(CommandState.RUNNING);
            try {
                Integer exitCode = executeCommand(c, taskId);
                commandsResults[i] = exitCode;
                c.setExitcode(exitCode);
                c.setState(CommandState.FINISHED);
            } catch (Throwable t) {
                c.setState(CommandState.FAILED);
            }
        }
        return commandsResults;
    }

    private TaskExecutionResult getTaskResult(Command[] commands, int[] commandsResults) {
        TaskExecutionResult taskExecutionResult;
        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for (Command cmd : commands) {
            if (cmd.getState().equals(CommandState.FAILED)) {
                result = TaskExecutionResult.RESULT.FAILURE;
                break;
            }
        }
        taskExecutionResult = new TaskExecutionResult(result, commandsResults, commands);
        return taskExecutionResult;
    }

    private Integer start(Task task) {
        try {
            LOGGER.info("Starting DockerTaskExecutor " + this.getContainerName());
            this.workerDockerRequestHelper.start(task.getTaskSpec());
            return SUCCESS_EXIT_CODE;
        } catch (DockerStartException | DockerCreateContainerException | NotFoundDockerImage de) {
            LOGGER.error("Set task [" + task.getId() + "] to FAILED because a container error [" + de.getMessage() + "]");
            task.setState(TaskState.FAILED);
            return FAIL_EXIT_CODE;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Set task [" + task.getId() + "] to FAILED [" + e.getMessage() + "]");
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

    private Integer executeCommand(Command command, String taskId) throws Exception {
        LOGGER.info("Executing command of the [" + command.getCommand() + "] for the task [" + taskId + "]" +
                this.getContainerName() + "].");
        String execId = this.workerDockerRequestHelper.createExecInstance(command.getCommand());
        this.workerDockerRequestHelper.startExecInstance(execId);

        ExecInstanceResult execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
        while (execInstanceResult.getExitCode() == null) {
            execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
            final long poolingPeriodTime = 300;

            try {
                sleep(poolingPeriodTime);
            } catch (InterruptedException e) {
               LOGGER.error(e.getMessage(), e);
            }
        }
        LOGGER.info("Executed command [" + command.getCommand() + "] for the task [" + taskId + "] with exitcode=[" +
                execInstanceResult.getExitCode() + "] in worker [" + this.getContainerName() + "].");
        return execInstanceResult.getExitCode();
    }

//    @Override
//    public String toString() {
//        return "DockerTaskExecutor imageId={" + getImageId() + "} containerName={" + getContainerName() + "}";
//    }

    private String getContainerName() {
        return this.workerDockerRequestHelper.getContainerName();
    }

}
