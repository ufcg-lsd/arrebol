package org.fogbowcloud.arrebol.execution.docker;

import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.loadTaskScriptExecutor;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockAddress;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockContainerName;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockEcFileContent;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockEcFilePath;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockImageId;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockTask;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockTsFilePath;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockWorkerDockerRequestHelper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;

import java.util.List;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult.RESULT;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerImageNotFoundException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
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
        DockerTaskExecutor dockerTaskExecutor =
                mockDockerTaskExecutorWithStartException(
                        new DockerImageNotFoundException("Error to pull docker image"));

        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());
    }

    @Test
    public void testErrorCreatingContainer() throws Exception {
        DockerTaskExecutor dockerTaskExecutor =
                mockDockerTaskExecutorWithStartException(
                        new DockerCreateContainerException("Could not create container"));
        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());
    }

    @Test
    public void testErrorStartingContainer() throws Exception {
        DockerTaskExecutor dockerTaskExecutor =
                mockDockerTaskExecutorWithStartException(
                        new DockerStartException("Could not start container"));
        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());
    }

    @Test
    public void testSuccessfulExecution() throws Exception {

        WorkerDockerRequestHelper workerDockerRequestHelper = mockWorkerDockerRequestHelper();
        DockerExecutorHelper dockerExecutorHelper = mockDockerExecutorHelper();

        DockerTaskExecutor dockerTaskExecutor =
                new DockerTaskExecutor(workerDockerRequestHelper, dockerExecutorHelper);

        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FINISHED));
        assertEquals(RESULT.SUCCESS, taskExecutionResult.getResult());
    }

    private DockerTaskExecutor mockDockerTaskExecutorWithStartException(Exception e)
            throws Exception {
        DockerExecutorHelper dockerExecutorHelper = mockDockerExecutorHelper();
        WorkerDockerRequestHelper workerDockerRequestHelper = mockWorkerDockerRequestHelper();
        Mockito.when(workerDockerRequestHelper.start(Mockito.any(TaskSpec.class))).thenThrow(e);

        DockerTaskExecutor dockerTaskExecutor =
            new DockerTaskExecutor(workerDockerRequestHelper, dockerExecutorHelper);

        return dockerTaskExecutor;
    }

    private DockerExecutorHelper mockDockerExecutorHelper() throws Exception {
        DockerExecutorHelper dockerExecutorHelper = Mockito.mock(DockerExecutorHelper.class);
        Mockito.doNothing().when(dockerExecutorHelper).sendTaskExecutorScript();
        Mockito.doNothing()
                .when(dockerExecutorHelper)
                .writeTaskScript(anyListOf(Command.class), Mockito.eq(mockTsFilePath));
        Mockito.doNothing()
                .when(dockerExecutorHelper)
                .runExecutorScript(Mockito.eq(mockTsFilePath));
        Mockito.when(dockerExecutorHelper.getEcFile(Mockito.eq(mockEcFilePath)))
                .thenReturn(mockEcFileContent);
        Mockito.doCallRealMethod()
                .when(dockerExecutorHelper)
                .parseEcContentToArray(Mockito.anyString(), Mockito.anyInt());
        return dockerExecutorHelper;
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
