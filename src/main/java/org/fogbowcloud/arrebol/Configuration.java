package org.fogbowcloud.arrebol;

import java.util.List;

public class Configuration {

    private final String poolType;
    private final Integer workerPoolSize;
    private final String imageId;
    private final List<String> resourceAddresses;

    public Configuration(String poolType, Integer workerPoolSize, String imageId, List<String> resourceAddresses) {
        this.poolType = poolType;
        this.workerPoolSize = workerPoolSize;
        this.imageId = imageId;
        this.resourceAddresses = resourceAddresses;
    }

    public String getPoolType() {
        return poolType;
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
