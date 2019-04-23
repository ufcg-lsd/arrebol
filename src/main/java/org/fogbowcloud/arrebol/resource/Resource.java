package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.models.specification.Specification;

public interface Resource {

    boolean match(Specification spec);

    ResourceState getState();

    void setState(ResourceState state);

    Specification getSpecification();

    String getId();

    int getPoolId();
}
