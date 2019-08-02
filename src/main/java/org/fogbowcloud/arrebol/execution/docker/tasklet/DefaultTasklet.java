package org.fogbowcloud.arrebol.execution.docker.tasklet;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.docker.DockerCommandExecutor;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;

import java.util.List;

import static java.lang.Thread.sleep;

public class DefaultTasklet implements Tasklet {

    private String taskScriptExecutor;
    private TaskletHelper taskletHelper;
    private final Logger LOGGER = Logger.getLogger(DefaultTasklet.class);
    private static final long poolingPeriodTimeMs = 2000;


    public DefaultTasklet(String apiAddress, String containerId, String taskScriptExecutor) {
        this.taskScriptExecutor = taskScriptExecutor;
        this.taskletHelper = new TaskletHelper(apiAddress, containerId);
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        TaskExecutionResult taskExecutionResult;

        String taskId = task.getId();
        List<Command> commands = task.getTaskSpec().getCommands();

        try {
            setupContainerEnvironment(taskId, commands);
            runScript(taskId);
            trackTaskExecution(taskId, commands);
        } catch (Throwable t) {
            LOGGER.error("Set task [" + task.getId() + "] to FAILED [" + t.getMessage() + "]", t);
            failUnfinishedCommands(task.getTaskSpec().getCommands());
        } finally {
            taskExecutionResult = new TaskExecutionResult(getTaskResult(commands),
                    getExitCodes(commands));
            LOGGER.debug("Result of task [" + task.getId() + "]: "
                    + taskExecutionResult.getResult().toString());
        }
        return taskExecutionResult;
    }

    private void setupContainerEnvironment(String taskId, List<Command> commands) throws Exception {
        this.taskletHelper.sendTaskScriptExecutor(taskScriptExecutor);
        LOGGER.debug(
                "Starting to write commands [len=" + commands.size() + "] of task " + taskId
                        + " to .ts file.");
        this.taskletHelper.sendTaskScript(taskId, commands);
    }

    private void runScript(String taskId) throws Exception {
        this.taskletHelper.runTaskScriptExecutor(taskId);
    }

    private void trackTaskExecution(String taskId, List<Command> commands) throws Exception {
        int currentIndex = 0;
        while (currentIndex < commands.size()) {
            Command cmd = commands.get(currentIndex);
            cmd.setState(CommandState.RUNNING);
            currentIndex = updateCommandsState(commands, taskId, currentIndex);
            LOGGER.debug("After sync waiting for index [" + currentIndex + "]");
            try {
                sleep(poolingPeriodTimeMs);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
    }

    private int updateCommandsState(List<Command> cmds, String taskId, int startIndex)
            throws Exception {
        int[] exitcodes = taskletHelper.getExitCodes(taskId, cmds.size());
        int lastIndex = syncUntilTheLastCmdFinished(cmds, exitcodes, startIndex);
        return lastIndex;
    }

    private Integer syncUntilTheLastCmdFinished(List<Command> cmds, int[] exitcodes, int startIndex) {
        int currentIndex = startIndex;
        while (currentIndex < cmds.size()) {
            int exitCode = exitcodes[currentIndex];
            Command cmd = cmds.get(currentIndex);
            boolean hasFinished = hasFinished(exitCode);
            if(hasFinished){
                cmd.setState(CommandState.FINISHED);
                cmd.setExitcode(exitCode);
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

    private boolean hasFinished(int exitCode) {
        boolean isUndetermined = false;
        if (exitCode == TaskExecutionResult.UNDETERMINED_RESULT) {
            isUndetermined = true;
        }
        return !isUndetermined;
    }

    private void failUnfinishedCommands(List<Command> commands) {
        for (Command c : commands) {
            if (!c.getState().equals(CommandState.FINISHED)) {
                c.setState(CommandState.FAILED);
                c.setExitcode(TaskExecutionResult.UNDETERMINED_RESULT);
            }
        }
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

    protected void setTaskletHelper(TaskletHelper taskletHelper) {
        this.taskletHelper = taskletHelper;
    }
}
