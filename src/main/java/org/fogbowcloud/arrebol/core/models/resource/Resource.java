package org.fogbowcloud.arrebol.core.models.resource;

import org.fogbowcloud.arrebol.core.models.specification.Specification;

public interface Resource {
    Specification getRequestedSpec();
}
