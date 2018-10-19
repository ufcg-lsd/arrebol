package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.Resource;

public interface ResourceStateTransitioner {
    void releaseResource(Resource resource);

    void holdResource(Resource resource);

    void putResourceToRemove(Resource resource);
}
