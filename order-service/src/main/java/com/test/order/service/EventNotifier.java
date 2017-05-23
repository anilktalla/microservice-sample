package com.test.order.service;

import java.util.EventObject;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.DefaultFluentProducerTemplate;
import org.apache.camel.component.consul.ConsulConstants;
import org.apache.camel.component.consul.enpoint.ConsulKeyValueActions;
import org.apache.camel.management.event.CamelContextStartedEvent;
import org.apache.camel.management.event.CamelContextStoppingEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.test.protocol.dto.Register;

@Component
public class EventNotifier extends EventNotifierSupport {

	@Value("${port}")
	private int port;

	@Override
	public void notify(EventObject event) throws Exception {
		if (event instanceof CamelContextStartedEvent) {
			CamelContext context = ((CamelContextStartedEvent) event).getContext();
			
			DefaultFluentProducerTemplate.on(context)
		    .withHeader(ConsulConstants.CONSUL_ACTION, ConsulKeyValueActions.PUT)
		    .withHeader(ConsulConstants.CONSUL_KEY, "order")
		    .withBody(register("order" + port, "order", "127.0.0.1", port))
		    .to("direct:consul")
		    .send();
		}
		if (event instanceof CamelContextStoppingEvent) {
			CamelContext context = ((CamelContextStoppingEvent) event).getContext();
			
			DefaultFluentProducerTemplate.on(context)
		    .withHeader(ConsulConstants.CONSUL_ACTION, ConsulKeyValueActions.DELETE_KEY)
		    .withHeader(ConsulConstants.CONSUL_KEY, "order")
		    .to("direct:consul")
		    .send();
		}
	}

	@Override
	public boolean isEnabled(EventObject event) {
		return (event instanceof CamelContextStartedEvent || event instanceof CamelContextStoppingEvent);
	}
	
	private Register register(String id,String name, String address, int port){
		
		Register register = new Register();
		register.setID(id);
		register.setName(name);
		register.setAddress(address);
		register.setPort(port);
		
		return register;
	}

}