/* (C)2020 */
package org.fogbowcloud.arrebol.resource;

import java.util.Collection;
import org.fogbowcloud.arrebol.execution.Worker;

public interface WorkerPool {

  int getId();

  Collection<Worker> getWorkers();

  void addWorkers(Collection<Worker> workers);
}
