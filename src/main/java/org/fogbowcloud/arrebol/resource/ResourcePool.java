package org.fogbowcloud.arrebol.resource;

import java.util.Collection;

public interface ResourcePool {

    int getId();

    Collection<Resource> getResources();
}
