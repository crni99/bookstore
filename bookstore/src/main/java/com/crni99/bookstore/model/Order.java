package com.crni99.bookstore.model;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_date", nullable = false)
	private LocalDate orderDate;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "book_id")
	private Book book;

	public Order() {
	}

	public Order(Long id, LocalDate orderDate, Customer customer, Book book) {
		this.id = id;
		this.orderDate = orderDate;
		this.customer = customer;
		this.book = book;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", orderDate=" + orderDate + ", customer=" + customer + ", book=" + book + "]";
	}

}
