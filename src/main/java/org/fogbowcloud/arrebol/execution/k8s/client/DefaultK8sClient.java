/* (C)2020 */
package org.fogbowcloud.arrebol.execution.k8s.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimVolumeSource;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.util.ClientBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.k8s.http.HttpWrapper;
import org.fogbowcloud.arrebol.execution.k8s.models.K8sContainer;
import org.fogbowcloud.arrebol.execution.k8s.models.K8sJob;
import org.fogbowcloud.arrebol.execution.k8s.models.K8sJobSpec;
import org.fogbowcloud.arrebol.execution.k8s.models.K8sObjectMeta;
import org.fogbowcloud.arrebol.execution.k8s.models.K8sPodSpec;
import org.fogbowcloud.arrebol.execution.k8s.models.K8sPodTemplateSpec;
import org.fogbowcloud.arrebol.execution.k8s.models.K8sResourceRequirements;

@Entity
public class DefaultK8sClient implements K8sClient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String namespace;

  @Transient private BatchV1Api batchApi;

  private String volumeName;

  @Transient private final Gson gson;

  @Transient private final Logger LOGGER = Logger.getLogger(DefaultK8sClient.class);

  public DefaultK8sClient(String host, String namespace, String volumeName) throws IOException {
    ApiClient client = ClientBuilder.defaultClient();
    client.setBasePath(host);
    client.setDebugging(true);

    Configuration.setDefaultApiClient(client);
    this.batchApi = new BatchV1Api();
    this.namespace = namespace;
    this.volumeName = volumeName;
    this.gson = new GsonBuilder().create();
  }

  public DefaultK8sClient() {
    this.gson = new GsonBuilder().create();
  }

  @Override
  public K8sJob createJob(
      String name, String imageId, String memoryRequest, String cpuRequest, String command)
      throws ApiException {
    List<String> commandList = buildCommands(command);
    K8sContainer podContainer = new K8sContainer().name(name).image(imageId).command(commandList);

    K8sPodSpec podSpec =
        new K8sPodSpec().restartPolicy("Never").containers(Arrays.asList(podContainer));

    if (Objects.nonNull(volumeName)) {
      podContainer.volumeMounts(
          Arrays.asList(new V1VolumeMount().name("k8s-nfs").mountPath("/nfs")));

      podSpec.volumes(
          Arrays.asList(
              new V1Volume()
                  .name("k8s-nfs")
                  .persistentVolumeClaim(
                      new V1PersistentVolumeClaimVolumeSource().claimName(volumeName))));
    }

    if (Objects.nonNull(cpuRequest) && Objects.nonNull(memoryRequest)) {
      final K8sResourceRequirements resources = new K8sResourceRequirements();
      final Map<String, String> requests = new HashMap<>();

      requests.put("cpu", cpuRequest);
      requests.put("memory", memoryRequest);

      final Map<String, String> limits = new HashMap<>();

      limits.put("cpu", cpuRequest);
      limits.put("memory", memoryRequest);

      resources.requests(requests);
      resources.limits(limits);
      podContainer.resources(resources);
    }

    K8sJob job =
        new K8sJob()
            .apiVersion("batch/v1")
            .kind("Job")
            .metadata(new K8sObjectMeta().name(name))
            .spec(
                new K8sJobSpec()
                    .template(
                        new K8sPodTemplateSpec()
                            .metadata(
                                new V1ObjectMeta().labels(Collections.singletonMap("app", name)))
                            .spec(podSpec))
                    .backoffLimit(2));

    StringEntity requestBody;

    try {
      requestBody = makeJSONBody(job);
    } catch (UnsupportedEncodingException e) {
      throw new ApiException("Job is not well formed to built JSON.");
    }

    final String jobEndpoint =
        this.batchApi.getApiClient().getBasePath() + "/apis/batch/v1/namespaces/default/jobs";
    K8sJob resultJob = null;

    try {
      final String jsonResponse =
          HttpWrapper.doRequest(
              HttpPost.METHOD_NAME, jobEndpoint, new LinkedList<Header>(), requestBody);

      LOGGER.debug("Result submit job [" + name + "] :" + jsonResponse);
      resultJob = this.gson.fromJson(jsonResponse, K8sJob.class);

      LOGGER.info("Job was submitted with success to K8s cluster: " + resultJob);
    } catch (Exception e) {
      throw new ApiException("Submit Job to K8s cluster has FAILED: " + e.getMessage());
    }

    return resultJob;
  }

  private StringEntity makeJSONBody(K8sJob job) throws UnsupportedEncodingException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(job);

    LOGGER.info("JSON body: " + json);

    return new StringEntity(json);
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
    return batchApi.deleteNamespacedJob(name, namespace, null, null, null, null, null, null);
  }

  @Override
  public K8sJob getJob(String name) throws ApiException {

    final String jobEndpoint =
        this.batchApi.getApiClient().getBasePath()
            + "/apis/batch/v1/namespaces/default/jobs/"
            + name;
    K8sJob resultJob = null;

    try {
      final String jsonResponse = HttpWrapper.doRequest(HttpGet.METHOD_NAME, jobEndpoint, null);

      LOGGER.debug("Result get job [" + name + "] :" + jsonResponse);
      resultJob = this.gson.fromJson(jsonResponse, K8sJob.class);

      LOGGER.info("Job was getted with success to K8s cluster: " + resultJob);

    } catch (Exception e) {
      throw new ApiException("Get Job to K8s cluster has FAILED: " + e.getMessage());
    }
    return resultJob;
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
