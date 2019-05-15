package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.execution.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.models.task.TaskSpec;

import java.util.Arrays;
import java.util.List;

public class DockerTaskExecutor extends DockerTaskExecutorAbstract {

    private final String BASH = "/bin/bash";
    private final String DOCKER_EXEC = "docker exec";
    private final String DOCKER_RUN = "docker run --rm -idt --name";
    private final String DOCKER_STOP = "docker stop";

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    public DockerTaskExecutor(String imageId, String containerName) {
        super(imageId, containerName);
    }

    protected Integer start() {
        try {
            String[] cmd = {
                    BASH,
                    "-c",
                    DOCKER_RUN + " " + getContainerName() + " " + getImageId()
            };
            Process p = Runtime.getRuntime().exec(cmd);

            Integer exitCode = p.waitFor();
            return exitCode;
        } catch (Exception e){
            throw new RuntimeException("Error while trying execute commands to start container " + getContainerName());
        }
    }

    protected Integer stop() {
        try {
            String[] cmd = {
                    BASH,
                    "-c",
                    DOCKER_STOP + " " + getContainerName()
            };
            Process p = Runtime.getRuntime().exec(cmd);

            Integer exitCode = p.waitFor();
            return exitCode;

        } catch (Exception e){
            throw new RuntimeException("Error while trying execute commands to stop container " + getContainerName());
        }
    }

    protected Integer executeCommand(Command command) {
        try {
            String commandStr = command.getCommand();
            String[] cmd = {
                    BASH,
                    "-c",
                    DOCKER_EXEC + " " + getContainerName() + " " + commandStr
            };
            ProcessBuilder builder = new ProcessBuilder(cmd);
            Process process = builder.start();
            Integer exitCode = process.waitFor();
            return exitCode;

        } catch(Exception e){
            throw new RuntimeException("Error while truing execute commands to container " + getContainerName());
        }
    }

    @Override
    public String toString() {
        return "DockerTaskExecutor imageId={" + getImageId() + "} containerName={" + getContainerName() + "}";
    }
}
