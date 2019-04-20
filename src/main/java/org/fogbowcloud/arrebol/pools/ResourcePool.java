package org.fogbowcloud.arrebol.pools;

import org.fogbowcloud.arrebol.core.resource.models.Resource;

import java.util.Collection;

public interface ResourcePool {

    Collection<Resource> getResources();
}
