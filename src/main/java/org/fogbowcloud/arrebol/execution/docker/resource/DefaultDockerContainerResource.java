package org.fogbowcloud.arrebol.execution.docker.resource;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.constants.DockerConstants;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerImageNotFoundException;
import org.fogbowcloud.arrebol.execution.docker.request.ContainerRequestHelper;
import org.fogbowcloud.arrebol.execution.docker.request.HttpWrapper;

public class DefaultDockerContainerResource implements DockerContainerResource{
    private String containerName;
    private String apiAddress;
    private String defaultImageId;
    private ContainerRequestHelper containerRequestHelper;
    private HttpWrapper httpWrapper;

    private static final Logger LOGGER = Logger.getLogger(DefaultDockerContainerResource.class);

    /**
     * @param apiAddress Defines the address where requests for the Docker API should be made
     * @param containerName Sets the name of the container, is an identifier.
     * @param defaultImageId Image docker used as default if no one is specified in the task.
     */
    public DefaultDockerContainerResource(String containerName, String apiAddress,
                                          String defaultImageId) {
        this.containerName = containerName;
        this.apiAddress = apiAddress;
        this.defaultImageId = defaultImageId;
        this.containerRequestHelper = new ContainerRequestHelper(apiAddress, containerName);
        this.httpWrapper = new HttpWrapper();
    }

    @Override
    public void start(ContainerSpecification containerSpecification) throws UnsupportedEncodingException {
        String image = this.setUpImage(containerSpecification.getImageId());
        Map<String, String> containerRequirements = this.getDockerContainerRequirements(containerSpecification.getRequirements());
        this.containerRequestHelper.createContainer(image, containerRequirements);
        this.containerRequestHelper.startContainer();
        LOGGER.info("Started the container " + this.containerName);
    }

    private String setUpImage(String image) {
        try {
            if (image != null && !image.trim().isEmpty()) {
                LOGGER.info("Using image [" + image + "] to start " + containerName);
            } else {
                image = this.defaultImageId;
                LOGGER.info("Using default image [" + image + "] to start " + containerName);
            }
            this.pullImage(image);
        } catch (Exception e) {
            throw new DockerImageNotFoundException("Error to pull docker image: " + image +
                    " with error " + e.getMessage());
        }
        return image;
    }

    private void pullImage(String imageId) throws Exception {
        final String endpoint = String.format("%s/images/create?fromImage=%s:latest", this.apiAddress, imageId);
        this.httpWrapper.doRequest(HttpPost.METHOD_NAME, endpoint);
    }

    private Map<String, String> getDockerContainerRequirements(Map<String, String> taskRequirements) {
        Map<String, String> mapRequirements = new HashMap<>();
        String dockerRequirements = taskRequirements.get(DockerConstants.METADATA_DOCKER_REQUIREMENTS);
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

    @Override
    public void stop() {
        this.containerRequestHelper.removeContainer();
    }

    @Override
    public String getId() {
        return this.containerName;
    }

    @Override
    public String getApiAddress() {
        return this.apiAddress;
    }
}
