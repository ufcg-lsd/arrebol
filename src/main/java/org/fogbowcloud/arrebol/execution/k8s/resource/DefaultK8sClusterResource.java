package org.fogbowcloud.arrebol.execution.k8s.resource;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DefaultK8sClusterResource implements K8sClusterResource {

	@Id
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

	public String getAddress() {
		return address;
	}

}
