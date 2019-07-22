package org.fogbowcloud.arrebol.execution.docker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult.RESULT;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.NotFoundDockerImageException;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class DockerTaskExecutorTest {

    private Task task;

    @Before
    public void beforeEachTestMethod() {
        task = mockTask();
    }

    @Test
    public void testNotFoundImage() throws Exception {
        Mockito.when(task.getTaskSpec().getImage()).thenReturn("anyImage");
        DockerTaskExecutor dockerTaskExecutor = mockDockerTaskExecutor(
            new NotFoundDockerImageException("Error to pull docker image"));
        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());
    }

    @Test
    public void testErrorCreatingContainer() throws UnsupportedEncodingException {
        Task task = mockTask();
        DockerTaskExecutor dockerTaskExecutor = mockDockerTaskExecutor(
            new DockerCreateContainerException("Could not create container"));
        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());

    }

    @Test
    public void testErrorStartingContainer() throws UnsupportedEncodingException {
        Task task = mockTask();
        DockerTaskExecutor dockerTaskExecutor = mockDockerTaskExecutor(
            new DockerStartException("Could not start container"));
        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());

    }

    @Test
    public void testSuccessfulExecution() throws Exception {

        WorkerDockerRequestHelper workerDockerRequestHelper = Mockito
            .mock(WorkerDockerRequestHelper.class);
        Mockito.when(workerDockerRequestHelper.getContainerName()).thenReturn("MockedContainer");
        Mockito.when(workerDockerRequestHelper.start(any(TaskSpec.class)))
            .thenReturn("MockedContainerId");
        Mockito.doNothing().when(workerDockerRequestHelper).stopContainer();

        DockerExecutorHelper dockerExecutorHelper = Mockito
            .spy(new DockerExecutorHelper("mockTaskScript", workerDockerRequestHelper));
        String ecContent = "0\r\n0\r\n0\r\n0\r\n0";
        Mockito.doReturn(ecContent).when(dockerExecutorHelper)
            .getEcFile("/tmp/" + task.getId() + ".ts.ec");

        DockerTaskExecutor dockerTaskExecutor = Mockito.spy(new DockerTaskExecutor("mockAddress",
            "mockContainerName", "mockScript", "mockImageId"));
        Mockito.doNothing().when(dockerTaskExecutor).setupAndRun(any(Task.class));
        dockerTaskExecutor.setWorkerDockerRequestHelper(workerDockerRequestHelper);
        dockerTaskExecutor.setDockerExecutorHelper(dockerExecutorHelper);

        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FINISHED));
        assertEquals(RESULT.SUCCESS, taskExecutionResult.getResult());

    }

    private DockerTaskExecutor mockDockerTaskExecutor(RuntimeException e)
        throws UnsupportedEncodingException {
        WorkerDockerRequestHelper workerDockerRequestHelper = Mockito
            .mock(WorkerDockerRequestHelper.class);
        Mockito.when(workerDockerRequestHelper.start(any(TaskSpec.class)))
            .thenThrow(e);

        DockerTaskExecutor dockerTaskExecutor = new DockerTaskExecutor("mockAddress",
            "mockContainerName", "mockScript", "mockImageId");
        dockerTaskExecutor.setWorkerDockerRequestHelper(workerDockerRequestHelper);
        return dockerTaskExecutor;
    }

    private Task mockTask() {
        Command command = new Command("echo Hello World");
        List<Command> commands = Collections.nCopies(5, command);

        TaskSpec taskSpec = Mockito.mock(TaskSpec.class);
        Mockito.when(taskSpec.getImage()).thenReturn(null);
        Mockito.when(taskSpec.getCommands()).thenReturn(commands);

        Task task = new Task("mockTaskId", taskSpec);
        return task;
    }

    private boolean isAll(List<Command> commands, CommandState commandState) {
        boolean isAll = true;
        for (Command c : commands) {
            if (!c.getState().equals(commandState)) {
                isAll = false;
                break;
            }
        }
        return isAll;
    }
}