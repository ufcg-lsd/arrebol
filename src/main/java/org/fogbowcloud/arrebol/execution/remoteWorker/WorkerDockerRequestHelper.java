package org.fogbowcloud.arrebol.execution.remoteWorker;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.utils.AppUtil;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class WorkerDockerRequestHelper {

    private HttpWrapper httpWrapper;
    private String address;
    private String containerName;
    private ContainerRequestHelper containerRequestHelper;

    private final Logger LOGGER = Logger.getLogger(WorkerDockerRequestHelper.class);

    public WorkerDockerRequestHelper(String address, String containerName, String image) {
        this.httpWrapper = new HttpWrapper();
        this.address = address;
        this.containerName = containerName;
        this.containerRequestHelper = new ContainerRequestHelper(address, containerName, image);
    }

    public String start() throws Exception {
        String containerId = this.containerRequestHelper.createContainer();
        this.containerRequestHelper.startContainer();
        LOGGER.info("Started the container " + this.containerName);
        return containerId;
    }

    public void stop() throws Exception {
        this.containerRequestHelper.killContainer();
        this.containerRequestHelper.removeContainer();
    }

    public String createExecInstance(String command) throws Exception {
        final String endpoint = String.format("%s/containers/%s/exec", this.address, this.containerName);
        StringEntity body = jsonCreateExecInstance(command);
        String response = this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
        String execId = AppUtil.getValueFromJsonStr("Id", response);
        return execId;
    }

    public void startExecInstance(String execId) throws Exception {
        final String endpoint = String.format("%s/exec/%s/start", this.address, execId);
        StringEntity body = jsonStartExecInstance();
        this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
    }

    public ExecInstanceResult inspectExecInstance(String execId) throws Exception {
        final String endpoint = String.format("%s/exec/%s/json", this.address, execId);
        String response = this.httpWrapper.doRequest(HttpGet.METHOD_NAME, endpoint);
        ExecInstanceResult execInstanceResult = instanceExecResult(response);
        return execInstanceResult;
    }

    private StringEntity jsonCreateExecInstance(String command) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        AppUtil.makeBodyField(jsonObject, "Tty", true);

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

    private ExecInstanceResult instanceExecResult(String response){
        JSONObject jsonObject = new JSONObject(response);
        String execId = jsonObject.getString("ID");
        Integer exitCode = null;
        if(!jsonObject.isNull("ExitCode")){
            exitCode = jsonObject.getInt("ExitCode");
        }
        boolean running = jsonObject.getBoolean("Running");
        ExecInstanceResult execInstanceResult = new ExecInstanceResult(execId, exitCode, running);
        return execInstanceResult;
    }

}
