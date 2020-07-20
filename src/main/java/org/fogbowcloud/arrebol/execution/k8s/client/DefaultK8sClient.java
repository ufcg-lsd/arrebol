package org.fogbowcloud.arrebol.execution.k8s.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimVolumeSource;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.util.ClientBuilder;

@Entity
public class DefaultK8sClient implements K8sClient {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String namespace;
	
	@Transient
    private BatchV1Api batchApi;
	
	private String volumeName; 
	
	@Transient
	private final Logger LOGGER = Logger.getLogger(DefaultK8sClient.class);

    public DefaultK8sClient(String host, String namespace, String volumeName) throws IOException {
        ApiClient client = ClientBuilder.defaultClient();
        client.setBasePath(host);
        client.setDebugging(true);
        
        Configuration.setDefaultApiClient(client);
        this.batchApi = new BatchV1Api();
        this.namespace = namespace;
        this.volumeName = volumeName;
    }
    
    public DefaultK8sClient() {}
    
    @Override
    public V1Job createJob(String name, String imageId, String command) throws ApiException {
    	List<String> commandList = buildCommands(command);
    	V1Container podContainer = new V1Container()
    			.name(name)
                .image(imageId)
                .command(commandList);
    		
    	
        V1PodSpec podSpec = new V1PodSpec()
                .restartPolicy("Never")
                .containers(Arrays.asList(
                		podContainer
                	)
                );
        
        if(Objects.nonNull(volumeName)) {
    		podContainer.volumeMounts(Arrays.asList(
            		new V1VolumeMount()
            		.name("k8s-nfs")
            		.mountPath("/nfs")
            		)
    			);
    		
    		podSpec.volumes(Arrays.asList(
        			new V1Volume()
    				.name("k8s-nfs")
    				.persistentVolumeClaim(
    						new V1PersistentVolumeClaimVolumeSource()
    						.claimName(volumeName)
    						)
    				)
    			);
    	}
        
        V1Job job =  new V1Job()
                .apiVersion("batch/v1")
                .kind("Job")
                .metadata(
                        new V1ObjectMeta().name(name))
                .spec(new V1JobSpec()
                        .template(new V1PodTemplateSpec()
                            .metadata(new V1ObjectMeta().labels(Collections.singletonMap("app",name)))
                            .spec(podSpec)
                        ).backoffLimit(2)
                );
        
        return batchApi.createNamespacedJob(namespace, job, null, null, null);
        
    }
    
    private List<String> buildCommands(String command) {
		List<String> commands = new LinkedList<String>();
		commands.add(new String("/bin/sh"));
		commands.add(new String("-c"));
		commands.add(new String(command));
		return commands;
	}

	@Override
    public V1Status deleteJob(String name) throws ApiException {
        return batchApi.deleteNamespacedJob(name, namespace, null, null,  null, null, null, null);    
    }

	@Override
	public V1Job getJob(String name) throws ApiException {
		return batchApi.readNamespacedJob(name, this.namespace, null, null, null);
	}

	public Integer getId() {
		return id;
	}

	public String getNamespace() {
		return namespace;
	}

	public BatchV1Api getBatchApi() {
		return batchApi;
	}

	public String getVolumeName() {
		return volumeName;
	}

	
	
}
