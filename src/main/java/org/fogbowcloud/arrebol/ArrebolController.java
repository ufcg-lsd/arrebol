package org.fogbowcloud.arrebol;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.execution.creator.DockerWorkerCreator;
import org.fogbowcloud.arrebol.execution.creator.RawWorkerCreator;
import org.fogbowcloud.arrebol.execution.creator.WorkerCreator;
import org.fogbowcloud.arrebol.execution.docker.DockerVariable;
import org.fogbowcloud.arrebol.execution.docker.constans.DockerConstants;
import org.fogbowcloud.arrebol.execution.raw.RawConstants;
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
            loadConfigurationFile(path);
            checkConfigurationProperties();
            loadGlobalVariables();
            loadWorkerCreator(this.configuration.getPoolType());
        } catch (FileNotFoundException f) {
            LOGGER.error("Error on loading properties file path=" + path, f);
            System.exit(FAIL_EXIT_CODE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(FAIL_EXIT_CODE);
        }

        String queueId = UUID.randomUUID().toString();
        String queueName = "defaultQueue";

        this.queue = new TaskQueue(queueId, queueName);

        int poolId = 1;
        WorkerPool pool = createPool(configuration, poolId);

        //create the scheduler bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        this.scheduler = new DefaultScheduler(queue, pool, policy);

        this.jobPool = Collections.synchronizedMap(new HashMap<String, Job>());
        this.jobDatabaseCommitter = new Timer(true);
        this.jobStateMonitor = new Timer(true);
    }

    private void checkConfigurationProperties() throws Exception {
        final String verifyMsg = " Please, verify your configuration file.";
        final String imageIdMsg =
            "Docker Image ID configuration property wrong or missing." + verifyMsg;
        final String poolTypeMsg =
            "Worker Pool Type configuration property wrong or missing." + verifyMsg;
        final String resourceAddressesMsg =
            "Docker Image ID configuration property wrong or missing." + verifyMsg;
        final String workerPoolSizeMsg =
            "Docker Image ID configuration property wrong or missing." + verifyMsg;

        String imageId = this.configuration.getImageId();
        String poolType = this.configuration.getPoolType();
        List<String> resourceAddresses = this.configuration.getResourceAddresses();
        Integer workerPoolSize = this.configuration.getWorkerPoolSize();

        if (imageId == null || imageId.trim().isEmpty() || imageId.contains(":")) {
            throw new Exception(imageIdMsg);
        } else if (poolType == null || poolType.trim().isEmpty()) {
            throw new Exception(poolTypeMsg);
        } else if (resourceAddresses == null || resourceAddresses.isEmpty()) {
            throw new Exception(resourceAddressesMsg);
        } else if (workerPoolSize == null || workerPoolSize == 0) {
            throw new Exception(workerPoolSizeMsg);
        }
    }

    private void loadConfigurationFile(String path) throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(
            new FileReader(path + File.separator + "arrebol.json"));
        this.configuration = gson.fromJson(bufferedReader, Configuration.class);
    }

    private void loadGlobalVariables(){
        switch (configuration.getPoolType()) {
            case DockerConstants.DOCKER_TYPE:
                DockerVariable.DEFAULT_IMAGE = configuration.getImageId();
                break;
        }
    }

    private WorkerPool createPool(Configuration configuration, int poolId) {

        //we need to deal with missing/wrong properties

        Collection<Worker> workers = new LinkedList<>(
            workerCreator.createWorkers(poolId, configuration));

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

    private void loadWorkerCreator(String type) throws IOException {
        switch (type) {
            case DockerConstants.DOCKER_TYPE:
                this.workerCreator = new DockerWorkerCreator();
                break;
            case RawConstants.RAW_TYPE:
                this.workerCreator = new RawWorkerCreator();
                break;
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
