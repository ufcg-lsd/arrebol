package org.fogbowcloud.arrebol.execution.k8s.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1JobSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.ClientBuilder;

@Entity
public class DefaultK8sClient implements K8sClient {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String namespace;
	@Transient
    private BatchV1Api batchApi;

    public DefaultK8sClient(String host, String namespace) throws IOException {
        ApiClient client = ClientBuilder.defaultClient();
        client.setBasePath(host);
        client.setDebugging(true);
        
        Configuration.setDefaultApiClient(client);
        this.batchApi = new BatchV1Api();
    }
    
    @Override
    //TODO organize this method
    public V1JobList listJobs() {
        try {
            return batchApi.listNamespacedJob(namespace, null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api->listNamespacedJob: " + e.toString());
            System.out.println("------ Response body: \n" + e.getResponseBody());
            //System.out.println("------ Stack trace: \n");
            //e.printStackTrace();
        }
        return null;
    }
    
    @Override
    //TODO organize this method
    public V1Job createJob(String name, String containerImage, List<String> command) {       
        
        V1PodSpec podSpec = new V1PodSpec()
                .restartPolicy("Never")
                .containers(Arrays.asList(
                    new V1Container()
                        .name(name)
                        .image(containerImage)
                        .command(command)
                        /*.volumeMounts(Arrays.asList(
                                new V1VolumeMount().mountPath("/nfs").name("nfs-job-test").subPath("/nfs")
                        )*/
                        )
                /*).volumes(Arrays.asList(
                        new V1Volume()
                                .name(volname)
                                .persistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource().claimName(volclaim))
                )*/
                );
        
        V1Job job =  new V1Job()
                .apiVersion("batch/v1")
                .kind("Job")
                .metadata(
                        new V1ObjectMeta().name(name))
                .spec(new V1JobSpec()
                        .template(new V1PodTemplateSpec()
                            .metadata(new V1ObjectMeta().labels(Collections.singletonMap("app",name)))
                            .spec(podSpec)
                        )
                );
        
        try {
            return batchApi.createNamespacedJob(namespace, job, null, null, null);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api->createNamespacedJob: " + e.toString());
            System.out.println("------ Response body: \n" + e.getResponseBody());
            //System.out.println("------ Stack trace: \n");
            //e.printStackTrace();
        }
        return null;
    }
    
    @Override
    //TODO organize this method
    public V1Status deleteJob(String name) {
        String pretty = "pretty_example";  // str | If 'true', then the output is pretty printed. (optional);
        V1DeleteOptions body = new V1DeleteOptions(); // V1DeleteOptions |  (optional);
        try {
            return batchApi.deleteNamespacedJob(name, namespace, pretty, null,  null, null, null, body);    
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api->deleteNamespacedJob: " + e.toString());
            System.out.println("------ Response body: \n" + e.getResponseBody());
            //System.out.println("------ Stack trace: \n");
            //e.printStackTrace();
        }
        return null;
    }

	@Override
	//TODO implement this method
	public V1Job getJob(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
