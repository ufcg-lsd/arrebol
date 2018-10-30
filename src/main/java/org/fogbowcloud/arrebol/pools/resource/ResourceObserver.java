package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.resource.AbstractResource;

public interface ResourceObserver {
    public void update(AbstractResource resource);
}
