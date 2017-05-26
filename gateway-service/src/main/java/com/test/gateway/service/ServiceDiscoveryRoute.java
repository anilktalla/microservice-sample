package com.test.gateway.service;

import java.io.Serializable;

public class ServiceDiscoveryRoute implements Serializable {

	private static final long serialVersionUID = 1L;

	private String host;
	private String serviceName;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
