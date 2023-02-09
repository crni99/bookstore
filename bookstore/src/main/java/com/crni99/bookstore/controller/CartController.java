package com.crni99.bookstore.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crni99.bookstore.model.Book;
import com.crni99.bookstore.service.BookService;
import com.crni99.bookstore.service.ShoppingCartService;

@Controller
@RequestMapping("/cart")
public class CartController {

	private final BookService bookService;
	private final ShoppingCartService shoppingCartService;

	public CartController(BookService bookService, ShoppingCartService shoppingCartService) {
		this.bookService = bookService;
		this.shoppingCartService = shoppingCartService;
	}

	@GetMapping(value = { "", "/" })
	public String shoppingCart(Model model) {
		model.addAttribute("cart", shoppingCartService.getCart());
		return "cart";
	}

	@GetMapping("/add/{id}")
	public String addToCart(@PathVariable("id") Long id, RedirectAttributes redirect) {
		List<Book> cart = shoppingCartService.getCart();
		Book book = bookService.findBookById(id).get();
		if (book != null) {
			cart.add(book);
		}
		shoppingCartService.getSession().setAttribute("cart", cart);
		redirect.addFlashAttribute("successMessage", "Added book successfully!");
		return "redirect:/cart";
	}

	@GetMapping("/remove/{id}")
	public String removeFromCart(@PathVariable("id") Long id, RedirectAttributes redirect) {
		Book book = bookService.findBookById(id).get();
		if (book != null) {
			shoppingCartService.deleteProductWithId(id);
		}
		redirect.addFlashAttribute("successMessage", "Removed book successfully!");
		return "redirect:/cart";
	}

	@GetMapping("/remove/all")
	public String removeAllFromCart() {
		List<Book> cart = shoppingCartService.getCart();
		cart.removeAll(cart);
		return "redirect:/cart";
	}

}