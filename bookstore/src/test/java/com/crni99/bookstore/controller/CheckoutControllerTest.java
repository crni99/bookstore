package com.crni99.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crni99.bookstore.model.Book;
import com.crni99.bookstore.model.Customer;
import com.crni99.bookstore.service.BillingService;
import com.crni99.bookstore.service.EmailService;
import com.crni99.bookstore.service.ShoppingCartService;

class CheckoutControllerTest {

	private BillingService billingService = mock(BillingService.class);
	private EmailService emailService = mock(EmailService.class);
	private ShoppingCartService shoppingCartService = mock(ShoppingCartService.class);
	private CheckoutController checkoutController = new CheckoutController(billingService, emailService,
			shoppingCartService);

	@Test
	void shouldReturnCheckout() {
		Model model = mock(Model.class);
		Book book1 = new Book();
		Book book2 = new Book();
		List<Book> cart = new ArrayList<>(Arrays.asList(book1, book2));
		when(shoppingCartService.getCart()).thenReturn(cart);
		when(shoppingCartService.totalPrice()).thenReturn(new BigDecimal(100));
		when(shoppingCartService.getshippingCosts()).thenReturn("10");
		String expectedView = "checkout";

		String result = checkoutController.checkout(model);

		assertThat(expectedView).isEqualTo(result);
		assertThat(2).isEqualTo(cart.size());
		verify(shoppingCartService).getCart();
		verify(shoppingCartService).totalPrice();
		verify(shoppingCartService).getshippingCosts();
	}

	@Test
	void shouldRedirectCartWhenCheckoutBecauseCartIsEmpty() {
		Model model = mock(Model.class);
		List<Book> cart = new ArrayList<>();
		when(shoppingCartService.getCart()).thenReturn(cart);
		when(shoppingCartService.totalPrice()).thenReturn(new BigDecimal(100));
		when(shoppingCartService.getshippingCosts()).thenReturn("10");
		String expectedView = "redirect:/cart";

		String result = checkoutController.checkout(model);

		assertThat(expectedView).isEqualTo(result);
		assertThat(0).isEqualTo(cart.size());
	}

	@Test
	void shouldPlaceOrder() {
		Customer customer = new Customer(1L, "Ognjen", "Andjelic", "Serbia", "NN 10", "Belgrade", "11000", "123",
				"mail@example.com");
		List<Book> cart = new ArrayList<>();
		Book book = new Book(1L, "The Lorde of the Rings", new BigDecimal(100), "J. R. R. Tolkien", "978-0-261-10320-7",
				"Allen & Unwin", LocalDate.now());
		cart.add(book);
		String expectedView = "redirect:/cart";

		when(shoppingCartService.getCart()).thenReturn(cart);
		doNothing().when(billingService).createOrder(customer, cart);
		doNothing().when(emailService).sendEmail(customer.getEmail(), "bookstore - Order Confirmation",
				"Your order has been confirmed.");
		doNothing().when(shoppingCartService).emptyCart();

		RedirectAttributes redirect = mock(RedirectAttributes.class);
		BindingResult bindingResult = new BeanPropertyBindingResult(book, "book");
		String result = checkoutController.placeOrder(customer, bindingResult, redirect);

		assertThat(expectedView).isEqualTo(result);
		verify(shoppingCartService, times(1)).getCart();
		verify(billingService, times(1)).createOrder(customer, cart);
		verify(emailService, times(1)).sendEmail(customer.getEmail(), "bookstore - Order Confirmation",
				"Your order has been confirmed.");
		verify(shoppingCartService, times(1)).emptyCart();
	}
	
	@Test
	void shouldPlaceOrderButResultHasErrors() {
		Customer customer = new Customer();
		List<Book> cart = new ArrayList<>();
		Book book = new Book();
		cart.add(book);
		ObjectError error = new ObjectError("customer","Customer cannot be empty.");
		String expectedView = "/checkout";

		when(shoppingCartService.getCart()).thenReturn(cart);

		RedirectAttributes redirect = mock(RedirectAttributes.class);
		BindingResult bindingResult = new BeanPropertyBindingResult(null, null);
		bindingResult.addError(error);
		String result = checkoutController.placeOrder(customer, bindingResult, redirect);

		assertThat(expectedView).isEqualTo(result);
	}
}
