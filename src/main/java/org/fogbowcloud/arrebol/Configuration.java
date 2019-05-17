package org.fogbowcloud.arrebol;

import java.util.List;

public class Configuration {

    private String poolType;
    private Integer poolSize;
    private String imageId;
    private List<String> workers;

    public Configuration(String poolType, Integer poolSize, String imageId, List<String> workers) {
        this.poolType = poolType;
        this.poolSize = poolSize;
        this.imageId = imageId;
        this.workers = workers;
    }

    public String getPoolType() {
        return poolType;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public String getImageId() {
        return imageId;
    }

    public List<String> getWorkers() {
        return workers;
    }
}
