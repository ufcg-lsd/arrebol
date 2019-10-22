package org.fogbowcloud.arrebol.repositories;

import org.fogbowcloud.arrebol.models.queue.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueRepository extends JpaRepository<Queue, String> {

}
