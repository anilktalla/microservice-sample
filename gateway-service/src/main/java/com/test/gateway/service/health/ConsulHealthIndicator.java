package com.test.gateway.service.health;

import java.util.List;
import java.util.Map;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Self;
import com.ecwid.consul.v1.agent.model.Self.Config;

@Component
public class ConsulHealthIndicator extends AbstractHealthIndicator {

	private ConsulClient consul;

	public ConsulHealthIndicator() {
		this.consul = new ConsulClient();
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		try {
			Response<Self> self = consul.getAgentSelf();
			Config config = self.getValue().getConfig();
			Response<Map<String, List<String>>> services = consul.getCatalogServices(QueryParams.DEFAULT);
			builder.up().withDetail("services", services.getValue())
					.withDetail("advertiseAddress", config.getAdvertiseAddress())
					.withDetail("datacenter", config.getDatacenter()).withDetail("domain", config.getDomain())
					.withDetail("nodeName", config.getNodeName()).withDetail("bindAddress", config.getBindAddress())
					.withDetail("clientAddress", config.getClientAddress());
		} catch (Exception e) {
			builder.down(e);
		}
	}
}
