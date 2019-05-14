package org.fogbowcloud.arrebol.execution.remoteWorker;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.fogbowcloud.arrebol.utils.AppUtil;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ContainerRequestHelper {
    private HttpWrapper httpWrapper;
    private String address;
    private String image;
    private String containerName;

    public ContainerRequestHelper(String address, String containerName, String image) {
        this.httpWrapper = new HttpWrapper();
        this.address = address;
        this.containerName = containerName;
        this.image = image;
    }

    public String createContainer() {
        final String endPoint = String.format("%s/containers/create?name%s", address, containerName);
        String containerId = null;
        try {
            StringEntity body = jsonCreateContainer(image);
            String response = this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endPoint, body);
            containerId = AppUtil.getValueFromJsonStr("Id", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return containerId;
    }

    public String startContainer(){
        final String endpoint = String.format("%s/containers/%s/start", address, containerName);
        String message = null;
        try {
            message = post(endpoint);
        } catch(Exception e){
            e.printStackTrace();
        }
        return message;
    }

    public String killContainer(){
        final String endpoint = String.format("%s/containers/%s/kill", address, containerName);
        String message = null;
        try {
            message = post(endpoint);
        } catch(Exception e){
            e.printStackTrace();
        }
        return message;
    }

    private String post(String endpoint) throws Exception {
        String response = this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint);
        String message = AppUtil.getValueFromJsonStr("message", response);
        return message;
    }

    private StringEntity jsonCreateContainer(String image) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        AppUtil.makeBodyField(jsonObject, "Image", image);
        return new StringEntity(jsonObject.toString());
    }

}
