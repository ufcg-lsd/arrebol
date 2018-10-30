package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.resource.Resource;

public interface ResourceStateTransitioner {
    void releaseResource(Resource resource);

    void holdResource(Resource resource);

    void putResourceToRemove(Resource resource);
}
