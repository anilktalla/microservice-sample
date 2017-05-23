package com.test.gateway.service;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.consul.ConsulConfiguration;
import org.apache.camel.component.consul.ConsulRegistry;
import org.apache.camel.component.consul.cloud.ConsulServiceDiscovery;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.cloud.ServiceCallConfigurationDefinition;
import org.apache.camel.model.cloud.ServiceCallServiceDiscoveryConfiguration;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.test.protocol.dto.Order;

@Component
public class GatewayRoute extends RouteBuilder {
		
	@Autowired
	CamelContext context;
	
	@Value("${port}")
	private int port;
			
	@Override
	public void configure() throws Exception { 
		
		JacksonDataFormat format = new JacksonDataFormat();
		format.useList();
		format.setUnmarshalType(Order.class);
		
		restConfiguration()
			.component("netty4-http")
			.bindingMode(RestBindingMode.json)
			.port(port)
			.apiContextPath("/api-doc")
            .apiProperty("api.title", "Gateway Order API").apiProperty("api.version", "1.0")
            .apiProperty("cors", "true");
		
		 
		ConsulConfiguration consul =  new ConsulConfiguration(context);
		consul.setUrl("http://localhost:8500");
		consul.setKey("order");
		 ServiceCallConfigurationDefinition config = new ServiceCallConfigurationDefinition();
		config.setComponent("netty4-http");
		config.setServiceDiscovery(new ConsulServiceDiscovery(consul));
		
		
		context.setServiceCallConfiguration(config);
		
		
		rest("/api/v1")
		 .get("/order").consumes("application/json").description("Find all orders").outType(Order.class)
		 	.route().serviceCall().name("order").defaultLoadBalancer().consulServiceDiscovery().endParent().unmarshal(format).endRest()
		 .post("/order").consumes("application/json").description("Add new order").type(Order.class).outType(Order.class)
			.route().serviceCall("order").unmarshal(format).endRest();
			
		
	}
		
}
