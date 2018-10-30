package org.fogbowcloud.arrebol.core.models.resource;

import org.fogbowcloud.arrebol.core.models.specification.Specification;

public abstract class AbstractResource {

    private String id;
    private Specification requestedSpecification;
    private ResourceState state;

    public AbstractResource(String id, Specification requestedSpecification) {
        this.id = id;
        this.requestedSpecification = requestedSpecification;
        setState(ResourceState.NOT_READY);
    }

    public abstract boolean match(Specification spec);

    protected abstract boolean internalCheckConnectivity();

    public ResourceState getState() {
        return state;
    }

    public synchronized void setState(ResourceState state) {
        this.state = state;
    }

    public Specification getRequestedSpecification() {
        return this.requestedSpecification;
    }

    public void setRequestedSpecification(Specification requestedSpecification) {
        this.requestedSpecification = requestedSpecification;
    }

    public String getId() {
        return id;
    }

}
