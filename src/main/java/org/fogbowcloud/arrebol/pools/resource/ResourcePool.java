package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;
import org.fogbowcloud.arrebol.pools.Pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePool implements Pool<AbstractResource>, ResourceStateTransitioner {

    private Map<String, AbstractResource> resourcePool;

    public ResourcePool() {
        this.resourcePool = new ConcurrentHashMap<String, AbstractResource>();
    }

    @Override
    public boolean addToPool(AbstractResource resource) {
        if (resource.getId() != null && resource.getRequestedSpecification() != null && resource.getState() != null) {
            this.resourcePool.put(resource.getId(), resource);
            return true;
        }
        return false;
    }

    @Override
    public void releaseResource(AbstractResource resource) {
        // TODO
    }

    @Override
    public void holdResource(AbstractResource resource) {
        // TODO
    }

    @Override
    public void putResourceToRemove(AbstractResource resource) {
        // TODO: resource has failed
    }

}
