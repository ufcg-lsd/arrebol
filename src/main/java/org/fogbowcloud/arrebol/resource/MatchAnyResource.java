package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.models.specification.Specification;

public class MatchAnyResource implements Resource {

    //simple resource that accepts any request

    private ResourceState state;
    private final Specification spec;
    private final String id;
    private final int poolId;

    public MatchAnyResource(Specification spec, String id, int poolId) {
        this.spec = spec;
        this.id = id;
        this.poolId = poolId;
        this.state = ResourceState.IDLE;
    }

    @Override
    public boolean match(Specification spec) {
        return true;
    }

    @Override
    public ResourceState getState() {
        return this.state;
    }

    @Override
    public void setState(ResourceState state) {
        this.state = state;
    }

    @Override
    public Specification getSpecification() {
        return this.spec;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getPoolId() {
        return this.poolId;
    }

    @Override
    public String toString() {
        return "id={" + this.id + "} poolId={" + poolId + "}";
    }
}
