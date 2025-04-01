package com.order.service.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name ="orders")
@NoArgsConstructor // ← Add this (required by Hibernate)
@AllArgsConstructor //
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "User ID is required")
	private Long userId;
	
    @NotNull(message = "Book ID is required")
	private Long bookId;
    
    @Positive(message = "Quantity must be at least 1")	
	private int quantity;
    
    @Column(length = 20)
	private String status; // "PLACED", "CANCELLED", "COMPLETED"
	
	
	public Long getId() {
		return id;
	}
	public Long getUserId() {
		return userId;
	}
	public Long getBookId() {
		return bookId;
	}
	public int getQuantity() {
		return quantity;
	}
	public String getStatus() {
		return status;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	// Getters and Setters
}