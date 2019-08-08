package org.fogbowcloud.arrebol.execution.docker.helpers;

import org.apache.http.client.methods.HttpPost;
import org.fogbowcloud.arrebol.execution.docker.request.HttpWrapper;

public class DockerImageRequestHelper {
    private String apiAddress;

    public DockerImageRequestHelper(String apiAddress) {
        this.apiAddress = apiAddress;
    }

    public void pullImage(String imageId) throws Exception {
        final String endpoint =
            String.format("%s/images/create?fromImage=%s:latest", apiAddress, imageId);
        HttpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint);
    }
}
