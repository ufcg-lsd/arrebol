package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.infrastructure.FogbowInfraProvider;
import org.fogbowcloud.arrebol.core.models.resource.AbstractResource;
import org.fogbowcloud.arrebol.infrastructure.InfraProvider;
import org.fogbowcloud.arrebol.pools.PoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePoolManager implements ResourceSubject, ResourceStateTransitioner, PoolManager<AbstractResource> {

    private List<ResourceObserver> resourcesObservers;

    private List<AbstractResource> freeResources = new ArrayList<AbstractResource>();
    private Map<String, AbstractResource> resourcePool = new ConcurrentHashMap<String, AbstractResource>();

    private InfraProvider infraProvider;

    public ResourcePoolManager() {
        this.resourcesObservers = new ArrayList<ResourceObserver>();

        this.infraProvider = new FogbowInfraProvider();
        // create request default X resources
    }

    public void registerObserver(ResourceObserver o) {
        this.resourcesObservers.add(o);
    }

    public void removeObserver(ResourceObserver o) {
        this.resourcesObservers.remove(o);
    }

    public void notifyObservers(AbstractResource resource) {
        for (ResourceObserver observer: this.resourcesObservers) {
            observer.update(resource);
        }
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

    @Override
    public void addToPool(AbstractResource resource) {
        this.resourcePool.put(resource.getId(), resource);
        notifyObservers(resource);
    }
}
