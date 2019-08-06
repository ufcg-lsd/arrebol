package org.fogbowcloud.arrebol.execution.docker;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult.RESULT;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.resource.ContainerSpecification;
import org.fogbowcloud.arrebol.execution.docker.resource.DockerContainerResource;
import org.fogbowcloud.arrebol.execution.docker.tasklet.DefaultTasklet;
import org.fogbowcloud.arrebol.execution.docker.tasklet.Tasklet;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.models.task.Task;

/**
 * This implementation of {@link TaskExecutor} manages the execution of a {@link Task} in a,
 * possible remote, DockerContainer. A new container is created to execute every {@link Task} and
 * destroyed after the execution has finished. A task representation is sent to the container, which
 * drives the execution of the commands itself. This objects monitors the execution until it ends on
 * success or failure. If any container initialization error occurs, the return {@link
 * TaskExecutionResult} indicates the Failure.
 */
public class DockerTaskExecutor implements TaskExecutor {

    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);
    private DockerContainerResource dockerContainerResource;
    private Tasklet tasklet;

    public DockerTaskExecutor(
            DockerContainerResource dockerContainerResource, String taskScriptExecutor) {
        this.dockerContainerResource = dockerContainerResource;
        this.tasklet =
                new DefaultTasklet(
                        dockerContainerResource.getApiAddress(),
                        dockerContainerResource.getId(),
                        taskScriptExecutor);
    }

    /** {@inheritDoc} */
    @Override
    public TaskExecutionResult execute(Task task) {
        TaskExecutionResult taskExecutionResult;
        try {
            ContainerSpecification containerSpecification = createContainerSpecification(task);
            LOGGER.info("Starting the Docker Task Executor [" + this.dockerContainerResource.getId() + "] to execute task [" + task.getId() + "]");
            this.dockerContainerResource.start(containerSpecification);
        } catch (Throwable e) {
            LOGGER.error("Error while start resource: [" + e.getMessage() + "]", e);
            failTask(task);
            taskExecutionResult = getFailResultInstance(task.getTaskSpec().getCommands().size());
            return taskExecutionResult;
        }
        LOGGER.debug("Starting to execute task " + task.getId() + " in resource[" + this.dockerContainerResource.getId() + "]");
        taskExecutionResult = this.tasklet.execute(task);
        try {
            LOGGER.info("Stopping DockerTaskExecutor [" + this.dockerContainerResource.getId() + "]");
            this.dockerContainerResource.stop();
        } catch (Throwable e) {
            LOGGER.error("Error while stop Docker Task Executor [" + this.dockerContainerResource.getId() + "]: [" + e.getMessage() + "]", e);
        }

        return taskExecutionResult;
    }

    private ContainerSpecification createContainerSpecification(Task task) {
        Specification specification = task.getTaskSpec().getSpec();
        ContainerSpecification containerSpecification = new ContainerSpecification();
        if( Objects.nonNull(specification)){
            String imageId = task.getTaskSpec().getSpec().getImage();
            Map<String, String> requirements = task.getTaskSpec().getSpec().getRequirements();

            containerSpecification =
                new ContainerSpecification(imageId, requirements);
        }
        return containerSpecification;
    }

    private void failTask(Task task) {
        for (Command c : task.getTaskSpec().getCommands()) {
            c.setState(CommandState.FAILED);
            c.setExitcode(TaskExecutionResult.UNDETERMINED_RESULT);
        }
    }

    private TaskExecutionResult getFailResultInstance(int size) {
        int[] exitCodes = new int[size];
        Arrays.fill(exitCodes, TaskExecutionResult.UNDETERMINED_RESULT);
        TaskExecutionResult taskExecutionResult =
                new TaskExecutionResult(RESULT.FAILURE, exitCodes);
        return taskExecutionResult;
    }

    protected void setTasklet(Tasklet tasklet) {
        this.tasklet = tasklet;
    }
}
