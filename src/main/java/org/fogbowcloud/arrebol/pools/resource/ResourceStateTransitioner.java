package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.resource.AbstractResource;

public interface ResourceStateTransitioner {
    void releaseResource(AbstractResource resource);

    void holdResource(AbstractResource resource);

    void putResourceToRemove(AbstractResource resource);
}
