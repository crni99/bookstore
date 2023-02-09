package com.crni99.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

class LoginControllerTest {

	private LoginController LoginController = new LoginController();

	@Test
	void shouldShowLoginPageAndButAuthenticationFail() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(null);

		String result = LoginController.showLoginPage();

		assertThat(result).isEqualTo("login");
	}

	@Test
	void shouldShowLoginPageButAnonymousPresentAndReturnToLoginAgain() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser",
				AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

		String result = LoginController.showLoginPage();

		assertThat(result).isEqualTo("login");
	}

	@Test
	void shouldShowLoginPageAndAfterLoginRedirectToBook() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password",
				AuthorityUtils.createAuthorityList("ROLE_USER")));

		String result = LoginController.showLoginPage();

		assertThat(result).isEqualTo("redirect:/book");
	}

	@Test
	void shouldShowAccessDeniedOrErrorPage() {
		String result = LoginController.showAccessDenied();

		assertThat(result).isEqualTo("error");
	}
}
