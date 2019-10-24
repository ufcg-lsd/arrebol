package org.fogbowcloud.arrebol;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.datastore.managers.QueueDBManager;
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
import org.fogbowcloud.arrebol.queue.DefaultQueue;
import org.fogbowcloud.arrebol.queue.Queue;
import org.fogbowcloud.arrebol.queue.QueueManager;
import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.datastore.repositories.JobRepository;
import org.fogbowcloud.arrebol.queue.spec.QueueSpec;
import org.fogbowcloud.arrebol.queue.spec.WorkerNode;
import org.fogbowcloud.arrebol.resource.StaticPool;
import org.fogbowcloud.arrebol.resource.WorkerPool;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;
import org.fogbowcloud.arrebol.scheduler.FifoSchedulerPolicy;
import org.fogbowcloud.arrebol.utils.ConfValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@Component
public class ArrebolController {

    private static final int FAIL_EXIT_CODE = 1;
    private static final String defaultQueueId = "default";
    private static final String defaultQueueName = "defaultQueue";
    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);
    private final Map<String, Job> jobPool;
    private final QueueManager queueManager;
    private Configuration configuration;
    private WorkerCreator workerCreator;
    private Integer poolId;

    public ArrebolController() {
        poolId = 1;
        String path = null;
        try {
             path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource("")).getPath();
            this.configuration = loadConfigurationFile(path);
            ConfValidator.validate(configuration);
            buildWorkerCreator(configuration);
        } catch (FileNotFoundException f) {
            LOGGER.error("Error on loading properties file path=" + path, f);
            System.exit(FAIL_EXIT_CODE);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(FAIL_EXIT_CODE);
        }

        Map<String, Queue> queues = new HashMap<>();
        this.queueManager = new QueueManager(queues);
        this.jobPool = Collections.synchronizedMap(new HashMap<String, Job>());
    }

    private Queue createDefaultQueue(){
//        String queueId = UUID.randomUUID().toString();
        TaskQueue tq = new TaskQueue(defaultQueueId, defaultQueueName);

        WorkerPool pool = createPool(poolId);

        //create the scheduler bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        DefaultScheduler scheduler = new DefaultScheduler(tq, pool, policy);
        DefaultQueue defaultQueue = new DefaultQueue(defaultQueueId, tq, scheduler);
        return defaultQueue;
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
        Queue defaultQueue = createDefaultQueue();
        this.queueManager.addQueue(defaultQueue);
        this.queueManager.startQueue(defaultQueueId);

        //commit the job pool to DB using a COMMIT_PERIOD_MILLIS PERIOD between successive commits
        //(I also specified the delay to the start the fist commit to be COMMIT_PERIOD_MILLIS)

        // TODO: read from bd
    }

    public void stop() {
        // TODO: delete all resources?
    }

    public String addJob(String queue, Job job){
        job.setJobState(JobState.QUEUED);
        this.jobPool.put(job.getId(), job);
        this.queueManager.addJob(queue, job);

        return job.getId();
    }

    public String stopJob(Job job) {

        for (Task task : job.getTasks()) {
            ////still unsuportted
        }
        return job.getId();
    }

    public Job getJob(String queueId, String jobId){
        return this.queueManager.getJob(queueId, jobId);
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

    public String createQueue(QueueSpec queueSpec) {
        Queue queue = createQueueFromSpec(queueSpec);
        this.queueManager.addQueue(queue);
        this.queueManager.startQueue(queue.getId());
        return queue.getId();
    }

    private Queue createQueueFromSpec(QueueSpec queueSpec){
        String queueId = UUID.randomUUID().toString();
        TaskQueue tq = new TaskQueue(queueId, queueSpec.getName());

        poolId++;
        WorkerPool pool = createPool(poolId, queueSpec.getWorkerNodes());

        //create the scheduler bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        DefaultScheduler scheduler = new DefaultScheduler(tq, pool, policy);
        DefaultQueue defaultQueue = new DefaultQueue(queueId, tq, scheduler);
        return defaultQueue;
    }

    private WorkerPool createPool(int poolId, List<WorkerNode> workerNodes){
        Collection<Worker> workers = new LinkedList<>();
        for(WorkerNode workerNode : workerNodes){
            workers.addAll(workerCreator.createWorkers(poolId, workerNode));
        }

        WorkerPool pool = new StaticPool(poolId, workers);
        LOGGER.info("pool={" + pool + "} created with workers={" + workers + "}");

        return pool;
    }
}
