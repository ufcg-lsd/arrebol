package org.fogbowcloud.arrebol.execution.docker.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.DockerVariable;
import org.fogbowcloud.arrebol.execution.docker.constans.DockerConstants;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.fogbowcloud.arrebol.utils.AppUtil;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerDockerRequestHelper {

    private HttpWrapper httpWrapper;
    private final String address;
    private final String containerName;
    private ContainerRequestHelper containerRequestHelper;

    private final Logger LOGGER = Logger.getLogger(WorkerDockerRequestHelper.class);

    public WorkerDockerRequestHelper(String address, String containerName) {
        this.httpWrapper = new HttpWrapper();
        this.address = address;
        this.containerName = containerName;
        this.containerRequestHelper = new ContainerRequestHelper(address, containerName);
    }

    public String start(TaskSpec taskSpec) throws DockerCreateContainerException, DockerStartException, UnsupportedEncodingException {
        setUpImage(taskSpec);
        Map<String, String> requirements  = setUpContainerRequirements(taskSpec);
        String containerId = this.containerRequestHelper.createContainer(taskSpec.getImage(), requirements);
        this.containerRequestHelper.startContainer();
        LOGGER.info("Started the container " + this.containerName);
        return containerId;
    }

    public void stop() throws DockerRemoveContainerException {
        this.containerRequestHelper.removeContainer();
    }

    public String createExecInstance(String command) throws Exception {
        final String endpoint = String.format("%s/containers/%s/exec", this.address, this.containerName);
        StringEntity body = jsonCreateExecInstance(command);
        String response = this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
        return AppUtil.getValueFromJsonStr("Id", response);
    }

    public void startExecInstance(String execId) throws Exception {
        final String endpoint = String.format("%s/exec/%s/start", this.address, execId);
        StringEntity body = jsonStartExecInstance();
        this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint, body);
    }

    public ExecInstanceResult inspectExecInstance(String execId) throws Exception {
        final String endpoint = String.format("%s/exec/%s/json", this.address, execId);
        String response = this.httpWrapper.doRequest(HttpGet.METHOD_NAME, endpoint);
        return instanceExecResult(response);
    }

    private Map<String, String> setUpContainerRequirements(TaskSpec taskSpec) {
        Map<String, String> mapRequirements = new HashMap<>();
        String dockerRequirements = taskSpec.getSpec().getRequirements().get(DockerConstants.METADATA_DOCKER_REQUIREMENTS);
        if (dockerRequirements != null) {
            String[] requirements = dockerRequirements.split("&&");
            for (String requirement : requirements) {
                String[] req = requirement.split("==");
                String key = req[0].trim();
                String value = req[1].trim();
                switch (key) {
                    case DockerConstants.DOCKER_MEMORY:
                        mapRequirements.put(DockerConstants.JSON_KEY_MEMORY, value);
                        LOGGER.info("Added requirement [" + DockerConstants.JSON_KEY_MEMORY +
                                "] with value [" + value + "] to container [" + this.containerName + "]");
                        break;
                    case DockerConstants.DOCKER_CPU_WEIGHT:
                        mapRequirements.put(DockerConstants.JSON_KEY_CPU_SHARES, value);
                        LOGGER.info("Added requirement [" + DockerConstants.JSON_KEY_CPU_SHARES +
                                "] with value [" + value + "] to container [" + this.containerName + "]");
                        break;
                }
            }
        }
        return mapRequirements;
    }

    private void setUpImage(TaskSpec taskSpec) {
        String image = taskSpec.getImage();
        try {
            if (image != null && !image.trim().isEmpty()) {
                LOGGER.info("Using image [" + image + "] to start " + containerName);
            } else {
                LOGGER.info("Using default image [" + DockerVariable.DEFAULT_IMAGE + "] to start " + containerName);
                image = DockerVariable.DEFAULT_IMAGE;
            }
            this.pullImage(image);
        } catch (Exception e) {
            LOGGER.info("Error to pull docker image: " + image + " for the task spec " + taskSpec.getSpec() +
                    "with error " + e.getMessage());
        }
    }

    public void pullImage(String imageId) throws Exception {
        final String endpoint = String.format("%s/images/create?fromImage=%s:latest", this.address, imageId);
        this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint);
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
        AppUtil.makeBodyField(jsonObject, "Tty", false);
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
        return new ExecInstanceResult(execId, exitCode, running);
    }

    public String getContainerName() {
        return containerName;
    }
}
