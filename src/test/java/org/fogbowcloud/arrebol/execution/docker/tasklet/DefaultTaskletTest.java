package org.fogbowcloud.arrebol.execution.docker.tasklet;

import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.isAll;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.loadTaskScriptExecutor;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.MOCK_ADDRESS;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.MOCK_CONTAINER_NAME;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.MOCK_EC_ARRAY;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockTask;
import static org.junit.Assert.*;

import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult.RESULT;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultTaskletTest {
    private Task task;

    @Before
    public void beforeEachTestMethod() {
        task = mockTask();
    }

    @Test
    public void testSuccessfulExecution() throws Exception {
        TaskletHelper taskletHelper = Mockito.mock(TaskletHelper.class);
        Mockito.when(taskletHelper.getExitCodes(task.getId(), task.getTaskSpec().getCommands().size())).thenReturn(
            MOCK_EC_ARRAY);

        DefaultTasklet tasklet = new DefaultTasklet(MOCK_ADDRESS, MOCK_CONTAINER_NAME, loadTaskScriptExecutor());
        tasklet.setTaskletHelper(taskletHelper);

        TaskExecutionResult taskExecutionResult = tasklet.execute(task);

        assertTrue(isAll(task.getTaskSpec().getCommands(), CommandState.FINISHED));
        assertEquals(RESULT.SUCCESS, taskExecutionResult.getResult());
    }

}