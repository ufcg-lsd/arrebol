package org.fogbowcloud.arrebol.core.resource;

import org.fogbowcloud.arrebol.infrastructure.FogbowInfraProvider;
import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;
import org.fogbowcloud.arrebol.infrastructure.InfraProvider;
import org.fogbowcloud.arrebol.pools.resource.ResourcePool;

import java.util.ArrayList;
import java.util.List;

public class ResourceManager implements ResourceSubject {

    private List<ResourceObserver> resourcesObservers;

    private List<AbstractResource> freeResources = new ArrayList<AbstractResource>();
    private ResourcePool resourcePool;

    private InfraProvider infraProvider;

    public ResourceManager() {
        this.resourcePool = new ResourcePool();
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

    public ResourcePool getResourcePool() {
        return this.resourcePool;
    }

    private void addToPool(AbstractResource resource) {
        if (this.resourcePool.addToPool(resource)) {
            notifyObservers(resource);
        }
    }
}
