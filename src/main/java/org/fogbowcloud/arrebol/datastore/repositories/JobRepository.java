/* (C)2020 */
package org.fogbowcloud.arrebol.datastore.repositories;

import javax.transaction.Transactional;
import org.fogbowcloud.arrebol.models.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

  @Transactional
  String deleteById(String id);
}
