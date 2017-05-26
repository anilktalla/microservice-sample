package com.test.gateway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.consul.ConsulConfiguration;
import org.apache.camel.component.consul.cloud.ConsulServiceDiscovery;
import org.apache.camel.model.cloud.ServiceCallConfigurationDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orbitz.consul.Consul;

@Component
public class GatewayRouteBuilder extends RouteBuilder {

	private static final String SERVICE_DISCOVERY_KEY = "service-discovery-routes";
	private static final String CONSUL_URL = "http://localhost:8500";
	private static final String REMOTE_HOST_HEADER = "REMOTE_HOST";

	@Autowired
	private CamelContext context;

	@Value("${port}")
	private int port;

	private Map<String, String> serviceDiscoveryMap = new HashMap<>();

	@Override
	public void configure() throws Exception {

		loadServiceDiscoveryConfig();

		ConsulConfiguration consul = new ConsulConfiguration(context);
		consul.setUrl(CONSUL_URL);
		ServiceCallConfigurationDefinition config = new ServiceCallConfigurationDefinition();
		config.setComponent("netty4-http");
		config.setServiceDiscovery(new ConsulServiceDiscovery(consul));

		context.setServiceCallConfiguration(config);

		from("netty4-http:http://0.0.0.0:8080/?matchOnUriPrefix=true")
				.dynamicRouter(method(GatewayRouteBuilder.class, "routeDiscovery"));

	}

	public String routeDiscovery(Exchange exchange) {
		String host = getIpAddr(exchange.getIn().getBody(HttpServletRequest.class));

		if (serviceDiscoveryMap.containsKey(host)) {
			return "serviceCall:" + serviceDiscoveryMap.get(host);
		}

		return null;
	}

	private void loadServiceDiscoveryConfig() {
		Optional<com.orbitz.consul.model.kv.Value> value = Consul.builder().withUrl(CONSUL_URL).build().keyValueClient()
				.getValue(SERVICE_DISCOVERY_KEY);

		if (value.isPresent()) {
			List<ServiceDiscoveryRoute> routes = new Gson().fromJson(value.get().getValueAsString().get(),
					new TypeToken<List<ServiceDiscoveryRoute>>() {
					}.getType());
			routes.forEach(item -> serviceDiscoveryMap.put(item.getHost(), item.getServiceName()));
		}

	}

	private static String getIpAddr(HttpServletRequest request) {
		/*String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;*/
		return request.getRemoteAddr();
	}

}
