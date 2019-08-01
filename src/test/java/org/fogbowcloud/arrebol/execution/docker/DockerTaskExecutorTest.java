package org.fogbowcloud.arrebol.execution.docker;

import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.isAll;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.loadTaskScriptExecutor;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockDockerContainerResource;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockSuccessTaskExecutionResult;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockTask;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult.RESULT;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerImageNotFoundException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.docker.resource.ContainerSpecification;
import org.fogbowcloud.arrebol.execution.docker.resource.DockerContainerResource;
import org.fogbowcloud.arrebol.execution.docker.tasklet.DefaultTasklet;
import org.fogbowcloud.arrebol.execution.docker.tasklet.Tasklet;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
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
        DockerContainerResource dockerContainerResource = mockDockerContainerResource();
        Mockito.doThrow(new DockerImageNotFoundException("Error to pull docker image"))
                .when(dockerContainerResource)
                .start(Mockito.any(ContainerSpecification.class));

        DockerTaskExecutor dockerTaskExecutor =
                new DockerTaskExecutor(dockerContainerResource, loadTaskScriptExecutor());

        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());
    }

    @Test
    public void testErrorCreatingContainer() throws Exception {
        DockerContainerResource dockerContainerResource = mockDockerContainerResource();
        Mockito.doThrow(new DockerCreateContainerException("Could not create container"))
                .when(dockerContainerResource)
                .start(Mockito.any(ContainerSpecification.class));

        DockerTaskExecutor dockerTaskExecutor =
                new DockerTaskExecutor(dockerContainerResource, loadTaskScriptExecutor());
        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());
    }

    @Test
    public void testErrorStartingContainer() throws Exception {
        DockerContainerResource dockerContainerResource = mockDockerContainerResource();
        Mockito.doThrow(new DockerStartException("Could not start container"))
                .when(dockerContainerResource)
                .start(Mockito.any(ContainerSpecification.class));

        DockerTaskExecutor dockerTaskExecutor =
                new DockerTaskExecutor(dockerContainerResource, loadTaskScriptExecutor());
        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FAILED));
        assertEquals(TaskExecutionResult.RESULT.FAILURE, taskExecutionResult.getResult());
    }

    @Test
    public void testSuccessfulExecution() throws Exception {
        DockerContainerResource dockerContainerResource = mockDockerContainerResource();
        Tasklet tasklet = Mockito.mock(DefaultTasklet.class);
        Mockito.when(tasklet.execute(Mockito.any(Task.class)))
                .thenReturn(mockSuccessTaskExecutionResult);

        DockerTaskExecutor dockerTaskExecutor =
                new DockerTaskExecutor(dockerContainerResource, loadTaskScriptExecutor());
        dockerTaskExecutor.setTasklet(tasklet);

        TaskExecutionResult taskExecutionResult = dockerTaskExecutor.execute(task);
        assertEquals(RESULT.SUCCESS, taskExecutionResult.getResult());
        assertEquals(mockSuccessTaskExecutionResult, taskExecutionResult);
    }
    //
    //    private DockerTaskExecutor mockDockerTaskExecutorWithStartException(Exception e)
    //            throws Exception {
    //
    //        DockerExecutorHelper dockerExecutorHelper = mockDockerExecutorHelper();
    //        WorkerDockerRequestHelper workerDockerRequestHelper = mockWorkerDockerRequestHelper();
    //
    // Mockito.when(workerDockerRequestHelper.start(Mockito.any(TaskSpec.class))).thenThrow(e);
    //
    //        DockerTaskExecutor dockerTaskExecutor =
    //            new DockerTaskExecutor(workerDockerRequestHelper, dockerExecutorHelper);
    //
    //        return dockerTaskExecutor;
    //    }
    //
    //    private DockerExecutorHelper mockDockerExecutorHelper() throws Exception {
    //        DockerExecutorHelper dockerExecutorHelper = Mockito.mock(DockerExecutorHelper.class);
    //        Mockito.doNothing().when(dockerExecutorHelper).sendTaskExecutorScript();
    //        Mockito.doNothing()
    //                .when(dockerExecutorHelper)
    //                .writeTaskScript(anyListOf(Command.class), Mockito.eq(mockTsFilePath));
    //        Mockito.doNothing()
    //                .when(dockerExecutorHelper)
    //                .runExecutorScript(Mockito.eq(mockTsFilePath));
    //        Mockito.when(dockerExecutorHelper.getEcFile(Mockito.eq(mockEcFilePath)))
    //                .thenReturn(mockEcFileContent);
    //        Mockito.doCallRealMethod()
    //                .when(dockerExecutorHelper)
    //                .parseEcContentToArray(Mockito.anyString(), Mockito.anyInt());
    //        return dockerExecutorHelper;
    //    }
}
