package org.fogbowcloud.arrebol.resource;

public interface ResourceObserver {

    void notifyAvailableResource(String resourceId, int poolId);

    void notifyFailedResource(String resourceId, int poolId);
}
