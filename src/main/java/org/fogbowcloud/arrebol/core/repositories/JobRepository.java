package org.fogbowcloud.arrebol.core.repositories;

import org.fogbowcloud.arrebol.core.models.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
}
