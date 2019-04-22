package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.core.resource.models.Resource;

import java.util.Collection;

public interface ResourcePool {

    int getId();

    Collection<Resource> getResources();
}
