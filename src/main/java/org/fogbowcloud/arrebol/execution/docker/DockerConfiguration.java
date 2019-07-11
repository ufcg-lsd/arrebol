package org.fogbowcloud.arrebol.execution.docker;

import java.util.List;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.configuration.Property;
import org.fogbowcloud.arrebol.execution.docker.constans.DockerConstants;

public class DockerConfiguration {

    private final Integer workerPoolSize;
    private final String imageId;
    private final List<String> resourceAddresses;

    public DockerConfiguration(Configuration configuration) throws Exception {
        checkDockerConfigurationProperties(configuration);
        Property<Double> workerPoolSize = configuration.getProperty(DockerConstants.JSON_WORKER_PULL_SIZE_KEY);
        Property<String> imageId = configuration.getProperty(DockerConstants.JSON_IMAGE_ID_KEY);
        Property<List<String>> resourceAddresses = configuration.getProperty(DockerConstants.JSON_RESOURCE_ADDRESSES_KEY);
        this.workerPoolSize = workerPoolSize.getValue().intValue();
        this.imageId = imageId.getValue();
        this.resourceAddresses = resourceAddresses.getValue();
        DockerVariable.DEFAULT_IMAGE = imageId.getValue();
    }

    private void checkDockerConfigurationProperties(Configuration configuration) throws Exception {
        String verifyMsg = " Please, verify your configuration file.";
        String imageIdMsg = "Docker Image ID configuration property wrong or missing." + verifyMsg;
        String resourceAddressesMsg = "Resource addresses configuration property wrong or missing." + verifyMsg;
        String workerPoolSizeMsg = "Worker pool size configuration property wrong or missing." + verifyMsg;

        Property<Double> workerPoolSize = configuration.getProperty(DockerConstants.JSON_WORKER_PULL_SIZE_KEY);
        Property<String> imageId = configuration.getProperty(DockerConstants.JSON_IMAGE_ID_KEY);
        Property<List<String>> resourceAddresses = configuration.getProperty(DockerConstants.JSON_RESOURCE_ADDRESSES_KEY);

        if (imageId.getValue() == null || imageId.getValue().trim().isEmpty() || imageId.getValue().contains(":")) {
            throw new Exception(imageIdMsg);
        } else if (resourceAddresses == null || resourceAddresses.getValue().isEmpty()) {
            throw new Exception(resourceAddressesMsg);
        } else if (workerPoolSize.getValue() == null || workerPoolSize.getValue().intValue() == 0) {
            throw new Exception(workerPoolSizeMsg);
        }
    }

    public Integer getWorkerPoolSize() {
        return workerPoolSize;
    }

    public String getImageId() {
        return imageId;
    }

    public List<String> getResourceAddresses() {
        return resourceAddresses;
    }
}
