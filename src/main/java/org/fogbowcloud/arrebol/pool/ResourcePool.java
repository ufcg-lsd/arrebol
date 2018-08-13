package org.fogbowcloud.arrebol.pool;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePool {

    private List<Resource>  freeResources = new ArrayList<Resource>();
    private Map<String, Resource> resourcePool = new ConcurrentHashMap<String, Resource>();

    public ResourcePool() {

    }

    public List<Task> getRunningTasks() {
        // TODO
        return null;
    }

    public List<Resource> getFreeResources() {
        return this.freeResources;
    }

}
