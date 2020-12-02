/* (C)2020 */
package org.fogbowcloud.arrebol.resource;

import java.util.Collection;
import java.util.LinkedList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.fogbowcloud.arrebol.execution.Worker;

@Entity
public class StaticPool implements WorkerPool {

  // this is a very simple pool implementation: we receive the workers at
  // the construction time, so the pool does not change.
  // also, we do not expose any information about how workers are being used (to that we can help
  // pool growth)

  @Id private int poolId;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = MatchAnyWorker.class)
  private Collection<Worker> workers;

  public StaticPool(int poolId, Collection<Worker> workers) {
    this.poolId = poolId;
    this.workers = workers;
  }

  public StaticPool() {}

  @Override
  public int getId() {
    return this.poolId;
  }

  @Override
  public Collection<Worker> getWorkers() {
    return new LinkedList<Worker>(this.workers);
  }

  @Override
  public void addWorkers(Collection<Worker> workers) {
    this.workers.addAll(workers);
  }

  @Override
  public String toString() {
    return "id={" + getId() + "}";
  }
}
