package org.fogbowcloud.arrebol.execution.docker.resource;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.docker.constants.DockerConstants;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerImageNotFoundException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerContainerRequestHelper;
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerRequestHelper;

public class DefaultDockerContainerResource implements DockerContainerResource {
    private static final Logger LOGGER = Logger.getLogger(DefaultDockerContainerResource.class);
    private boolean started;
    private String resourceId;
    private String apiAddress;
    private DockerContainerRequestHelper dockerContainerRequestHelper;
    private DockerRequestHelper dockerRequestHelper;

    /**
     * @param apiAddress Defines the address where requests for the Docker API should be made
     * @param resourceId Sets the name of the container, is an identifier.
     */
    public DefaultDockerContainerResource(String resourceId, String apiAddress, DockerContainerRequestHelper dockerContainerRequestHelper,
        DockerRequestHelper dockerRequestHelper) {
        this.resourceId = resourceId;
        this.apiAddress = apiAddress;
        this.dockerContainerRequestHelper = dockerContainerRequestHelper;
        this.dockerRequestHelper = dockerRequestHelper;
        this.started = false;
    }

    @Override
    public void start(ContainerSpecification containerSpecification)
            throws DockerStartException, DockerCreateContainerException,
                    UnsupportedEncodingException {
        if (isStarted()) {
            throw new DockerStartException("Container[" + this.resourceId + "] was already started");
        }
        if (Objects.isNull(containerSpecification)) {
            throw new IllegalArgumentException("ContainerSpecification may be not null.");
        }
        LOGGER.info(
                "Container specification ["
                        + containerSpecification.toString()
                        + "] to container ["
                        + this.resourceId
                        + "]");
        String image = this.setUpImage(containerSpecification.getImageId());
        Map<String, String> containerRequirements =
                this.getDockerContainerRequirements(containerSpecification.getRequirements());
        this.dockerContainerRequestHelper.createContainer(image, containerRequirements);
        this.dockerContainerRequestHelper.startContainer();
        this.started = true;
        LOGGER.info("Started the container " + this.resourceId);
    }

    private String setUpImage(String image) {
        try {
            if (image != null && !image.trim().isEmpty()) {
                LOGGER.info("Using image [" + image + "] to start " + resourceId);
            } else {
                throw new IllegalArgumentException("Image ID may be not null or empty");
            }
            dockerRequestHelper.pullImage(apiAddress, image);
        } catch (Exception e) {
            throw new DockerImageNotFoundException(
                    "Error to pull docker image: " + image + " with error " + e.getMessage());
        }
        return image;
    }

    private Map<String, String> getDockerContainerRequirements(
            Map<String, String> taskRequirements) {
        Map<String, String> mapRequirements = new HashMap<>();
        if (Objects.nonNull(taskRequirements)) {
            String dockerRequirements =
                    taskRequirements.get(DockerConstants.METADATA_DOCKER_REQUIREMENTS);
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
                                "] with value [" + value + "] to container [" + this.resourceId + "]");
                            break;
                        case DockerConstants.DOCKER_CPU_WEIGHT:
                            mapRequirements.put(DockerConstants.JSON_KEY_CPU_SHARES, value);
                            LOGGER.info("Added requirement [" + DockerConstants.JSON_KEY_CPU_SHARES +
                                "] with value [" + value + "] to container [" + this.resourceId + "]");
                            break;
                    }
                }
            }
        }
        return mapRequirements;
    }

    @Override
    public void stop() throws DockerRemoveContainerException {
        if(!isStarted()){
            throw new DockerRemoveContainerException("Container[" + this.resourceId + "] was already stopped");
        }
        this.dockerContainerRequestHelper.removeContainer();
        this.started = false;
    }

    @Override
    public String getId() {
        return this.resourceId;
    }

    @Override
    public String getApiAddress() {
        return this.apiAddress;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
