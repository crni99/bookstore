package com.crni99.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import com.crni99.bookstore.model.Book;

class ShoppingCartServiceTest {

	private Long ID_1 = 1L;
	private String NAME_1 = "The Lord of the Rings";
	private BigDecimal PRICE_1 = new BigDecimal(99.99);
	private String AUTHORS_1 = "J. R. R. Tolkien";
	private String ISBN_1 = "978-0-261-10320-7";
	private String PUBLISHER_1 = "Allen & Unwin";
	private LocalDate DOB_1 = LocalDate.of(1954, 07, 29);

	private Long ID_2 = 2L;
	private String NAME_2 = "The Da Vinci Code";
	private BigDecimal PRICE_2 = new BigDecimal(250.89);
	private String AUTHORS_2 = "Dan Brown";
	private String ISBN_2 = "0-385-50420-9";
	private String PUBLISHER_2 = "Doubleday";
	private LocalDate DOB_2 = LocalDate.of(2003, 04, 02);

	private BigDecimal shipping = new BigDecimal(6.00);

	private HttpSession session = mock(HttpSession.class);
	private ShoppingCartService shoppingCartService = new ShoppingCartService(session);

	@Test
	void ShouldRetrieveProductsFromCart() {
		Book book1 = new Book(ID_1, NAME_1, PRICE_1, AUTHORS_1, ISBN_1, PUBLISHER_1, DOB_1);
		Book book2 = new Book(ID_2, NAME_2, PRICE_2, AUTHORS_2, ISBN_2, PUBLISHER_2, DOB_2);
		ArrayList<Book> books = new ArrayList<>(Arrays.asList(book1, book2));

		when(shoppingCartService.getCart()).thenReturn(books);
		assertThat(books.size()).isEqualTo(2);
		assertThat(books.get(0)).isSameAs(book1);
	}

	@Test
	void shouldReturnTotalPriceWithShipping() {
		Book book1 = new Book(ID_1, NAME_1, PRICE_1, AUTHORS_1, ISBN_1, PUBLISHER_1, DOB_1);
		Book book2 = new Book(ID_2, NAME_2, PRICE_2, AUTHORS_2, ISBN_2, PUBLISHER_2, DOB_2);
		ArrayList<Book> books = new ArrayList<>(Arrays.asList(book1, book2));
		when(shoppingCartService.getCart()).thenReturn(books);

		BigDecimal totalPriceWithShipping = new BigDecimal(0);
		List<Book> cart = shoppingCartService.getCart();
		for (Book b : cart) {
			totalPriceWithShipping = totalPriceWithShipping.add(b.getPrice());
		}
		totalPriceWithShipping = totalPriceWithShipping.add(shipping);

		BigDecimal expectedTotalPrice = BigDecimal.ZERO;
		expectedTotalPrice = expectedTotalPrice.add(PRICE_1);
		expectedTotalPrice = expectedTotalPrice.add(PRICE_2);
		expectedTotalPrice = expectedTotalPrice.add(shipping);

		assertThat(expectedTotalPrice).isEqualTo(totalPriceWithShipping);
	}

	@Test
	void shouldDeleteProductsFromCart() {
		Book book1 = new Book(ID_1, NAME_1, PRICE_1, AUTHORS_1, ISBN_1, PUBLISHER_1, DOB_1);
		Book book2 = new Book(ID_2, NAME_2, PRICE_2, AUTHORS_2, ISBN_2, PUBLISHER_2, DOB_2);
		ArrayList<Book> books = new ArrayList<>(Arrays.asList(book1, book2));

		when(shoppingCartService.getCart()).thenReturn(books);
		shoppingCartService.emptyCart();
		assertThat(shoppingCartService.getCart()).isEmpty();
	}

	@Test
	void shouldDeleteProductWithIdFromCart() {
		Book book1 = new Book(ID_1, NAME_1, PRICE_1, AUTHORS_1, ISBN_1, PUBLISHER_1, DOB_1);
		Book book2 = new Book(ID_2, NAME_2, PRICE_2, AUTHORS_2, ISBN_2, PUBLISHER_2, DOB_2);
		ArrayList<Book> books = new ArrayList<>(Arrays.asList(book1, book2));

		when(shoppingCartService.getCart()).thenReturn(books);

		shoppingCartService.deleteProductWithId(ID_2);

		List<Book> updatedCart = shoppingCartService.getCart();
		assertThat(updatedCart.contains(book2)).isFalse();
		assertThat(book1).isEqualTo(updatedCart.get(0));
	}

}
