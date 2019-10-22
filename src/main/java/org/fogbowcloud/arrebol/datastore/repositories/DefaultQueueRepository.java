package org.fogbowcloud.arrebol.datastore.repositories;

import org.fogbowcloud.arrebol.models.queue.DefaultQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultQueueRepository extends JpaRepository<DefaultQueue, String> {

}
