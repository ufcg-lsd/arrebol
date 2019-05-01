package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.execution.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.models.task.TaskSpec;

import java.util.List;

public class DockerTaskExecutor implements TaskExecutor {

    private String imageId;
    private String containerName;

    private final String BASH = "/bin/bash";
    private final String DOCKER_EXEC = "docker exec";
    private final String DOCKER_RUN = "docker run --rm -idt --name";
    private final String DOCKER_STOP = "docker stop";

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    public DockerTaskExecutor(String imageId, String containerName) {
        this.imageId = imageId;
        this.containerName = containerName;
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        TaskExecutionResult taskExecutionResult;
        Integer startStatus = this.start();

        if(startStatus != 0){
            LOGGER.error("Exit code from container start: " + startStatus);
            throw new DockerStartException("Could not start container " + this.containerName);
        }

        LOGGER.info("Successful started container " + this.containerName);

        TaskSpec taskSpec = task.getTaskSpec();
        List<Command> commandsList = taskSpec.getCommands();

        int commandsSize = commandsList.size();
        Command[] commands = commandsList.toArray(new Command[commandsSize]);
        int[] commandsResults = new int[commandsSize];

        LOGGER.info("Starting to execute commands of task " + task.getId());
        for (int i = 0; i < commandsSize; i++) {
            Command c = commands[i];
            Integer exitCode = executeCommand(c);
            commandsResults[i] = exitCode;
        }

        Integer stopStatus = this.stop();
        if(stopStatus != 0){
            LOGGER.error("Exit code from container stop: " + stopStatus);
        }

        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for (int i : commandsResults) {
            if (i != 0) {
                result = TaskExecutionResult.RESULT.FAILURE;
                break;
            }
        }

        LOGGER.info("Result of task" + task.getId() + ": " + result.toString());

        taskExecutionResult = new TaskExecutionResult(result, commandsResults, commands);
        return taskExecutionResult;
    }

    private Integer start() {
        try {
            String[] cmd = {
                    BASH,
                    "-c",
                    DOCKER_RUN + " " + this.containerName + " " + this.imageId
            };
            Process p = Runtime.getRuntime().exec(cmd);

            Integer exitCode = p.waitFor();
            return exitCode;
        } catch (Exception e){
            throw new RuntimeException("Error while trying execute commands to start container " + this.containerName);
        }
    }

    private Integer stop() {
        try {
            String[] cmd = {
                    BASH,
                    "-c",
                    DOCKER_STOP + " " + this.containerName
            };
            Process p = Runtime.getRuntime().exec(cmd);

            Integer exitCode = p.waitFor();
            return exitCode;

        } catch (Exception e){
            throw new RuntimeException("Error while trying execute commands to stop container " + this.containerName);
        }
    }

    private Integer executeCommand(Command command) {
        try {
            String commandStr = command.getCommand();
            String[] cmd = {
                    BASH,
                    "-c",
                    DOCKER_EXEC + " " + this.containerName + " " + commandStr
            };

            command.setState(CommandState.RUNNING);

            Process p = Runtime.getRuntime().exec(cmd);

            Integer exitCode = p.waitFor();

            command.setState(CommandState.FINISHED);

            return exitCode;

        } catch(Exception e){
            throw new RuntimeException("Error while truing execute commands to container " + this.containerName);
        }
    }

    public String getImageId(){
        return this.imageId;
    }

    public String getContainerName() {
        return this.containerName;
    }

    @Override
    public String toString() {
        return "DockerTaskExecutor imageId={" + getImageId() + "} containerName={" + containerName + "}";
    }
}
