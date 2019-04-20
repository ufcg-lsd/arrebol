package org.fogbowcloud.arrebol.core.repositories;

import org.fogbowcloud.arrebol.core.models.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    @Transactional
    String deleteById(String id);
}
