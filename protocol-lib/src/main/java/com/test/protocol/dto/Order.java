package com.test.protocol.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private String customerId;
	private String orderId;
	private String quantity;
	private String price;
}
