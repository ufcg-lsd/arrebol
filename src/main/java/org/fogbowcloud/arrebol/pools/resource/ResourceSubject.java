package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.resource.AbstractResource;

public interface ResourceSubject {
    void registerObserver(ResourceObserver o);

    void removeObserver(ResourceObserver o);

    void notifyObservers(AbstractResource r);
}
