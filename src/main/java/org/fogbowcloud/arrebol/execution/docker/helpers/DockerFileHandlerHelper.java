package org.fogbowcloud.arrebol.execution.docker.helpers;

import org.fogbowcloud.arrebol.execution.docker.DockerCommandExecutor;

public class DockerFileHandlerHelper {

    private static final String COMMAND_WRITE_TO_FILE_PATTERN = "echo '%s' >> %s";
    private static final String COMMAND_READ_FILE_PATTERN = "cat %s";
    private String apiAddress;
    private DockerCommandExecutor dockerCommandExecutor;

    public DockerFileHandlerHelper(String apiAddress, DockerCommandExecutor dockerCommandExecutor) {
        this.dockerCommandExecutor = dockerCommandExecutor;
        this.apiAddress = apiAddress;
    }

    public Integer writeToFile(String containerId, String content, String filePath) {
        String command = String.format(COMMAND_WRITE_TO_FILE_PATTERN, content, filePath);
        try {
            Integer exitCode =
                    this.dockerCommandExecutor
                            .executeCommand(apiAddress, containerId, command)
                            .getExitCode();
            return exitCode;
        } catch (Exception e) {
            throw new RuntimeException("Error while write to file [" + filePath + "] in container [" + containerId + "]: " + e.getMessage());
        }
    }

    public String readFile(String containerId, String filePath) {
        String command = String.format(COMMAND_READ_FILE_PATTERN, filePath);
        try {
            String fileContent =
                    this.dockerCommandExecutor.executeCommandWithStout(
                            apiAddress, containerId, command);
            return fileContent;
        } catch (Exception e) {
            throw new RuntimeException("Error while read file [" + filePath + "] from container [" + containerId + "]");
        }
    }
}
