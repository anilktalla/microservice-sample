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
import org.apache.camel.component.jetty9.JettyHttpComponent9;
import org.apache.camel.model.cloud.ServiceCallConfigurationDefinition;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.SSLContextServerParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orbitz.consul.Consul;
import com.test.gateway.service.security.CustomTrustManagersParameters;

@Component
public class GatewayRouteBuilder extends RouteBuilder {

	private static final String SERVICE_DISCOVERY_KEY = "service-discovery-routes";
	private static final String CONSUL_URL = "http://localhost:8500";

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
		
		configureSSL();

		from("jetty:https://0.0.0.0:8445/?matchOnUriPrefix=true")
			.log("-------Success-----");
				//.dynamicRouter(method(GatewayRouteBuilder.class, "routeDiscovery"));

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
		return request.getRemoteAddr();
	}

	private void configureSSL() {

		SSLContextServerParameters serverParameters = new SSLContextServerParameters();
		serverParameters.setClientAuthentication("REQUIRE");

		KeyStoreParameters ksp = new KeyStoreParameters();
		ksp.setResource("/Users/anilkumartalla/Documents/tmp/ssl-camel/server/keystore.jks");
		ksp.setPassword("changeit");

		KeyManagersParameters kmp = new KeyManagersParameters();
		kmp.setKeyStore(ksp);
		kmp.setKeyPassword("changeit");

		CustomTrustManagersParameters trustManager = new CustomTrustManagersParameters();
		trustManager.setKeyStore(ksp);

		
		SSLContextParameters scp = new SSLContextParameters();
		scp.setKeyManagers(kmp);
		scp.setServerParameters(serverParameters);
		scp.setTrustManagers(trustManager);
		scp.setSecureSocketProtocol("TLSv1.2");

		JettyHttpComponent9 jettyComponent = context.getComponent("jetty", JettyHttpComponent9.class);
		jettyComponent.setSslContextParameters(scp);
	}

}
