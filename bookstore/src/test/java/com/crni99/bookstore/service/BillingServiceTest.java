package com.crni99.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.crni99.bookstore.model.Book;
import com.crni99.bookstore.model.Customer;
import com.crni99.bookstore.model.CustomerBooks;
import com.crni99.bookstore.model.Order;
import com.crni99.bookstore.repository.BillingRepository;
import com.crni99.bookstore.repository.OrderRepository;

class BillingServiceTest {

	private OrderRepository orderRepository = mock(OrderRepository.class);
	private BillingRepository billingRepository = mock(BillingRepository.class);
	private BillingService billingService = new BillingService(orderRepository, billingRepository);

	@Test
	void findPaginated_shouldReturnPaginatedBooks() {
		String term = "2012-12-12";
		LocalDate date = LocalDate.parse(term);
		
		Customer customer1 = new Customer();
		customer1.setId(1L);
		Book book1 = new Book();
		Order order1 = new Order(1L, LocalDate.now(), customer1, book1);
		Customer customer2 = new Customer();
		customer2.setId(2L);
		Book book2 = new Book();
		Book book3 = new Book();
		Order order2 = new Order(2L, LocalDate.now(), customer2, book2);
		Order order3 = new Order(3L, LocalDate.now(), customer2, book3);
		ArrayList<Order> orders = new ArrayList<>(Arrays.asList(order1, order2, order3));

		Pageable pageable = PageRequest.of(0, 3);

		when(orderRepository.findAll()).thenReturn(orders);

		Page<CustomerBooks> customerBooksPage = billingService.findPaginated(pageable, term);

		verify(orderRepository).findByOrderDate(date);
		assertThat(customerBooksPage).isNotNull();
		assertThat(customerBooksPage.getSize()).isEqualTo(3);
	}
	
	@Test
	void findPaginated_shouldReturnPaginatedBooksWhenTermIsNull() {
		Customer customer1 = new Customer();
		customer1.setId(1L);
		Book book1 = new Book();
		Order order1 = new Order(1L, LocalDate.now(), customer1, book1);
		Customer customer2 = new Customer();
		customer2.setId(2L);
		Book book2 = new Book();
		Book book3 = new Book();
		Order order2 = new Order(2L, LocalDate.now(), customer2, book2);
		Order order3 = new Order(3L, LocalDate.now(), customer2, book3);
		ArrayList<Order> orders = new ArrayList<>(Arrays.asList(order1, order2, order3));

		Pageable pageable = PageRequest.of(0, 3);

		when(orderRepository.findAll()).thenReturn(orders);

		Page<CustomerBooks> customerBooksPage = billingService.findPaginated(pageable, null);

		verify(orderRepository).findAll();
		assertThat(customerBooksPage).isNotNull();
		assertThat(customerBooksPage.getSize()).isEqualTo(3);
	}
	
	@Test
	void shouldCreateOrder() {
		Customer customer = new Customer();
		Book book1 = new Book();
		Book book2 = new Book();
		List<Book> books = Arrays.asList(book1, book2);

		billingService.createOrder(customer, books);

		verify(billingRepository, times(1)).save(customer);

		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
		verify(orderRepository, times(2)).save(captor.capture());
		List<Order> capturedOrders = captor.getAllValues();
		for (Order order : capturedOrders) {
			assertThat(customer).isEqualTo(order.getCustomer());
		}
	}

	@Test
	void shouldFindOrdersByCustomerId() {
		Customer customer1 = new Customer();
		customer1.setId(1L);
		Book book1 = new Book();
		Order order1 = new Order(1L, LocalDate.now(), customer1, book1);

		Customer customer2 = new Customer();
		customer2.setId(2L);
		Book book2 = new Book();
		Book book3 = new Book();
		Order order2 = new Order(2L, LocalDate.now(), customer2, book2);
		Order order3 = new Order(3L, LocalDate.now(), customer2, book3);

		List<Order> orders = Arrays.asList(order1, order2, order3);
		when(orderRepository.findAll()).thenReturn(orders);

		List<CustomerBooks> customerBooks = billingService.findOrdersByCustomerId(2L);
		assertThat(customer2).isEqualTo(customerBooks.get(0).getCustomer());
		assertThat(List.of(book2, book3)).isEqualTo(customerBooks.get(0).getBooks());
	}

}
