package org.fogbowcloud.arrebol.execution.k8s;

import org.fogbowcloud.arrebol.execution.k8s.constants.K8sConstants;
import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.configuration.Property;

public class K8sConfiguration {
	private final Integer capacity;
    private final String address;
    
    public K8sConfiguration(Configuration configuration) throws Exception {
        checkK8sConfigurationProperties(configuration);
        Property<Double> capacity = configuration.getProperty(K8sConstants.JSON_CLUSTER_CAPACITY_KEY);
        Property<String> address = configuration.getProperty(K8sConstants.JSON_CLUSTER_ADDRESS_KEY);
        this.capacity = capacity.getValue().intValue();
        this.address = address.getValue();
    }

	private void checkK8sConfigurationProperties(Configuration configuration) throws Exception {
		String verifyMsg = " Please, verify your configuration file.";
		String capacityMsg = "K8s cluster capacity configuration property wrong or missing." + verifyMsg;
		String addressMsg = "K8s cluster address configuration property wrong or missing." + verifyMsg;

        Property<Double> capacity = configuration.getProperty(K8sConstants.JSON_CLUSTER_CAPACITY_KEY);
        Property<String> address = configuration.getProperty(K8sConstants.JSON_CLUSTER_ADDRESS_KEY);
        
        if (address.getValue() == null || address.getValue().trim().isEmpty()) {
            throw new Exception(addressMsg);
        } else if (capacity.getValue() == null || capacity.getValue().intValue() == 0) {
            throw new Exception(capacityMsg);
        }
	}

	public Integer getCapacity() {
		return capacity;
	}

	public String getAddress() {
		return address;
	}

}
