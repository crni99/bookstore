package com.crni99.bookstore.model;

import java.util.List;

public class CustomerBooks {

	private Customer customer;
	private List<Book> books;

	public CustomerBooks(Customer customer, List<Book> books) {
		this.customer = customer;
		this.books = books;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

}