package org.fogbowcloud.arrebol.execution.k8s.resource;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class DefaultK8sClusterResource implements K8sClusterResource {

	private String id;
	private String address;

	public DefaultK8sClusterResource(String id, String address) {
		this.id = id;
		this.address = address;
	}

	public DefaultK8sClusterResource() {
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getApiAddress() {
		return address;
	}

}
