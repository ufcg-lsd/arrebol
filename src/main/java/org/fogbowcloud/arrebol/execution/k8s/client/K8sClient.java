package org.fogbowcloud.arrebol.execution.k8s.client;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Status;

public interface K8sClient {
    
	//FIXME what does it return?
	//??? createNamespace();
    
    V1Job createJob(String name, String containerImage, String command) throws ApiException;
    
    V1Status deleteJob(String name) throws ApiException;
    
    V1Job getJob(String name) throws ApiException;
    
}