/* (C)2020 */
package org.fogbowcloud.arrebol.execution.k8s;

import org.fogbowcloud.arrebol.execution.k8s.constants.K8sConstants;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.configuration.Property;

public class K8sConfiguration {
  private final Integer capacity;
  private final String address;
  private final String namespace;
  private final String volumeName;

  public K8sConfiguration(Configuration configuration) throws Exception {
    checkK8sConfigurationProperties(configuration);
    Property<Double> capacity = configuration.getProperty(K8sConstants.K8S_CLUSTER_CAPACITY);
    Property<String> address = configuration.getProperty(K8sConstants.K8S_CLUSTER_ADDRESS);
    Property<String> namespace = configuration.getProperty(K8sConstants.K8S_CLUSTER_NAMESPACE);
    Property<String> volumeName = configuration.getProperty(K8sConstants.K8S_CLUSTER_VOLUME_NAME);
    this.capacity = capacity.getValue().intValue();
    this.address = address.getValue();
    this.namespace = namespace.getValue();
    this.volumeName = volumeName.getValue();
  }

  private void checkK8sConfigurationProperties(Configuration configuration) throws Exception {
    String verifyMsg = " Please, verify your configuration file.";
    String capacityMsg =
        "K8s cluster capacity configuration property wrong or missing." + verifyMsg;
    String addressMsg = "K8s cluster address configuration property wrong or missing." + verifyMsg;
    String namespaceMsg =
        "K8s cluster namespace configuration property wrong or missing." + verifyMsg;

    Property<Double> capacity = configuration.getProperty(K8sConstants.K8S_CLUSTER_CAPACITY);
    Property<String> address = configuration.getProperty(K8sConstants.K8S_CLUSTER_ADDRESS);
    Property<String> namespace = configuration.getProperty(K8sConstants.K8S_CLUSTER_NAMESPACE);

    if (address.getValue() == null || address.getValue().trim().isEmpty()) {
      throw new Exception(addressMsg);
    } else if (capacity.getValue() == null || capacity.getValue().intValue() == 0) {
      throw new Exception(capacityMsg);
    } else if (namespace.getValue() == null || namespace.getValue().trim().isEmpty()) {
      throw new Exception(namespaceMsg);
    }
  }

  public Integer getCapacity() {
    return capacity;
  }

  public String getAddress() {
    return address;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getVolumeName() {
    return volumeName;
  }
}
