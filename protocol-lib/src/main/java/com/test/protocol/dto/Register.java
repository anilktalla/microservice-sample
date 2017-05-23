package com.test.protocol.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Register {

	private String ID;
	private String Name;
	private String Address;
	private int Port;
	
}
