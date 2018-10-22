package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.resource.Resource;

public interface ResourceObserver {
    public void update(Resource resource);
}
