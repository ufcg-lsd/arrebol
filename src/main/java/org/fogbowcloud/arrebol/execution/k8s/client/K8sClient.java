package org.fogbowcloud.arrebol.execution.k8s.client;

import org.fogbowcloud.arrebol.execution.k8s.models.K8sJob;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Status;

public interface K8sClient {

	K8sJob createJob(String name, String imageId, String memoryRequest, String cpuRequest, String command) throws ApiException;
    
    V1Status deleteJob(String name) throws ApiException;
    
    K8sJob getJob(String name) throws ApiException;
    
}