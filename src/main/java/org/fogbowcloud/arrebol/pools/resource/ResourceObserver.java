package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.Resource;

public interface ResourceObserver {
    public void update(Resource resource);
}
