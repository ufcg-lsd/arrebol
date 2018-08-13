package org.fogbowcloud.arrebol.pool;

import org.fogbowcloud.arrebol.core.models.Resource;

public interface ResourceObserver {
    public void update(Resource resource);
}
