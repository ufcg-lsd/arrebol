package org.fogbowcloud.arrebol;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.*;
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

import java.io.*;
import java.util.*;

@Component
public class ArrebolController {

    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);

    private Configuration configuration;
    private final DefaultScheduler scheduler;
    private final Map<String, Job> jobPool;
    private final TaskQueue queue;

    private final Timer jobDatabaseCommitter;

    private static final int COMMIT_PERIOD_MILLIS = 1000 * 20;//20 seconds

    @Autowired
    private JobRepository jobRepository;

    public ArrebolController() {

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        //Configuration arrebolConfiguration
        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path + File.separator + "arrebol.json"));
            this.configuration = gson.fromJson(bufferedReader, Configuration.class);
            DockerVariable.DEFAULT_IMAGE = this.configuration.getImageId();
        } catch (FileNotFoundException e) {
            LOGGER.error("Error on loading properties file path=" + path, e);
            System.exit(1);
        }

        String queueId = UUID.randomUUID().toString();
        String queueName = "defaultQueue";

        this.queue = new TaskQueue(queueId, queueName);

        int poolId = 1;
        WorkerPool pool = createPool(configuration, poolId);

        //create the scheduler bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        this.scheduler = new DefaultScheduler(queue, pool, policy);

        this.jobPool = Collections.synchronizedMap(new HashMap<String,  Job>());
        this.jobDatabaseCommitter = new Timer(true);
    }

    private static final String RAW_TYPE = "raw";
    private static final String DOCKER_TYPE = "docker";

    private WorkerPool createPool(Configuration configuration, int poolId) {

        //we need to deal with missing/wrong properties

        Collection<Worker> workers = new LinkedList<>();
        populatePool(workers, poolId, configuration);

        WorkerPool pool = new StaticPool(poolId, workers);
        LOGGER.info("pool={" + pool + "} created with workers={" + workers + "}");

        return pool;
    }

    private void populatePool(Collection<Worker> workers, int poolId, Configuration configuration){
        String poolType = configuration.getPoolType();
        if(poolType.equals(DOCKER_TYPE)){
            workers.addAll(createDockerWorkers(poolId, configuration));
        } else if(poolType.equals(RAW_TYPE)){
            workers.addAll(createRawWorkers(poolId, configuration));
        }
    }

    private Collection<Worker> createDockerWorkers(Integer poolId, Configuration configuration){
        Collection<Worker> workers = new LinkedList<>();
        int poolSize = new Integer(configuration.getPoolSize());
        String imageId = configuration.getImageId();
        for(String address : configuration.getWorkers()){
            for (int i = 0; i < poolSize; i++) {
                LOGGER.info("Creating docker worker with address=" + address);
                Worker worker = createDockerWorker(poolId, i, imageId, address);
                workers.add(worker);
            }
        }
        return workers;
    }

    private Worker createDockerWorker(Integer poolId, int resourceId, String imageId, String address){
        TaskExecutor executor = new DockerTaskExecutor(imageId, "docker-executor-" + UUID.randomUUID().toString(), address);
        Specification resourceSpec = null;
        Worker worker = new MatchAnyWorker(resourceSpec, "resourceId-"+resourceId, poolId, executor);
        return worker;
    }

    private Collection<Worker> createRawWorkers(Integer poolId, Configuration configuration){
        Collection<Worker> workers = new LinkedList<>();
        int poolSize = new Integer(configuration.getPoolSize());
        for (int i = 0; i < poolSize; i++) {
            LOGGER.info("Creating raw worker[" + i + "]");
            Worker worker = createRawWorker(poolId, i);
            workers.add(worker);
        }
        return workers;
    }

    private Worker createRawWorker(Integer poolId, int resourceId){
        TaskExecutor executor = new RawTaskExecutor();
        Specification resourceSpec = null;
        Worker worker = new MatchAnyWorker(resourceSpec, "resourceId-"+resourceId, poolId, executor);
        return worker;
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

        for(Task task : job.getTasks()){
            this.queue.addTask(task);
        }

        return job.getId();
    }

    public String stopJob(Job job) {

        for(Task task : job.getTasks()){
         //
        }
        return job.getId();
    }

    public TaskState getTaskState(String taskId) {
        //FIXME:
        return null;
    }


}
