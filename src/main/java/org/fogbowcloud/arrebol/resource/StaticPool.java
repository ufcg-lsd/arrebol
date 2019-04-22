package org.fogbowcloud.arrebol.resource;

import java.util.Collection;
import java.util.LinkedList;

public class StaticPool implements ResourcePool {

    //this is a very simple pool implementation: we receive the workers at
    //the construction time, so the pool does not change.
    //also, we do not expose any information about how resources are being used (to that we can help pool growth)

    private final int poolId;
    private final Collection<Resource> resources;

    public StaticPool(int poolId, Collection<Resource> resources) {
        this.poolId = poolId;
        this.resources = resources;
    }

    @Override
    public int getId() {
        return this.poolId;
    }

    @Override
    public Collection<Resource> getResources() {
        return new LinkedList<Resource>(this.resources);
    }
}
