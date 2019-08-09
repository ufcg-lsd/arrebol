package org.fogbowcloud.arrebol.resource;

import java.util.Map;
import org.fogbowcloud.arrebol.models.specification.Specification;

public interface Resource {

    boolean match(Map<String, String> requirements);

    ResourceState getState();

    void setState(ResourceState state);

    Specification getSpecification();

    String getId();

    int getPoolId();
}
