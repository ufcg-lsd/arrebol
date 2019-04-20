package org.fogbowcloud.arrebol.resource;

public interface ResourceObserver {

    void resourceAvailable(int resourceId, int poolId);

    void resourceFailed(int resourceId, int poolId);
}
