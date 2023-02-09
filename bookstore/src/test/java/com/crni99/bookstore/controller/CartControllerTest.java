package com.crni99.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crni99.bookstore.model.Book;
import com.crni99.bookstore.service.BookService;
import com.crni99.bookstore.service.ShoppingCartService;

class CartControllerTest {

	private BookService bookService = mock(BookService.class);
	private ShoppingCartService shoppingCartService = mock(ShoppingCartService.class);
	private CartController cartController = new CartController(bookService, shoppingCartService);

	@Test
	void shouldReturnPaginatedShoppingCart() {
		List<Book> cart = new ArrayList<>();
		Model model = mock(Model.class);
		when(shoppingCartService.getCart()).thenReturn(cart);
		String expectedView = "cart";

		String result = cartController.shoppingCart(model);

		assertThat(expectedView).isEqualTo(result);
		verify(model).addAttribute("cart", cart);
		verify(shoppingCartService).getCart();
	}

	@Test
	void shouldAddToCart() {
		Long id = 1L;
		Book book = new Book();
		List<Book> cart = new ArrayList<>();
		HttpSession session = mock(HttpSession.class);
		when(bookService.findBookById(id)).thenReturn(Optional.of(book));
		when(shoppingCartService.getCart()).thenReturn(cart);
		when(shoppingCartService.getSession()).thenReturn(session);
		RedirectAttributes redirect = mock(RedirectAttributes.class);
		String expectedView = "redirect:/cart";

		String result = cartController.addToCart(id, redirect);

		assertThat(expectedView).isEqualTo(result);
		assertThat(1).isEqualTo(cart.size());
		assertThat(book).isEqualTo(cart.get(0));
		verify(redirect).addFlashAttribute("successMessage", "Added book successfully!");
		verify(session).setAttribute("cart", cart);
		verify(bookService).findBookById(id);
		verify(shoppingCartService).getCart();
		verify(shoppingCartService).getSession();
	}

	@Test
	void shouldDeleteBookFromCart() {
		Long bookId = 1L;
		Book book = new Book();
		book.setId(bookId);
		when(bookService.findBookById(bookId)).thenReturn(Optional.of(book));
		RedirectAttributes redirect = mock(RedirectAttributes.class);
		String expectedView = "redirect:/cart";

		String result = cartController.removeFromCart(bookId, redirect);

		assertThat(expectedView).isEqualTo(result);
		verify(redirect).addFlashAttribute("successMessage", "Removed book successfully!");
		verify(bookService).findBookById(bookId);
		verify(shoppingCartService).deleteProductWithId(bookId);
	}

	@Test
	void shouldDeleteAllFromCart() {
		String expectedView = "redirect:/cart";

		String result = cartController.removeAllFromCart();

		assertThat(expectedView).isEqualTo(result);
		assertThat(shoppingCartService.getCart()).isEmpty();
	}

}