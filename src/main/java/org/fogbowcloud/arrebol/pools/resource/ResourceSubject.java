package org.fogbowcloud.arrebol.pools.resource;

import org.fogbowcloud.arrebol.core.models.Resource;

public interface ResourceSubject {
    void registerObserver(ResourceObserver o);

    void removeObserver(ResourceObserver o);

    void notifyObservers(Resource r);
}
