package org.fogbowcloud.arrebol.core.resource.models;

import org.fogbowcloud.arrebol.core.models.specification.Specification;

public class MatchAnyResource implements Resource {

    //simple resource that accepts any request

    private ResourceState state;
    private final Specification spec;
    private final String id;

    public MatchAnyResource(Specification spec, String id) {
        this.spec = spec;
        this.id = id;
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
}
