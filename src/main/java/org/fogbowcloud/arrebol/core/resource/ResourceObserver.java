package org.fogbowcloud.arrebol.core.resource;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;

public interface ResourceObserver {
    public void update(AbstractResource resource);
}
