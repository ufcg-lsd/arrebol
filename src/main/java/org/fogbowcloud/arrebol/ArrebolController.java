package org.fogbowcloud.arrebol;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.DockerTaskExecutor;
import org.fogbowcloud.arrebol.execution.RawTaskExecutor;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobState;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.repositories.JobRepository;
import org.fogbowcloud.arrebol.resource.MatchAnyWorker;
import org.fogbowcloud.arrebol.resource.StaticPool;
import org.fogbowcloud.arrebol.resource.WorkerPool;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;
import org.fogbowcloud.arrebol.scheduler.FifoSchedulerPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class ArrebolController {

    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);

    private final Properties properties;
    private final DefaultScheduler scheduler;
    private final Map<String, Job> jobPool;
    private final TaskQueue queue;

    private final Timer jobDatabaseCommitter;

    private static final int COMMIT_PERIOD_MILLIS = 1000 * 20;//20 seconds

    @Autowired
    private JobRepository jobRepository;

    public ArrebolController(Properties properties) {
        this.properties = properties;

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        Properties arrebolProperties = new Properties();

        try (InputStream input = new FileInputStream(path + File.separator + "arrebol.conf")) {
            arrebolProperties.load(input);
        } catch (IOException ex) {
            LOGGER.error("Error on loading properties file path=" + path, ex);
            System.exit(1);
        }

        String queueId = UUID.randomUUID().toString();
        String queueName = "defaultQueue";

        this.queue = new TaskQueue(queueId, queueName);

        int poolId = 1;
        WorkerPool pool = createPool(arrebolProperties, poolId);

        //create the scheduler bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        this.scheduler = new DefaultScheduler(queue, pool, policy);

        this.jobPool = Collections.synchronizedMap(new HashMap<String,  Job>());
        this.jobDatabaseCommitter = new Timer(true);
    }

    private static final String RAW_TYPE = "raw";
    private static final String DOCKER_TYPE = "docker";

    private WorkerPool createPool(Properties properties, int poolId) {

        //we need to deal with missing/wrong properties

        Collection<Worker> workers = new LinkedList<>();
        String poolType = properties.getProperty("pool.type");

        int poolSize = new Integer(properties.getProperty("pool.size"));
        for (int i = 0; i < poolSize; i++) {
            TaskExecutor executor = createTaskExecutor(poolType, properties);
            Specification resourceSpec = null;
            Worker worker = new MatchAnyWorker(resourceSpec, "resourceId-"+i, poolId, executor);
            workers.add(worker);
        }

        WorkerPool pool = new StaticPool(poolId, workers);
        LOGGER.info("pool={" + pool + "} created with workers={" + workers + "}");

        return pool;
    }

    private TaskExecutor createTaskExecutor(String type, Properties properties) {

        TaskExecutor executor = null;

        if (type.equals(RAW_TYPE)) {
            executor = new RawTaskExecutor();
        } else if (type.equals(DOCKER_TYPE)) {
            String imageId = properties.getProperty("pool.image_id");
            executor = new DockerTaskExecutor(imageId, "docker-executor-" + UUID.randomUUID().toString());
        }

        return executor;
    }

    public void start() {

        Thread schedulerThread = new Thread(this.scheduler, "scheduler-thread");
        schedulerThread.start();

        //commit the job pool to DB using a COMMIT_PERIOD_MILLIS PERIOD between successive commits
        //(I also specified the delay to the start the fist commit to be COMMIT_PERIOD_MILLIS)
        this.jobDatabaseCommitter.schedule(new TimerTask() {
                    public void run() {
                        LOGGER.info("Commit job pool to the database");
                        for(Job job : jobPool.values()) {
                            jobRepository.save(job);
                        }
                    }
                }, COMMIT_PERIOD_MILLIS, COMMIT_PERIOD_MILLIS
        );

        // TODO: read from bd
    }

    public void stop() {
        // TODO: delete all resources?
    }

    public String addJob(Job job) {

        job.setJobState(JobState.READY);
        this.jobPool.put(job.getId(), job);

        for(Task task : job.getTasks().values()){
            this.queue.addTask(task);
        }

        return job.getId();
    }

    public String stopJob(Job job) {

        Map<String, Task> taskMap = job.getTasks();
        for(Task task : taskMap.values()){
         //
        }
        return job.getId();
    }

    public TaskState getTaskState(String taskId) {
        //FIXME:
        return null;
    }
}
