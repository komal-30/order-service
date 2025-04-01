package com.order.service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.order.service.entity.Order;
import com.order.service.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RestTemplate restTemplate;

	// Get Operation
	@GetMapping("/{id}")
	public Order getOrder(@PathVariable Long id) {
		return orderRepository.findById(id).orElseThrow();
	}

	// Operation
	@PostMapping
	@PreAuthorize("hasRole('order_manager')") // Requires role
	@CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackPlaceOrder")
	public ResponseEntity<String> placeOrder(@RequestBody Order order) {
		// Use Map instead of Book class
		Map<String, Object> bookResponse = restTemplate
				.getForObject("http://inventory-service/api/books/" + order.getBookId(), Map.class);

		Long bookId = ((Number) bookResponse.get("id")).longValue();
		Long userId = ((Number) order.getUserId()).longValue();

		if ((Integer) bookResponse.get("stockQuantity") >= order.getQuantity()) {
			order.setStatus("PLACED");
			order.setBookId(bookId);
			order.setUserId(userId);
			orderRepository.save(order);
			return ResponseEntity.ok("Order placed successfully!");
		} else {
			return ResponseEntity.badRequest().body("Insufficient stock!");
		}
	}

	public ResponseEntity<String> fallbackPlaceOrder(Order order, Exception e) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("Inventory Service unavailable. Please try later.");
	}

	// Update Operation
	@PutMapping("/{id}")
	public ResponseEntity<Order> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
		Order order = orderRepository.findById(id).orElseThrow();
		order.setQuantity(quantity);
		return ResponseEntity.ok(orderRepository.save(order));
	}

	// Delete Operation
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable Long id) {
		if (orderRepository.existsById(id)) {
			orderRepository.deleteById(id);
			return ResponseEntity.ok("Book deleted successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
		}
	}
}