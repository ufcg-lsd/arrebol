package org.fogbowcloud.arrebol.execution.creator;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.execution.k8s.K8sConfiguration;
import org.fogbowcloud.arrebol.execution.k8s.K8sTaskExecutor;
import org.fogbowcloud.arrebol.execution.k8s.client.DefaultK8sClient;
import org.fogbowcloud.arrebol.execution.k8s.client.K8sClient;
import org.fogbowcloud.arrebol.execution.k8s.resource.DefaultK8sClusterResource;
import org.fogbowcloud.arrebol.execution.k8s.resource.K8sClusterResource;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.specification.Specification;
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
		String namespace = configuration.getNamespace();
		String volumeName = configuration.getVolumeName();
		for (int i = 0; i < poolSize; i++) {
			LOGGER.info("Creating k8s worker with address=" + address);
			Worker worker = createK8sWorker(poolId, address, namespace, volumeName);
			workers.add(worker);
		}

		return workers;
	}

	@Override
	// TODO implement this method
	public Collection<Worker> createWorkers(Integer poolId, WorkerNode workerNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private Worker createK8sWorker(Integer poolId, String address, String namespace, String volumeName) {
		String id = "k8s-executor-" + UUID.randomUUID().toString();
		K8sClusterResource k8sClusterResource = new DefaultK8sClusterResource(id, address);
		Specification resourceSpec = null;

		K8sClient k8sClient = null;
		try {
			k8sClient = new DefaultK8sClient(address, namespace, volumeName);
		} catch (IOException e) {
			LOGGER.error("Error while create k8s client in worker [" + id + "]", e);
		}

		K8sTaskExecutor k8sTaskExecutor = new K8sTaskExecutor(k8sClusterResource, k8sClient);
		Worker worker = new MatchAnyWorker(AppUtil.generateUniqueStringId(), resourceSpec, poolId, k8sTaskExecutor);
		LOGGER.info("Created K8s Worker [" + worker.getId() + "]");
		return worker;
	}

}
