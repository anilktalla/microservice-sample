package com.test.order.service;

import java.util.EventObject;

import org.apache.camel.management.event.CamelContextStartedEvent;
import org.apache.camel.management.event.CamelContextStoppingEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;

@Component
public class EventNotifier extends EventNotifierSupport {

	@Value("${port}")
	private int port;

	@Override
	public void notify(EventObject event) throws Exception {
		if (event instanceof CamelContextStartedEvent) {
			//CamelContext context = ((CamelContextStartedEvent) event).getContext();
			
			Registration register = ImmutableRegistration.builder()
	                .id("order-service")
	                .name("order")
	                .address("localhost")
	                .addTags("order")
	                .addTags("key1=value1")
	                .port(8087)
	                .build();
			
			Consul.builder().build().agentClient().register(register);
			
		}
		if (event instanceof CamelContextStoppingEvent) {
			Consul.builder().build().agentClient().deregister("order-service");
		}
	}

	@Override
	public boolean isEnabled(EventObject event) {
		return (event instanceof CamelContextStartedEvent || event instanceof CamelContextStoppingEvent);
	}

}