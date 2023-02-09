package com.crni99.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import com.crni99.bookstore.model.Book;
import com.crni99.bookstore.model.Customer;
import com.crni99.bookstore.model.CustomerBooks;
import com.crni99.bookstore.service.BillingService;

class OrderControllerTest {

	private BillingService billingService = mock(BillingService.class);
	private OrderController orderController = new OrderController(billingService);

	@Test
	void testSearchBooksWithBlankTerm() {
		String term = "";
		Model model = new BindingAwareModelMap();
		Optional<Integer> page = Optional.empty();
		Optional<Integer> size = Optional.empty();

		String result = orderController.searchOrders(term, model, page, size);

		assertThat(term).isBlank();
		assertThat("redirect:/orders").isEqualTo(result);
	}

	@Test
	void testShowSpecificOrder() {
		Long id = 1L;
		List<CustomerBooks> customerBooks = new ArrayList<>();
		CustomerBooks cb = new CustomerBooks(null, null);
		List<Book> books = new ArrayList<>();
		Book book1 = new Book(1L, "The Lorde of the Rings", new BigDecimal(99.99), "J. R. R. Tolkien",
				"978-0-261-10320-7", "Allen & Unwin", LocalDate.now());
		Book book2 = new Book(2L, "The Da Vinci Code", new BigDecimal(250.89), "Dan Brown", "978-1-612-13028-6",
				"Doubleday", LocalDate.now());
		books.add(book1);
		books.add(book2);
		Customer customer = new Customer();
		customer.setId(id);
		customer.setName("John Doe");
		cb.setCustomer(customer);
		cb.setBooks(books);
		customerBooks.add(cb);
		when(billingService.findOrdersByCustomerId(id)).thenReturn(customerBooks);

		Model model = new BindingAwareModelMap();
		String result = orderController.showSpecificOrder(id, model);

		assertThat("order").isEqualTo(result);
		Customer actualCustomer = (Customer) model.asMap().get("customer");
		assertThat(id).isEqualTo(actualCustomer.getId());
		assertThat("John Doe").isEqualTo(actualCustomer.getName());
		List<Book> actualBooks = (List<Book>) model.asMap().get("books");
		assertThat(2).isEqualByComparingTo(actualBooks.size());
		assertThat("The Lorde of the Rings").isEqualTo(actualBooks.get(0).getName());
		assertThat(new BigDecimal(99.99)).isEqualTo(actualBooks.get(0).getPrice());
		assertThat("J. R. R. Tolkien").isEqualTo(actualBooks.get(0).getAuthors());
		assertThat("978-0-261-10320-7").isEqualTo(actualBooks.get(0).getIsbn());
		assertThat("Allen & Unwin").isEqualTo(actualBooks.get(0).getPublisher());
		assertThat("The Da Vinci Code").isEqualTo(actualBooks.get(1).getName());
		assertThat(new BigDecimal(250.89)).isEqualTo(actualBooks.get(1).getPrice());
		assertThat("Dan Brown").isEqualTo(actualBooks.get(1).getAuthors());
		assertThat("978-1-612-13028-6").isEqualTo(actualBooks.get(1).getIsbn());
		assertThat("Doubleday").isEqualTo(actualBooks.get(1).getPublisher());
	}

	@Test
	void shouldShowSpecificOrderInvalidId() {
		Long id = -1L;
		List<CustomerBooks> customerBooks = new ArrayList<>();
		when(billingService.findOrdersByCustomerId(id)).thenReturn(customerBooks);

		Model model = new BindingAwareModelMap();

		String result = orderController.showSpecificOrder(id, model);

		assertThat("order").isEqualTo(result);
		Customer actualCustomer = (Customer) model.asMap().get("customer");
		assertThat(actualCustomer).isEqualTo(null);
		List<Book> actualBooks = (List<Book>) model.asMap().get("books");
		assertThat(actualBooks).isEqualTo(null);
	}

}