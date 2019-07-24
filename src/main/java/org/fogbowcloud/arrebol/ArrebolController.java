package org.fogbowcloud.arrebol;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.execution.WorkerTypes;
import org.fogbowcloud.arrebol.execution.creator.DockerWorkerCreator;
import org.fogbowcloud.arrebol.execution.creator.RawWorkerCreator;
import org.fogbowcloud.arrebol.execution.creator.WorkerCreator;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.repositories.JobRepository;
import org.fogbowcloud.arrebol.resource.StaticPool;
import org.fogbowcloud.arrebol.resource.WorkerPool;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;
import org.fogbowcloud.arrebol.scheduler.FifoSchedulerPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArrebolController {

    private static final int COMMIT_PERIOD_MILLIS = 1000 * 20;
    private static final int UPDATE_PERIOD_MILLIS = 1000 * 10;
    private static final int FAIL_EXIT_CODE = 1;
    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);
    private final DefaultScheduler scheduler;
    private final Map<String, Job> jobPool;
    private final TaskQueue queue;
    private final Timer jobDatabaseCommitter;
    private final Timer jobStateMonitor;
    private Configuration configuration;
    private WorkerCreator workerCreator;
    @Autowired
    private JobRepository jobRepository;

    public ArrebolController() {

        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
            .getResource("")).getPath();
        try {
            this.configuration = loadConfigurationFile(path);
            buildWorkerCreator(configuration);
        } catch (FileNotFoundException f) {
            LOGGER.error("Error on loading properties file path=" + path, f);
            System.exit(FAIL_EXIT_CODE);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(FAIL_EXIT_CODE);
        }

        String queueId = UUID.randomUUID().toString();
        String queueName = "defaultQueue";

        this.queue = new TaskQueue(queueId, queueName);

        int poolId = 1;
        WorkerPool pool = createPool(poolId);

        //create the scheduler bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        this.scheduler = new DefaultScheduler(queue, pool, policy);

        this.jobPool = Collections.synchronizedMap(new HashMap<String, Job>());
        this.jobDatabaseCommitter = new Timer(true);
        this.jobStateMonitor = new Timer(true);
    }

    private Configuration loadConfigurationFile(String path) throws FileNotFoundException {
        Configuration configuration;
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(
            new FileReader(path + File.separator + "arrebol.json"));
        configuration = gson.fromJson(bufferedReader, Configuration.class);
        return configuration;
    }

    private WorkerPool createPool(int poolId) {

        //we need to deal with missing/wrong properties

        Collection<Worker> workers = new LinkedList<>(workerCreator.createWorkers(poolId));

        WorkerPool pool = new StaticPool(poolId, workers);
        LOGGER.info("pool={" + pool + "} created with workers={" + workers + "}");

        return pool;
    }

    public void start() {

        Thread schedulerThread = new Thread(this.scheduler, "scheduler-thread");
        schedulerThread.start();

        //commit the job pool to DB using a COMMIT_PERIOD_MILLIS PERIOD between successive commits
        //(I also specified the delay to the start the fist commit to be COMMIT_PERIOD_MILLIS)
        this.jobDatabaseCommitter.schedule(new TimerTask() {
                                               public void run() {
                                                   LOGGER.info("Commit job pool to the database");
                                                   jobRepository.save(jobPool.values());
                                               }
                                           }, COMMIT_PERIOD_MILLIS, COMMIT_PERIOD_MILLIS
        );

        this.jobStateMonitor.schedule(new TimerTask() {
                                          public void run() {
                                              LOGGER.info("Updating job states");
                                              for (Job job : jobPool.values()) {
                                                  updateJobState(job);
                                              }
                                          }
                                      }, UPDATE_PERIOD_MILLIS, UPDATE_PERIOD_MILLIS
        );

        // TODO: read from bd
    }

    public void stop() {
        // TODO: delete all resources?
    }

    public String addJob(Job job) {

        job.setJobState(JobState.QUEUED);
        this.jobPool.put(job.getId(), job);

        for (Task task : job.getTasks()) {
            this.queue.addTask(task);
        }

        return job.getId();
    }

    public String stopJob(Job job) {

        for (Task task : job.getTasks()) {
            ////still unsuportted
        }
        return job.getId();
    }

    public TaskState getTaskState(String taskId) {
        //FIXME:
        return null;
    }

    private void buildWorkerCreator(Configuration configuration) throws Exception {
        String poolType = configuration.getPoolType();
        if (poolType.equals(WorkerTypes.DOCKER.getType())) {
            this.workerCreator = new DockerWorkerCreator(configuration);
        } else if (poolType.equals(WorkerTypes.RAW.getType())) {
            this.workerCreator = new RawWorkerCreator(configuration);
        } else {
            String poolTypeMsg = "Worker Pool Type configuration property wrong or missing. Please, verify your configuration file.";
            throw new IllegalArgumentException(poolTypeMsg);
        }
    }

    //The arrebol does not change job state internally, so we need this workaround
    private void updateJobState(Job job) {
        JobState jobState = job.getJobState();
        if (!(jobState.equals(JobState.FAILED) || jobState.equals(JobState.FINISHED))) {
            if (all(job.getTasks(), TaskState.FAILED.getValue())) {
                job.setJobState(JobState.FAILED);
            } else if (all(job.getTasks(),
                TaskState.FINISHED.getValue() + TaskState.FAILED.getValue())) {
                job.setJobState(JobState.FINISHED);
            } else if (all(job.getTasks(), TaskState.PENDING.getValue())) {
                job.setJobState(JobState.QUEUED);
            } else {
                job.setJobState(JobState.RUNNING);
            }
        }
    }

    private boolean all(Collection<Task> tasks, int mask) {
        for (Task t : tasks) {
            if ((t.getState().getValue() & mask) == 0) {
                return false;
            }
        }
        return true;
    }

}
