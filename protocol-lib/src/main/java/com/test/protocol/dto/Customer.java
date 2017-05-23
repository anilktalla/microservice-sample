package com.test.protocol.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String dob;
	private String phone;

}
