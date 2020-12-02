/* (C)2020 */
package org.fogbowcloud.arrebol.datastore.repositories;

import org.fogbowcloud.arrebol.processor.DefaultJobProcessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultQueueRepository extends JpaRepository<DefaultJobProcessor, String> {}
