package com.crni99.bookstore.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.crni99.bookstore.model.Book;
import com.crni99.bookstore.model.Customer;
import com.crni99.bookstore.model.CustomerBooks;
import com.crni99.bookstore.service.BillingService;

@Controller
@RequestMapping("/orders")
public class OrderController {

	private BillingService billingService;

	public OrderController(BillingService billingService) {
		this.billingService = billingService;
	}

	@GetMapping(value = { "", "/" })
	public String getAllOrders(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		return page(null, model, page, size);
	}

	@GetMapping("/search")
	public String searchOrders(@RequestParam("term") String term, Model model,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		if (term.isBlank()) {
			return "redirect:/orders";
		}
		return page(term, model, page, size);
	}

	@GetMapping("/{id}")
	public String showSpecificOrder(@PathVariable("id") Long id, Model model) {
		List<CustomerBooks> customerBooks = billingService.findOrdersByCustomerId(id);

		Customer customer = null;
		List<Book> books = null;
		for (CustomerBooks c : customerBooks) {
			customer = c.getCustomer();
			books = c.getBooks();
		}
		model.addAttribute("customer", customer);
		model.addAttribute("books", books);
		return "order";
	}

	private String page(@RequestParam("term") String term, Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(10);

		Page<CustomerBooks> orderPage;

		if (term == null) {
			orderPage = billingService.findPaginated(PageRequest.of(currentPage - 1, pageSize), null);
		} else {
			orderPage = billingService.findPaginated(PageRequest.of(currentPage - 1, pageSize), term);
		}
		model.addAttribute("orderPage", orderPage);

		int totalPages = orderPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		return "orders";
	}
}