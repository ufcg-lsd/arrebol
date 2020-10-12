package org.fogbowcloud.arrebol.datastore.managers;

import org.fogbowcloud.arrebol.datastore.repositories.DefaultQueueRepository;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.fogbowcloud.arrebol.processor.DefaultJobProcessor;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class QueueDBManagerTest {

    private static final String DEFAULT_QUEUE_ID = "default";
    DefaultQueueRepository defaultQueueRepository = mock(DefaultQueueRepository.class);
    DefaultJobProcessor p = mock(DefaultJobProcessor.class);
    QueueDBManager manager = QueueDBManager.getInstance();

    @Test
    public void getJobsByLabel() {
        String labelPattern = "hello-job";
        Map<String, Job> jobs = getJobs();
        Mockito.when(p.getJobs()).thenReturn(jobs);
        Mockito.when(defaultQueueRepository.findOne(DEFAULT_QUEUE_ID)).thenReturn(p);
        manager.setDefaultQueueRepository(defaultQueueRepository);

        List<Job> jobsList = manager.getJobsByLabel(DEFAULT_QUEUE_ID, labelPattern);
        assertTrue(checkJobsLabel(labelPattern, jobsList));
        assertEquals(2, jobsList.size());
    }

    private boolean checkJobsLabel(String label, List<Job> jobs) {
        for (Job j : jobs) {
            if (!j.getLabel().contains(label)) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Job> getJobs() {
        Job job1 = createJob("hello-job-1");
        Job job2 = createJob("hello-job-2");
        Job job3 = createJob("lorem-ipsum");
        Map<String, Job> jobs = new HashMap<>();
        jobs.put(job1.getId(), job1);
        jobs.put(job2.getId(), job2);
        jobs.put(job3.getId(), job3);
        return jobs;
    }

    private Job createJob(String label) {
        Command command1 = new Command("echo \"Hello\"");
        Command command2 = new Command("echo \"World\"");
        List<Command> cmds = Arrays.asList(command1, command2);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("date", "seg out 12 15:29:32 -03 2020");
        Map<String, String> requirements = new HashMap<>();
        requirements.put("image", "test-image");
        TaskSpec ts = new TaskSpec(1L, requirements, cmds, metadata);
        String taskId = "833c6f66-e0c4-40e6-90d9-59f7fcfef54e";
        Task task = new Task(taskId, ts);
        Job job = new Job(label, Collections.singletonList(task));
        return job;
    }
}