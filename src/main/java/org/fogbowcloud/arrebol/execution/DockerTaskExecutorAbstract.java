package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;

import java.util.Arrays;
import java.util.List;

public abstract class DockerTaskExecutorAbstract implements TaskExecutor{
    private String imageId;
    private String containerName;

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutorAbstract.class);

    public DockerTaskExecutorAbstract(String imageId, String containerName) {
        this.imageId = imageId;
        this.containerName = containerName;
    }

    @Override
    public TaskExecutionResult execute(Task task){
        //FIXME: We should catch the errors when starting/finishing the container and move the task to its FAILURE state
        //FIXME: also, follow the SAME log format we used in the RawTaskExecutor
        TaskExecutionResult taskExecutionResult;
        Integer startStatus = this.start();

        if(startStatus != 0){
            LOGGER.error("Exit code from container start: " + startStatus);
            throw new DockerStartException("Could not start container " + getContainerName());
        }

        LOGGER.info("Successful started container " + getContainerName());
        Command[] commands = getCommands(task);
        LOGGER.info("Starting to execute commands of task " + task.getId());
        int[] commandsResults = executeCommands(commands);

        Integer stopStatus = this.stop();
        if(stopStatus != 0){
            LOGGER.error("Exit code from container stop: " + stopStatus);
        }

        taskExecutionResult = getTaskResult(commands, commandsResults);

        LOGGER.info("Result of task" + task.getId() + ": " + taskExecutionResult.getResult().toString());
        return taskExecutionResult;
    }

    protected abstract Integer start();

    protected abstract Integer stop();

    protected Command[] getCommands(Task task){
        TaskSpec taskSpec = task.getTaskSpec();
        List<Command> commandsList = taskSpec.getCommands();

        int commandsSize = commandsList.size();
        Command[] commands = commandsList.toArray(new Command[commandsSize]);
        return commands;
    }

    protected abstract Integer executeCommand(Command command);

    protected int[] executeCommands(Command[] commands){
        int[] commandsResults = new int[commands.length];
        Arrays.fill(commandsResults, TaskExecutionResult.UNDETERMINED_RESULT);
        for (int i = 0; i < commands.length; i++) {
            Command c = commands[i];
            c.setState(CommandState.RUNNING);
            try {
                Integer exitCode = executeCommand(c);
                commandsResults[i] = exitCode;
                c.setState(CommandState.FINISHED);
            } catch (Throwable t) {
                c.setState(CommandState.FAILED);
            }

        }
        return commandsResults;
    }

    protected TaskExecutionResult getTaskResult(Command[] commands, int[] commandsResults){
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

    public String getContainerName(){
        return this.containerName;
    }

    public String getImageId(){
        return this.imageId;
    }
}
