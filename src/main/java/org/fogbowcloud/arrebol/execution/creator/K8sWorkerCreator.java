package org.fogbowcloud.arrebol.execution.creator;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.execution.k8s.K8sConfiguration;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.processor.spec.WorkerNode;
import org.fogbowcloud.arrebol.resource.MatchAnyWorker;
import org.fogbowcloud.arrebol.utils.AppUtil;

public class K8sWorkerCreator implements WorkerCreator {

	private final K8sConfiguration configuration;
	private final Logger LOGGER = Logger.getLogger(K8sWorkerCreator.class);
	
	public K8sWorkerCreator(Configuration configuration) throws Exception {
        this.configuration = new K8sConfiguration(configuration);
    }
	
	@Override
	public Collection<Worker> createWorkers(Integer poolId) {
		Collection<Worker> workers = new LinkedList<>();
		String address = configuration.getAddress();
        int poolSize = configuration.getCapacity();
        for (int i = 0; i < poolSize; i++) {
            LOGGER.info("Creating k8s worker with address=" + address);
            Worker worker = createK8sWorker(poolId, address);
            workers.add(worker);
        }
    
        return workers;
	}

	@Override
	//TODO implement this method
	public Collection<Worker> createWorkers(Integer poolId, WorkerNode workerNode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//TODO implement this method
	private Worker createK8sWorker(Integer poolId, String address) {
        Worker worker = new MatchAnyWorker(AppUtil.generateUniqueStringId(), null, poolId, null);
        LOGGER.info("Created K8s Worker [" + worker.getId() + "]");
        return worker;
    }
	
}
