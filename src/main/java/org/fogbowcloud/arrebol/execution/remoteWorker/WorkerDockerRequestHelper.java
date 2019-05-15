package org.fogbowcloud.arrebol.execution.remoteWorker;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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

    public WorkerDockerRequestHelper(String address, String containerName, String image) {
        this.httpWrapper = new HttpWrapper();
        this.address = address;
        this.containerName = containerName;
        this.containerRequestHelper = new ContainerRequestHelper(address, containerName, image);
    }

    public String start(){
        String containerId = this.containerRequestHelper.createContainer();
        this.containerRequestHelper.startContainer();
        return containerId;
    }

    public String stop(){
        String message = this.containerRequestHelper.killContainer();
        return message;
    }

    public String createExecInstance(String command) {
        final String endpoint = String.format("%s/containers/%s/exec", this.address, this.containerName);
        String execId = null;
        try {
            StringEntity body = jsonCreateExecInstance(command);
            String response = this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
            execId = AppUtil.getValueFromJsonStr("Id", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return execId;
    }

    public String startExecInstance(String execId) {
        final String endpoint = String.format("%s/exec/%s/start", this.address, execId);
        String message = null;
        try {
            StringEntity body = jsonStartExecInstance();
            String response = this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
            message = AppUtil.getValueFromJsonStr("message", response);
        } catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    public ExecInstanceResult inspectExecInstance(String execId) {
        final String endpoint = String.format("%s/exec/%s/json", this.address, execId);
        ExecInstanceResult execInstanceResult = null;
        try {
            String response = this.httpWrapper.doRequest(HttpGet.METHOD_NAME, endpoint);
            execInstanceResult = instanceExecResult(response);
        } catch(Exception e){
            e.printStackTrace();
        }
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
        Integer exitCode = jsonObject.getInt("ExitCode");
        boolean running = jsonObject.getBoolean("Running");
        ExecInstanceResult execInstanceResult = new ExecInstanceResult(execId, exitCode, running);
        return execInstanceResult;
    }

}
