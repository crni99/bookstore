package com.crni99.bookstore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crni99.bookstore.model.Book;
import com.crni99.bookstore.repository.BookRepository;

@Service
public class BookService {

	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public Page<Book> findPaginated(Pageable pageable, String term) {

		return page(pageable, term);
	}

	private Page<Book> page(Pageable pageable, String term) {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

		ArrayList<Book> books;
		List<Book> list;

		if (term == null) {
			books = (ArrayList<Book>) bookRepository.findAll();
		} else {
			books = (ArrayList<Book>) bookRepository.findByNameContaining(term);
		}

		if (books.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, books.size());
			list = books.subList(startItem, toIndex);
		}

		Page<Book> bookPage = new PageImpl<Book>(list, PageRequest.of(currentPage, pageSize), books.size());

		return bookPage;
	}

	public void save(Book book) {
		bookRepository.save(book);
	}

	public Optional<Book> findBookById(Long id) {
		Optional<Book> book = bookRepository.findById(id);
		return book;
	}

	public void delete(Long id) {
		bookRepository.deleteById(id);
	}

}
