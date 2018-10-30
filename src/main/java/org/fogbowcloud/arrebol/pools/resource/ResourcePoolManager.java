package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.infrastructure.FogbowInfraProvider;
import org.fogbowcloud.arrebol.core.models.resource.Resource;
import org.fogbowcloud.arrebol.infrastructure.InfraProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePoolManager implements ResourceSubject, ResourceStateTransitioner {

    private List<ResourceObserver> resourcesObservers;

    private List<Resource> freeResources = new ArrayList<Resource>();
    private Map<String, Resource> resourcePool = new ConcurrentHashMap<String, Resource>();

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

    public void notifyObservers(Resource resource) {
        for (ResourceObserver observer: this.resourcesObservers) {
            observer.update(resource);
        }
    }

    @Override
    public void releaseResource(Resource resource) {
        // TODO
    }

    @Override
    public void holdResource(Resource resource) {
        // TODO
    }

    @Override
    public void putResourceToRemove(Resource resource) {
        // TODO: resource has failed
    }
}
