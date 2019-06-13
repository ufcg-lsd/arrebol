package org.fogbowcloud.arrebol.execution.docker.request;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.utils.AppUtil;
import org.json.JSONObject;
import org.fogbowcloud.arrebol.execution.docker.constans.DockerConstants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ContainerRequestHelper {
    private HttpWrapper httpWrapper;
    private String address;
    private String image;
    private String containerName;
    private Map<String, String> requirements;

    private final Logger LOGGER = Logger.getLogger(ContainerRequestHelper.class);

    public ContainerRequestHelper(String address, String containerName) {
        this.httpWrapper = new HttpWrapper();
        this.address = address;
        this.containerName = containerName;
        this.image = image;
        this.requirements = new HashMap<>();
    }

    public String createContainer() throws DockerCreateContainerException, UnsupportedEncodingException {
        final String endPoint = String.format("%s/containers/create?name=%s", address, containerName);
        StringEntity body = jsonCreateContainer();
        String response;
        try {
            response = this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endPoint, body);
        } catch (Exception e){
            throw new DockerCreateContainerException("Could not create container [" + this.containerName + "]: " + e.getMessage());
        }
        LOGGER.debug("Create container ["+ containerName +"] request response: ["+ response +"]");
        String containerId = AppUtil.getValueFromJsonStr("Id", response);
        return containerId;
        //Todo catch and throw or threat possible exceptions


    }

    public void startContainer() throws DockerStartException {
        final String endpoint = String.format("%s/containers/%s/start", address, containerName);

        //Todo catch and throw or threat possible exceptions
        String response;
        try {
            response = post(endpoint);
        } catch (Exception e){
            throw new DockerStartException("Could not start container [" + this.containerName + "]: " + e.getMessage());
        }

        LOGGER.debug("Start container ["+ containerName +"] request response: ["+ response +"]");
    }

    public void removeContainer() throws DockerRemoveContainerException {
        final String endpoint = String.format("%s/containers/%s?force=true", address, containerName);

        //Todo catch and throw or threat possible exceptions
        String response;
        try {
            response = this.httpWrapper.doRequest(HttpDelete.METHOD_NAME, endpoint);
        } catch (Exception e){
            throw new DockerRemoveContainerException("Could not remove container [" + this.containerName + "]: " + e.getMessage());
        }

        LOGGER.debug("Remove container ["+ containerName +"] request response: ["+ response +"]");
    }

    private String post(String endpoint) throws Exception {
        return this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint);
    }

    public void setRequirements(Map<String, String> requirements) {
        this.requirements = requirements;
    }

    protected void setImage(String image){
        this.image = image;
    }

    private StringEntity jsonCreateContainer() throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        AppUtil.makeBodyField(jsonObject, "Image", image);
        AppUtil.makeBodyField(jsonObject, "Tty", true);
        //AppUtil.makeBodyField(jsonObject, "HostConfig", requirements);
        jsonAddRequirements(jsonObject);
        return new StringEntity(jsonObject.toString());
    }

    private void jsonAddRequirements(JSONObject jsonObject){
        JSONObject jsonRequirements = new JSONObject();
        for(Map.Entry<String, String> entry : this.requirements.entrySet()){
            switch(entry.getKey()){
                case DockerConstants.JSON_KEY_MEMORY:
                    Integer memory = Integer.valueOf(entry.getValue()) * 1048576;
                    jsonRequirements.put(DockerConstants.JSON_KEY_MEMORY, memory);
                    break;
                case DockerConstants.JSON_KEY_CPU_SHARES:
                    Integer cpuShares = Integer.valueOf(entry.getValue());
                    jsonRequirements.put(DockerConstants.JSON_KEY_CPU_SHARES, cpuShares);
                    break;
            }
        }
        AppUtil.makeBodyField(jsonObject, "HostConfig", jsonRequirements);
    }

}
