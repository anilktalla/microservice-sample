package com.test.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.test.protocol.dto.Order;

@Service
public class OrderService {

	private List<Order> orders = new ArrayList<>();

	public List<Order> findAll() {
		return orders;
	}

	public Order add(Order order) {
		orders.add(order);
		return order;
	}
}
