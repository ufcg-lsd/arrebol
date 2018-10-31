package org.fogbowcloud.arrebol.core.resource;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;

public interface ResourceSubject {
    void registerObserver(ResourceObserver o);

    void removeObserver(ResourceObserver o);

    void notifyObservers(AbstractResource r);
}
