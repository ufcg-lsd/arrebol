package org.fogbowcloud.arrebol.execution.docker;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
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

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private final DockerExecutorHelper dockerExecutorHelper;

    public DockerTaskExecutor(String containerName, String address, String taskScriptContent) {
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName);
        this.dockerExecutorHelper = new DockerExecutorHelper(taskScriptContent, this.workerDockerRequestHelper,
                containerName);
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        // FIXME: We should catch the errors when starting/finishing the container and move the task
        // to its FAILURE state
        // FIXME: also, follow the SAME log format we used in the RawTaskExecutor
        TaskExecutionResult taskExecutionResult;

        Integer startStatus = this.startExecution(task);

        if (startStatus != SUCCESS_EXIT_CODE) {
            LOGGER.error("Exit code from container start: " + startStatus);
            throw new DockerStartException("Could not start container " + getContainerName());
        } else {
            LOGGER.debug("Container " + getContainerName() + " started successfully for task "
                    + task.getId());

            List<Command> commands = task.getTaskSpec().getCommands();
            try {
                this.dockerExecutorHelper.sendTaskScriptExecutor(task.getId());

                String taskScriptFilepath = "/tmp/" + task.getId() + ".ts";
                this.dockerExecutorHelper.sendTaskScript(commands, taskScriptFilepath, task.getId());
                this.dockerExecutorHelper.runScriptExecutor(task.getId(), taskScriptFilepath);

            } catch (Throwable e) {
                LOGGER.error(e);
                for (Command cmd : commands) {
                    cmd.setState(CommandState.FAILED);
                    cmd.setExitcode(TaskExecutionResult.UNDETERMINED_RESULT);
                }
            }

            try {
                setAllToRunning(commands);
                final String EC_FILEPATH = "/tmp/" + task.getId() + ".tc.ec";
                updateCommandsState(commands, EC_FILEPATH);
            } catch (Exception e) {
                LOGGER.error(e);
            }

            Integer stopStatus = this.stopExecution();
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

    private void updateCommandsState(List<Command> cmds, String ecFilepath) throws Exception {
        final long poolingPeriodTime = 2000;
        int nextRunningIndex = 0;
        while (nextRunningIndex < cmds.size()) {
            String ecContent = getEcFile(ecFilepath);
            int[] exitcodes = parseEcContentToArray(ecContent, cmds.size());
            nextRunningIndex = syncCommandsWithEC(cmds, exitcodes, nextRunningIndex);

            try {
                sleep(poolingPeriodTime);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
    }

    private Integer syncCommandsWithEC(List<Command> cmds, int exitcodes[], int nextRunningIndex) {
        while (nextRunningIndex < cmds.size()) {
            int exitCode = exitcodes[nextRunningIndex];
            if (exitCode != TaskExecutionResult.UNDETERMINED_RESULT) {
                Command cmd = cmds.get(nextRunningIndex);
                if (evaluateCommand(cmd, exitCode)) {
                    nextRunningIndex++;
                } else {
                    break;
                }
            }
        }
        return nextRunningIndex;
    }

    private boolean evaluateCommand(Command command, int exitcode) {
        boolean isRunning = true;
        if (exitcode != TaskExecutionResult.UNDETERMINED_RESULT) {
            if (exitcode == 0) {
                command.setState(CommandState.FINISHED);
            } else {
                command.setState(CommandState.FAILED);
            }
            command.setExitcode(exitcode);
            isRunning = false;
        }
        return !isRunning;
    }

    private int[] parseEcContentToArray(String ecContent, int cmdsSize) {
        String strExitcodes[] = ecContent.split("\r\n");
        int exitcodes[] = new int[cmdsSize];
        for (int i = 0; i < strExitcodes.length; i++) {
            exitcodes[i] = Integer.valueOf(strExitcodes[i]);
        }
        for (int i = strExitcodes.length; i < cmdsSize; i++) {
            exitcodes[i] = TaskExecutionResult.UNDETERMINED_RESULT;
        }
        return exitcodes;
    }

    private String getEcFile(String ecFilePath) throws Exception {
        String commandToGetFile = String.format("cat %s", ecFilePath);
        String execId = this.workerDockerRequestHelper.createAttachExecInstance(commandToGetFile);
        String response = this.workerDockerRequestHelper.startExecInstance(execId).trim();
        return response;
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

    private void setAllToRunning(List<Command> commands) {
        for (Command c : commands) {
            c.setState(CommandState.RUNNING);
        }
    }


    private Integer startExecution(Task task) {
        try {
            LOGGER.info("Starting DockerTaskExecutor " + this.getContainerName());
            this.workerDockerRequestHelper.start(task.getTaskSpec());
            return SUCCESS_EXIT_CODE;
        } catch (DockerStartException | DockerCreateContainerException de) {
            LOGGER.info("Set task [" + task.getId() + "] to FAILED because a container error ["
                    + de.getMessage() + "]");
            task.setState(TaskState.FAILED);
            return FAIL_EXIT_CODE;
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Set task [" + task.getId() + "] to FAILED [" + e.getMessage() + "]");
            task.setState(TaskState.FAILED);
            return FAIL_EXIT_CODE;
        }
    }

    private Integer stopExecution() {
        try {
            LOGGER.info("Stopping DockerTaskExecutor " + this.getContainerName());
            this.workerDockerRequestHelper.stopContainer();
            return SUCCESS_EXIT_CODE;
        } catch (DockerRemoveContainerException de) {
            LOGGER.error("Failed to stop container with name " + this.getContainerName()
                    + " with exit code " + FAIL_EXIT_CODE);

            return FAIL_EXIT_CODE;
        }
    }

    private String getContainerName() {
        return this.workerDockerRequestHelper.getContainerName();
    }

}
