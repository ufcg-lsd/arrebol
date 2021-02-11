/* (C)2020 */
package org.fogbowcloud.arrebol.execution.docker.tasklet;

import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.*;
import static org.junit.Assert.*;

import java.io.IOException;
import org.fogbowcloud.arrebol.execution.docker.DockerCommandExecutor;
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerFileHandlerHelper;
import org.fogbowcloud.arrebol.models.task.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

public class TaskletHelperTest {

  private Task task;
  private DockerCommandExecutor dockerCommandExecutor;
  private DockerFileHandlerHelper dockerFileHandlerHelper;
  private static String badFormattedSameLineContent;
  private static String emptyContent;
  private static String badFormattedContent;
  private static String wellFormattedContent;
  private static String taskScriptExecutorContent;

  @BeforeClass
  public static void setup() throws IOException {
    final String TASK_SCRIPT_EXAMPLES_DIRECTORY = "task-script-examples/";
    final String EXIT_CODES_SAME_LINE_FILE_PATH =
        TASK_SCRIPT_EXAMPLES_DIRECTORY + "ec-in-same-line.ts.ec";
    final String EMPTY_EXAMPLE_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "empty-example.ts.ec";
    final String GAP_IN_EXIT_CODES_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "gap-in-ecs.ts.ec";
    final String WELL_FORMED_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "well-formed.ts.ec";

    badFormattedSameLineContent =
        getFileContent(new ClassPathResource(EXIT_CODES_SAME_LINE_FILE_PATH));
    emptyContent = getFileContent(new ClassPathResource(EMPTY_EXAMPLE_FILE_PATH));
    badFormattedContent = getFileContent(new ClassPathResource(GAP_IN_EXIT_CODES_FILE_PATH));
    wellFormattedContent = getFileContent(new ClassPathResource(WELL_FORMED_FILE_PATH));
    taskScriptExecutorContent = loadTaskScriptExecutor();
  }

  @Before
  public void beforeEachTestMethod() {
    task = mockTask();
    dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);
    dockerFileHandlerHelper = Mockito.mock(DockerFileHandlerHelper.class);
  }

  @Test
  public void testNotEmptyContent() {
    Assert.assertFalse(wellFormattedContent.isEmpty());
    Assert.assertFalse(badFormattedContent.isEmpty());
    Assert.assertFalse(badFormattedSameLineContent.isEmpty());
  }

  @Test
  public void testEmptyContent() {
    Assert.assertTrue(emptyContent.isEmpty());
  }

  @Test
  public void testSendTaskScriptExecutor() throws Exception {
    String taskScriptExecutorFilePath = "/tmp/" + TASK_SCRIPT_EXECUTOR_NAME;
    Mockito.when(
            dockerFileHandlerHelper.writeToFile(
                Mockito.eq(MOCK_CONTAINER_NAME),
                Mockito.eq(taskScriptExecutorContent),
                Mockito.eq(taskScriptExecutorFilePath)))
        .thenReturn(MOCK_SUCCESS_STATUS_CODE);

    TaskletHelper taskletHelper =
        new TaskletHelper(
            MOCK_ADDRESS, MOCK_CONTAINER_NAME, dockerCommandExecutor, dockerFileHandlerHelper);
    taskletHelper.sendTaskScriptExecutor(taskScriptExecutorContent);
  }

  @Test(expected = Exception.class)
  public void testFailCommandSendTaskScriptExecutor() throws Exception {
    String taskScriptExecutorFilePath = "/tmp/" + TASK_SCRIPT_EXECUTOR_NAME;
    Mockito.when(
            dockerFileHandlerHelper.writeToFile(
                Mockito.eq(MOCK_CONTAINER_NAME),
                Mockito.eq(taskScriptExecutorContent),
                Mockito.eq(taskScriptExecutorFilePath)))
        .thenReturn(MOCK_FAIL_STATUS_CODE);

    TaskletHelper taskletHelper =
        new TaskletHelper(
            MOCK_ADDRESS, MOCK_CONTAINER_NAME, dockerCommandExecutor, dockerFileHandlerHelper);
    taskletHelper.sendTaskScriptExecutor(loadTaskScriptExecutor());
  }

  @Test
  public void testSendTaskScript() throws Exception {
    String expectedWriteCommand = "echo '" + MOCK_COMMAND + "' >> " + MOCK_TS_FILE_PATH;
    Mockito.when(
            dockerCommandExecutor.executeCommand(
                Mockito.eq(MOCK_ADDRESS),
                Mockito.eq(MOCK_CONTAINER_NAME),
                Mockito.eq(expectedWriteCommand)))
        .thenReturn(MOCK_SUCCESS_EXEC_INSTANCE_RESULT);

    TaskletHelper taskletHelper =
        new TaskletHelper(
            MOCK_ADDRESS, MOCK_CONTAINER_NAME, dockerCommandExecutor, dockerFileHandlerHelper);
    taskletHelper.sendTaskScript(task.getId(), task.getTaskSpec().getCommands());
  }

  @Test(expected = Exception.class)
  public void testFailSendTaskScript() throws Exception {
    Mockito.when(
            dockerFileHandlerHelper.writeToFile(
                Mockito.eq(MOCK_CONTAINER_NAME),
                Mockito.eq(MOCK_TASK_SCRIPT_CONTENT),
                Mockito.eq(MOCK_TS_FILE_PATH)))
        .thenReturn(MOCK_FAIL_STATUS_CODE);

    TaskletHelper taskletHelper =
        new TaskletHelper(
            MOCK_ADDRESS, MOCK_CONTAINER_NAME, dockerCommandExecutor, dockerFileHandlerHelper);
    taskletHelper.sendTaskScript(task.getId(), task.getTaskSpec().getCommands());
  }

  @Test
  public void testGetExitCodes() throws Exception {
    Mockito.when(
            dockerFileHandlerHelper.readFile(
                Mockito.eq(MOCK_CONTAINER_NAME), Mockito.eq(MOCK_EC_FILE_PATH)))
        .thenReturn(MOCK_EC_FILE_CONTENT);

    TaskletHelper taskletHelper =
        new TaskletHelper(
            MOCK_ADDRESS, MOCK_CONTAINER_NAME, dockerCommandExecutor, dockerFileHandlerHelper);
    assertArrayEquals(
        MOCK_EC_ARRAY,
        taskletHelper.getExitCodes(task.getId(), task.getTaskSpec().getCommands().size()));
  }
}
