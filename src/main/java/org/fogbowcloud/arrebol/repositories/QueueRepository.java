package org.fogbowcloud.arrebol.repositories;

import org.fogbowcloud.arrebol.models.queue.DefaultQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueRepository extends JpaRepository<DefaultQueue, String> {

}
