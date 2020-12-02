/* (C)2020 */
package org.fogbowcloud.arrebol.execution.docker;

import static java.lang.Thread.sleep;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.HttpWrapper;
import org.fogbowcloud.arrebol.utils.AppUtil;
import org.json.JSONObject;

public class DockerCommandExecutor {

  private final Logger LOGGER = Logger.getLogger(DockerCommandExecutor.class);
  private static final long poolingPeriodTimeMs = 300;

  /**
   * It creates the command execution instance, sends it to the docker and each period of time
   * {@link DockerCommandExecutor#poolingPeriodTimeMs} checks if exit code already exists. If exists
   * then returns an {@link ExecInstanceResult}.
   */
  public ExecInstanceResult executeCommand(String apiAddress, String containerId, String command)
      throws Exception {
    LOGGER.info("Executing command [" + command + "] to the [" + containerId + "].");
    String execId = this.createExecInstance(apiAddress, containerId, command, false, false);
    this.startExecInstance(apiAddress, execId);

    ExecInstanceResult execInstanceResult = this.inspectExecInstance(apiAddress, execId);
    while (Objects.isNull(execInstanceResult.getExitCode())) {
      execInstanceResult = this.inspectExecInstance(apiAddress, execId);
      try {
        sleep(poolingPeriodTimeMs);
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    LOGGER.info(
        "Executed command ["
            + command
            + "] + with exitcode=["
            + execInstanceResult.getExitCode()
            + "] in worker ["
            + containerId
            + "].");
    return execInstanceResult;
  }

  /**
   * It creates the command execution instance and sends it to the docker, without waiting for the
   * end of execution and nor for its exit code.
   */
  public void executeAsyncCommand(String address, String containerId, String command)
      throws Exception {
    LOGGER.info("Sending command [" + command + "] to the container [" + containerId + "].");

    String execId = this.createExecInstance(address, containerId, command, false, false);
    this.startExecInstance(address, execId);
  }

  public String executeCommandWithStout(String address, String containerId, String command)
      throws Exception {
    String execId = this.createExecInstance(address, containerId, command, true, true);
    String response = this.startExecInstance(address, execId).trim();
    ExecInstanceResult result = this.inspectExecInstance(address, execId);
    if (result.getExitCode() != 0) {
      throw new RuntimeException(
          "No zero exitcode ["
              + result.getExitCode()
              + "] to execute command ["
              + command
              + "]: "
              + response);
    }
    return response;
  }

  private ExecInstanceResult inspectExecInstance(String address, String execId) throws Exception {
    final String endpoint = String.format("%s/exec/%s/json", address, execId);
    String response = HttpWrapper.doRequest(HttpGet.METHOD_NAME, endpoint);
    return instanceExecResult(response);
  }

  private String createExecInstance(
      String address,
      String containerId,
      String command,
      boolean attachStdout,
      boolean attachStderr)
      throws Exception {
    final String endpoint = String.format("%s/containers/%s/exec", address, containerId);
    StringEntity body = jsonCreateExecInstance(command, attachStdout, attachStderr);
    LOGGER.debug("body of the request to create an exec=[" + EntityUtils.toString(body) + "]");
    String response = HttpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
    return AppUtil.getValueFromJsonStr("Id", response);
  }

  private String startExecInstance(String address, String execId) throws Exception {
    final String endpoint = String.format("%s/exec/%s/start", address, execId);
    StringEntity body = jsonStartExecInstance();
    String response = HttpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
    return response;
  }

  private StringEntity jsonCreateExecInstance(
      String command, boolean attachStdout, boolean attachStderr)
      throws UnsupportedEncodingException {
    JSONObject jsonObject = new JSONObject();
    AppUtil.makeBodyField(jsonObject, "Tty", true);
    AppUtil.makeBodyField(jsonObject, "AttachStdout", attachStdout);
    AppUtil.makeBodyField(jsonObject, "AttachStdout", attachStderr);

    List<String> commandBash = Arrays.asList("/bin/bash", "-c", command);
    AppUtil.makeBodyField(jsonObject, "Cmd", commandBash);

    return new StringEntity(jsonObject.toString());
  }

  private StringEntity jsonStartExecInstance() throws UnsupportedEncodingException {
    JSONObject jsonObject = new JSONObject();
    AppUtil.makeBodyField(jsonObject, "Detach", false);
    AppUtil.makeBodyField(jsonObject, "Tty", true);
    return new StringEntity(jsonObject.toString());
  }

  private ExecInstanceResult instanceExecResult(String response) {
    JSONObject jsonObject = new JSONObject(response);
    String execId = jsonObject.getString("ID");
    Integer exitCode = null;
    if (!jsonObject.isNull("ExitCode")) {
      exitCode = jsonObject.getInt("ExitCode");
    }
    boolean running = jsonObject.getBoolean("Running");
    return new ExecInstanceResult(execId, exitCode, running);
  }
}
