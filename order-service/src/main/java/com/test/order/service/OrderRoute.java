package com.test.order.service;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.test.protocol.dto.Order;

@Component
public class OrderRoute extends RouteBuilder {
		
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
            .apiProperty("api.title", "Order API").apiProperty("api.version", "1.0")
            .apiProperty("cors", "true");
		
		/* from("direct:consul")
         	.to("consul:agent?url=http://localhost:8500")
             .to("log:camel-consul?level=INFO");*/
		 
		rest("/api/v1")
		 .get("/order").consumes("application/json").description("Find all orders").outType(Order.class)
		 	.to("bean:orderService?method=findAll")
		 .post("/order").consumes("application/json").description("Add new order").type(Order.class).outType(Order.class)
		 	.to("bean:orderService?method=add(${body})");
			
	}
		
}
